package org.rococo.artists.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.rococo.artists.client.FilesGrpcClient;
import org.rococo.artists.data.ArtistEntity;
import org.rococo.artists.data.ArtistRepository;
import org.rococo.artists.ex.ArtistAlreadyExistsException;
import org.rococo.artists.ex.ArtistNotFoundException;
import org.rococo.artists.mapper.ArtistMapper;
import org.rococo.artists.mapper.PageableMapper;
import org.rococo.artists.specs.ArtistSpecs;
import org.rococo.grpc.artists.*;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.files.ImageGrpcResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ArtistGrpcService extends ArtistsServiceGrpc.ArtistsServiceImplBase {

    private final ArtistRepository artistRepository;
    private final ArtistSpecs artistSpecs;
    private final FilesGrpcClient filesClient;

    @Override
    @Transactional
    public void add(AddArtistGrpcRequest request, StreamObserver<ArtistGrpcResponse> responseObserver) {

        log.info("Add new artist: {}", request.toString());
        artistRepository.findByName(request.getName())
                .ifPresentOrElse(
                        artist -> {
                            throw new ArtistAlreadyExistsException(request.getName());
                        },
                        () -> {
                            var savedArtist = artistRepository.save(
                                    ArtistMapper.fromGrpcRequest(request)
                                            .setCreatedDate(LocalDateTime.now()));
                            filesClient.add(savedArtist.getId(), request.getPhoto());
                            responseObserver.onNext(
                                    ArtistMapper.toGrpcResponse(savedArtist, request.getPhoto()));
                        }
                );

        responseObserver.onCompleted();

    }

    @Override
    @Transactional(readOnly = true)
    public void findById(IdType request, StreamObserver<ArtistGrpcResponse> responseObserver) {

        log.info("Get artist by id: {}", request.getId());

        artistRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        artist -> {
                            var photo = filesClient.findImage(artist.getId())
                                    .orElse(ImageGrpcResponse.getDefaultInstance());
                            responseObserver.onNext(
                                    ArtistMapper.toGrpcResponse(artist, photo.getContent().toStringUtf8()));
                        },
                        () -> {
                            throw new ArtistNotFoundException(UUID.fromString(request.getId()));
                        }
                );

        responseObserver.onCompleted();

    }

    @Override
    @Transactional(readOnly = true)
    public void findByName(NameType request, StreamObserver<ArtistGrpcResponse> responseObserver) {

        var name = request.getName();
        log.info("Get artist by name: {}", name);

        if (name.isEmpty())
            throw new IllegalArgumentException("Artist name must not be empty");

        artistRepository.findByName(request.getName())
                .ifPresentOrElse(
                        artist -> {
                            var photo = filesClient.findImage(artist.getId())
                                    .orElse(ImageGrpcResponse.getDefaultInstance());
                            responseObserver.onNext(
                                    ArtistMapper.toGrpcResponse(artist, photo.getContent().toStringUtf8()));
                        },
                        () -> {
                            throw new ArtistNotFoundException(name);
                        }
                );

        responseObserver.onCompleted();

    }

    @Override
    public void findAllByIds(ArtistsByIdsGrpcRequest request, StreamObserver<ArtistListGrpcResponse> responseObserver) {

        var isOriginalText = request.getOriginalPhoto()
                ? "original"
                : "thumbnail";
        log.info("Find all artists with {} photos by ids: {}", isOriginalText, request.getIds().getIdList());

        var ids = request.getIds().getIdList().stream()
                .map(UUID::fromString)
                .toList();

        var artistEntities = artistRepository.findAllById(ids);
        var artistIds = artistEntities.stream()
                .map(ArtistEntity::getId)
                .distinct()
                .toList();

        var photos = filesClient.findAllByIds(artistIds, request.getOriginalPhoto());
        var photoMap = photos.stream()
                .collect(Collectors.toMap(
                        photo -> UUID.fromString(photo.getEntityId()),
                        photo -> photo));

        var grpcArtists = artistEntities.stream()
                .map(artist ->
                        ArtistMapper.toGrpcResponse(
                                artist,
                                photoMap.getOrDefault(artist.getId(), ImageGrpcResponse.getDefaultInstance()).getContent().toStringUtf8()))
                .toList();

        responseObserver.onNext(ArtistListGrpcResponse.newBuilder()
                .addAllArtists(grpcArtists)
                .build());

        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void findAll(ArtistsFilterGrpcRequest request, StreamObserver<ArtistsGrpcResponse> responseObserver) {

        var isOriginalText = request.getOriginalPhoto()
                ? "original"
                : "thumbnail";
        log.info("Find all artists with {} photos by params: {}", isOriginalText, request);

        var artistsEntities = artistRepository.findAll(
                artistSpecs.findByCriteria(
                        ArtistMapper.fromGrpcFilter(request)),
                PageableMapper.fromPageableGrpc(request.getPageable()));

        var artistIds = artistsEntities.stream()
                .map(ArtistEntity::getId)
                .distinct()
                .toList();

        var photoMap = filesClient.findAllByIds(artistIds, request.getOriginalPhoto()).stream()
                .collect(Collectors.toMap(
                        photo -> UUID.fromString(photo.getEntityId()),
                        photo -> photo.getContent().toStringUtf8()));

        responseObserver.onNext(
                ArtistMapper.toPageGrpc(artistsEntities, photoMap));

        responseObserver.onCompleted();

    }

    @Override
    @Transactional
    public void update(UpdateArtistGrpcRequest request, StreamObserver<ArtistGrpcResponse> responseObserver) {

        log.info("Update artist: {}", request);

        artistRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        artist -> {
                            artistRepository.findByName(request.getName())
                                    .ifPresent(artistWithSameName -> {
                                        if (!artistWithSameName.getId().equals(artist.getId()))
                                            throw new ArtistAlreadyExistsException(artist.getName());
                                    });

                            var existPhoto = filesClient.findImage(artist.getId());
                            if (existPhoto.isPresent() && !request.getPhoto().isEmpty()) {
                                filesClient.update(artist.getId(), request.getPhoto());
                            } else if (existPhoto.isPresent() && request.getPhoto().isEmpty()) {
                                filesClient.delete(artist.getId());
                            } else if (existPhoto.isEmpty() && !request.getPhoto().isEmpty()) {
                                filesClient.add(artist.getId(), request.getPhoto());
                            }

                            responseObserver.onNext(
                                    ArtistMapper.toGrpcResponse(
                                            artistRepository.save(
                                                    ArtistMapper.updateFromGrpcRequest(artist, request)),
                                            request.getPhoto()));
                        },
                        () -> {
                            throw new ArtistNotFoundException(UUID.fromString(request.getId()));
                        }
                );

        responseObserver.onCompleted();

    }

    @Override
    @Transactional
    public void removeById(IdType request, StreamObserver<Empty> responseObserver) {

        log.info("Delete artist by id: {}", request.getId());

        var id = UUID.fromString(request.getId());
        artistRepository.deleteById(id);
        filesClient.delete(id);

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();

    }

}
