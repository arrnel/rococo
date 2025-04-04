package org.rococo.paintings.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.paintings.*;
import org.rococo.paintings.data.PaintingRepository;
import org.rococo.paintings.ex.PaintingAlreadyExistException;
import org.rococo.paintings.ex.PaintingNotFoundException;
import org.rococo.paintings.mapper.PaintingMapper;
import org.rococo.paintings.specs.PaintingSpecs;

import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class PaintingGrpcService extends PaintingsServiceGrpc.PaintingsServiceImplBase {

    private final PaintingRepository paintingRepository;
    private final PaintingSpecs paintingSpecs;

    @Override
    public void add(AddPaintingGrpcRequest request, StreamObserver<PaintingGrpcResponse> responseObserver) {

        log.info("Add new painting: {}", request);

        paintingRepository.findByTitle(request.getTitle())
                .ifPresentOrElse(
                        painting -> {
                            throw new PaintingAlreadyExistException(request.getTitle());
                        },
                        () -> {
                            responseObserver.onNext(
                                    PaintingMapper.toGrpcResponse(
                                            paintingRepository.save(
                                                    PaintingMapper.fromGrpcRequest(request))));
                            responseObserver.onCompleted();
                        }
                );


    }

    @Override
    public void findById(IdType request, StreamObserver<PaintingGrpcResponse> responseObserver) {

        log.info("Find painting by id: {}", request.getId());

        paintingRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        museum -> responseObserver.onNext(
                                PaintingMapper.toGrpcResponse(museum)),
                        () -> {
                            throw new PaintingNotFoundException(UUID.fromString(request.getId()));
                        }
                );

        responseObserver.onCompleted();

    }

    @Override
    public void findAll(PaintingsFilterGrpcRequest request, StreamObserver<PaintingsGrpcResponse> responseObserver) {

        log.info("Find all paintings by params: {}", request);

        responseObserver.onNext(
                PaintingMapper.toPageGrpc(
                        paintingRepository.findAll(
                                paintingSpecs.findByCriteria(PaintingMapper.fromGrpcFilter(request)),
                                PaintingMapper.fromPageableGrpc(request.getPageable()))
                ));

        responseObserver.onCompleted();

    }

    @Override
    public void update(UpdatePaintingGrpcRequest request, StreamObserver<PaintingGrpcResponse> responseObserver) {

        log.info("Update painting: {}", request);

        paintingRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        painting -> {
                            paintingRepository.findByTitle(request.getTitle())
                                    .ifPresent(p -> {
                                        if (!p.getId().equals(painting.getId()))
                                            throw new PaintingAlreadyExistException(painting.getTitle());
                                    });
                            responseObserver.onNext(
                                    PaintingMapper.toGrpcResponse(
                                            paintingRepository.save(
                                                    PaintingMapper.updateFromGrpcRequest(painting, request))));
                        },
                        () -> {
                            throw new PaintingNotFoundException(UUID.fromString(request.getId()));
                        }
                );

        responseObserver.onCompleted();

    }

    @Override
    public void removeById(IdType request, StreamObserver<Empty> responseObserver) {

        log.info("Find painting by id: {}", request.getId());

        paintingRepository.findById(UUID.fromString(request.getId()))
                .ifPresent(paintingRepository::delete);

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();

    }

}
