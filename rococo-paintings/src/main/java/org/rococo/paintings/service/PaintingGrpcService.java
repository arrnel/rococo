package org.rococo.paintings.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.rococo.grpc.artists.ArtistGrpcResponse;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.files.ImageGrpcResponse;
import org.rococo.grpc.museums.MuseumGrpcResponse;
import org.rococo.grpc.paintings.*;
import org.rococo.paintings.client.ArtistsGrpcClient;
import org.rococo.paintings.client.FilesGrpcClient;
import org.rococo.paintings.client.MuseumsGrpcClient;
import org.rococo.paintings.data.PaintingEntity;
import org.rococo.paintings.data.PaintingRepository;
import org.rococo.paintings.ex.ArtistNotFoundException;
import org.rococo.paintings.ex.MuseumNotFoundException;
import org.rococo.paintings.ex.PaintingAlreadyExistsException;
import org.rococo.paintings.ex.PaintingNotFoundException;
import org.rococo.paintings.mapper.PageableMapper;
import org.rococo.paintings.mapper.PaintingMapper;
import org.rococo.paintings.specs.PaintingSpecs;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class PaintingGrpcService extends PaintingsServiceGrpc.PaintingsServiceImplBase {

    private final ArtistsGrpcClient artistsClient;
    private final MuseumsGrpcClient museumsClient;
    private final FilesGrpcClient filesClient;

    private final PaintingRepository paintingRepository;
    private final PaintingSpecs paintingSpecs;

    @Override
    @Transactional
    public void add(AddPaintingGrpcRequest request, StreamObserver<PaintingGrpcResponse> responseObserver) {

        log.info("Add new painting: {}", request);

        paintingRepository.findByTitle(request.getTitle())
                .ifPresentOrElse(
                        museum -> {
                            throw new PaintingAlreadyExistsException(request.getTitle());
                        },
                        () -> {
                            var artistId = UUID.fromString(request.getArtistId());
                            var artist = artistsClient.findById(artistId)
                                    .orElseThrow(() -> new ArtistNotFoundException(artistId));
                            var museumId = UUID.fromString(request.getMuseumId());
                            var museum = museumsClient.findById(museumId)
                                    .orElseThrow(() -> new MuseumNotFoundException(museumId));

                            var painting = paintingRepository.save(
                                    PaintingMapper.fromGrpcRequest(request));
                            filesClient.add(painting.getId(), request.getPhoto());

                            responseObserver.onNext(
                                    PaintingMapper.toGrpcResponse(
                                            painting,
                                            artist,
                                            museum,
                                            request.getPhoto()));
                            responseObserver.onCompleted();
                        });


    }

    @Override
    @Transactional(readOnly = true)
    public void findById(IdType request, StreamObserver<PaintingGrpcResponse> responseObserver) {

        log.info("Find painting by id: {}", request.getId());

        paintingRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        painting -> {
                            var artist = artistsClient.findById(painting.getArtistId()).orElse(ArtistGrpcResponse.getDefaultInstance());
                            var museum = museumsClient.findById(painting.getMuseumId()).orElse(MuseumGrpcResponse.getDefaultInstance());
                            var photo = filesClient.findImage(painting.getId()).orElse(ImageGrpcResponse.getDefaultInstance());
                            responseObserver.onNext(PaintingMapper.toGrpcResponse(painting, artist, museum, photo.getContent().toStringUtf8()));
                            responseObserver.onCompleted();
                        },
                        () -> {
                            throw new PaintingNotFoundException(UUID.fromString(request.getId()));
                        }
                );

    }

    @Override
    @Transactional(readOnly = true)
    public void findByTitle(NameType request, StreamObserver<PaintingGrpcResponse> responseObserver) {

        log.info("Find painting by title: {}", request.getName());

        paintingRepository.findByTitle(request.getName())
                .ifPresentOrElse(
                        painting -> {
                            var artist = artistsClient.findById(painting.getArtistId()).orElse(ArtistGrpcResponse.getDefaultInstance());
                            var museum = museumsClient.findById(painting.getMuseumId()).orElse(MuseumGrpcResponse.getDefaultInstance());
                            var photo = filesClient.findImage(painting.getId()).orElse(ImageGrpcResponse.getDefaultInstance());
                            responseObserver.onNext(PaintingMapper.toGrpcResponse(painting, artist, museum, photo.getContent().toStringUtf8()));
                            responseObserver.onCompleted();
                        },
                        () -> {
                            throw new PaintingNotFoundException(request.getName());
                        }
                );

    }

    @Override
    @Transactional(readOnly = true)
    public void findAll(PaintingsFilterGrpcRequest request, StreamObserver<PaintingsGrpcResponse> responseObserver) {

        var isOriginalText = request.getOriginalPhoto()
                ? "original"
                : "thumbnail";
        log.info("Find all paintings with {} photos by params: {}", isOriginalText, request);

        var paintingEntities = paintingRepository.findAll(
                paintingSpecs.findByCriteria(
                        PaintingMapper.fromGrpcFilter(request)),
                PageableMapper.fromPageableGrpc(request.getPageable()));

        var paintingIds = paintingEntities.stream()
                .map(PaintingEntity::getId)
                .distinct()
                .toList();

        var artistIds = paintingEntities.stream()
                .map(PaintingEntity::getArtistId)
                .distinct()
                .toList();

        var museumIds = paintingEntities.stream()
                .map(PaintingEntity::getMuseumId)
                .distinct()
                .toList();

        var artistMap = artistsClient.findAllByIds(artistIds).stream()
                .collect(Collectors.toMap(
                        artist -> UUID.fromString(artist.getId()),
                        artist -> artist));

        var museumMap = museumsClient.findAllByIds(museumIds).stream()
                .collect(Collectors.toMap(
                        artist -> UUID.fromString(artist.getId()),
                        artist -> artist));

        var photoMap = filesClient.findAllByIds(paintingIds, request.getOriginalPhoto()).stream()
                .collect(Collectors.toMap(
                        photo -> UUID.fromString(photo.getEntityId()),
                        photo -> photo.getContent().toStringUtf8()));

        responseObserver.onNext(
                PaintingMapper.toPageGrpc(
                        paintingRepository.findAll(
                                paintingSpecs.findByCriteria(
                                        PaintingMapper.fromGrpcFilter(request)),
                                PageableMapper.fromPageableGrpc(request.getPageable())),
                        artistMap,
                        museumMap,
                        photoMap
                ));

        responseObserver.onCompleted();

    }

    @Override
    @Transactional
    public void update(UpdatePaintingGrpcRequest request, StreamObserver<PaintingGrpcResponse> responseObserver) {

        log.info("Update painting: {}", request);

        paintingRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        painting -> {
                            paintingRepository.findByTitle(request.getTitle())
                                    .ifPresent(m -> {
                                        if (!m.getId().equals(painting.getId()))
                                            throw new PaintingAlreadyExistsException(request.getTitle());
                                    });

                            var artistId = UUID.fromString(request.getArtistId());
                            var museumId = UUID.fromString(request.getMuseumId());
                            var artist = artistsClient.findById(artistId)
                                    .orElseThrow(() -> new ArtistNotFoundException(artistId));
                            var museum = museumsClient.findById(museumId)
                                    .orElseThrow(() -> new MuseumNotFoundException(museumId));

                            var updatedPainting = paintingRepository.save(
                                    PaintingMapper.updateFromGrpcRequest(painting, request));

                            var existPhoto = filesClient.findImage(painting.getId());
                            if (existPhoto.isPresent() && !request.getPhoto().isEmpty()) {
                                filesClient.update(painting.getId(), request.getPhoto());
                            } else if (existPhoto.isPresent() && request.getPhoto().isEmpty()) {
                                filesClient.delete(painting.getId());
                            } else if (existPhoto.isEmpty() && !request.getPhoto().isEmpty()) {
                                filesClient.add(painting.getId(), request.getPhoto());
                            }

                            responseObserver.onNext(
                                    PaintingMapper.toGrpcResponse(
                                            updatedPainting,
                                            artist,
                                            museum,
                                            request.getPhoto()
                                    ));
                            responseObserver.onCompleted();
                        },
                        () -> {
                            throw new PaintingNotFoundException(UUID.fromString(request.getId()));
                        }
                );

    }

    @Override
    @Transactional
    public void removeById(IdType request, StreamObserver<Empty> responseObserver) {

        log.info("Find painting by id: {}", request.getId());

        paintingRepository.findById(UUID.fromString(request.getId()))
                .ifPresent(painting -> {
                    filesClient.delete(painting.getId());
                    paintingRepository.delete(painting);
                });

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();

    }

}
