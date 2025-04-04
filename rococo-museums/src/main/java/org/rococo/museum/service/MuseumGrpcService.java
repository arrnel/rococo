package org.rococo.museum.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.IdsType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.museums.*;
import org.rococo.museum.data.MuseumRepository;
import org.rococo.museum.ex.MuseumAlreadyExistException;
import org.rococo.museum.ex.MuseumNotFoundException;
import org.rococo.museum.mapper.MuseumMapper;
import org.rococo.museum.specs.MuseumSpecs;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class MuseumGrpcService extends MuseumsServiceGrpc.MuseumsServiceImplBase {

    private final MuseumRepository museumRepository;
    private final MuseumSpecs museumSpecs;

    @Override
    public void add(AddMuseumGrpcRequest request, StreamObserver<MuseumGrpcResponse> responseObserver) {

        log.info("Find all countries by params: {}", request);

        log.info("Country Id UUID: {}", request.getCountryId());
        museumRepository.findByTitle(request.getTitle())
                .ifPresent(museum -> {
                    throw new MuseumAlreadyExistException(request.getTitle());
                });

        responseObserver.onNext(
                MuseumMapper.toGrpcResponse(
                        museumRepository.save(
                                MuseumMapper.fromGrpcRequest(request))));

        responseObserver.onCompleted();

    }

    @Override
    public void findById(IdType request, StreamObserver<MuseumGrpcResponse> responseObserver) {

        log.info("Find museum by id: {}", request.getId());

        museumRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        museum -> {
                            responseObserver.onNext(MuseumMapper.toGrpcResponse(museum));
                            responseObserver.onCompleted();
                        },
                        () -> {
                            throw new MuseumNotFoundException(UUID.fromString(request.getId()));
                        }
                );

    }

    @Override
    public void findByTitle(NameType request, StreamObserver<MuseumGrpcResponse> responseObserver) {

        log.info("Find museum by title: {}", request.getName());

        museumRepository.findByTitle(request.getName())
                .ifPresentOrElse(
                        museum -> {
                            responseObserver.onNext(MuseumMapper.toGrpcResponse(museum));
                            responseObserver.onCompleted();
                        },
                        () -> {
                            throw new MuseumNotFoundException(request.getName());
                        }
                );


    }

    @Override
    public void findAllByIds(IdsType request, StreamObserver<MuseumListGrpcResponse> responseObserver) {

        log.info("Find all museum by params: {}", request);

        var ids = request.getIdList().stream()
                .map(UUID::fromString)
                .toList();
        var grpcMuseums = museumRepository.findAllById(ids).stream()
                .map(MuseumMapper::toGrpcResponse)
                .toList();

        responseObserver.onNext(MuseumListGrpcResponse.newBuilder()
                .addAllMuseums(grpcMuseums)
                .build());

        responseObserver.onCompleted();

    }

    @Override
    public void findAll(MuseumsFilterGrpcRequest request, StreamObserver<MuseumsGrpcResponse> responseObserver) {

        log.info("Find all museum by params: {}", request);

        responseObserver.onNext(
                MuseumMapper.toPageGrpc(
                        museumRepository.findAll(
                                museumSpecs.findByCriteria(
                                        MuseumMapper.fromGrpcFilter(request)),
                                MuseumMapper.fromPageableGrpc(request.getPageable()))));

        responseObserver.onCompleted();

    }

    @Override
    public void update(UpdateMuseumGrpcRequest request, StreamObserver<MuseumGrpcResponse> responseObserver) {

        log.info("Update museum: {}", request);

        museumRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        museum -> {
                            museumRepository.findByTitle(request.getTitle())
                                    .ifPresent(m -> {
                                        if (!m.getId().equals(museum.getId()))
                                            throw new MuseumAlreadyExistException(request.getTitle());
                                    });
                            responseObserver.onNext(
                                    MuseumMapper.toGrpcResponse(
                                            museumRepository.save(
                                                    MuseumMapper.updateFromGrpcRequest(museum, request))));
                            responseObserver.onCompleted();
                        },
                        () -> {
                            throw new MuseumNotFoundException(UUID.fromString(request.getId()));
                        }
                );

    }

    @Override
    public void removeById(IdType request, StreamObserver<com.google.protobuf.Empty> responseObserver) {

        log.info("Delete museum by id: {}", request.getId());

        museumRepository.findById(UUID.fromString(request.getId()))
                .ifPresent(museumRepository::delete);

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();

    }

}
