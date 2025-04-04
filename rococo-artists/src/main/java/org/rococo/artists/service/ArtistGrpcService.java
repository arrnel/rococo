package org.rococo.artists.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.rococo.artists.data.ArtistRepository;
import org.rococo.artists.ex.ArtistAlreadyExistException;
import org.rococo.artists.ex.ArtistNotFoundException;
import org.rococo.artists.mapper.ArtistMapper;
import org.rococo.artists.specs.ArtistSpecs;
import org.rococo.grpc.artists.*;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ArtistGrpcService extends ArtistsServiceGrpc.ArtistsServiceImplBase {

    private final ArtistRepository artistRepository;
    private final ArtistSpecs artistSpecs;

    @Override
    public void add(AddArtistGrpcRequest request, StreamObserver<ArtistGrpcResponse> responseObserver) {

        log.info("Add new artist: {}", request.toString());

        artistRepository.findByName(request.getName())
                .ifPresentOrElse(
                        artist -> {
                            throw new ArtistAlreadyExistException(request.getName());
                        },
                        () -> responseObserver.onNext(
                                ArtistMapper.toGrpcResponse(
                                        artistRepository.save(
                                                ArtistMapper.fromGrpcRequest(request))))
                );

        responseObserver.onCompleted();

    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = ArtistNotFoundException.class)
    public void findById(IdType request, StreamObserver<ArtistGrpcResponse> responseObserver) {

        log.info("Get artist by id: {}", request.getId());

        artistRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        artist -> responseObserver.onNext(
                                ArtistMapper.toGrpcResponse(artist)),
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
                        artist -> responseObserver.onNext(
                                ArtistMapper.toGrpcResponse(artist)),
                        () -> {
                            throw new ArtistNotFoundException(name);
                        }
                );

        responseObserver.onCompleted();

    }

    @Override
    @Transactional(readOnly = true)
    public void findAll(ArtistsFilterGrpcRequest request, StreamObserver<ArtistsGrpcResponse> responseObserver) {

        log.info("Find artists by params: {}", request);

        responseObserver.onNext(
                ArtistMapper.toPageGrpc(
                        artistRepository.findAll(
                                artistSpecs.findByCriteria(ArtistMapper.fromGrpcFilter(request)),
                                ArtistMapper.fromPageableGrpc(request.getPageable()))
                ));

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
                                            throw new ArtistAlreadyExistException(artist.getName());
                                    });
                            responseObserver.onNext(
                                    ArtistMapper.toGrpcResponse(
                                            artistRepository.save(
                                                    ArtistMapper.updateFromGrpcRequest(artist, request))));
                        },
                        () -> {
                            throw new ArtistNotFoundException(UUID.fromString(request.getId()));
                        }
                );

        responseObserver.onCompleted();

    }

    @Override
    public void removeById(IdType request, StreamObserver<Empty> responseObserver) {

        log.info("Delete artist by id: {}", request.getId());

        artistRepository.findById(UUID.fromString(request.getId()))
                .ifPresent(artistRepository::delete);

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();

    }

}
