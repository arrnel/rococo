package org.rococo.gateway.client;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.gateway.ex.PaintingAlreadyExistException;
import org.rococo.gateway.ex.PaintingNotFoundException;
import org.rococo.gateway.ex.ServiceUnavailableException;
import org.rococo.gateway.mapper.PaintingMapper;
import org.rococo.gateway.model.paintings.AddPaintingRequestDTO;
import org.rococo.gateway.model.paintings.PaintingDTO;
import org.rococo.gateway.model.paintings.UpdatePaintingRequestDTO;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.paintings.PaintingsServiceGrpc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class PaintingsGrpcClient {

    private static final String SERVICE_NAME = "rococo-paintings";

    @GrpcClient("grpcPaintingsClient")
    private PaintingsServiceGrpc.PaintingsServiceBlockingStub paintingsServiceStub;

    @Nonnull
    public PaintingDTO add(AddPaintingRequestDTO requestDTO) {

        try {
            return PaintingMapper.toDTO(
                    paintingsServiceStub.add(
                            PaintingMapper.toGrpcModel(requestDTO)));

        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.ALREADY_EXISTS)
                throw new PaintingAlreadyExistException(requestDTO.title());
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    @Nonnull
    public Optional<PaintingDTO> findById(UUID id) {

        try {
            var idType = IdType.newBuilder()
                    .setId(id.toString())
                    .build();
            return Optional.of(
                    PaintingMapper.toDTO(
                            paintingsServiceStub.findById(idType)));
        } catch (StatusRuntimeException ex) {

            if (ex.getStatus().getCode() != Status.Code.NOT_FOUND)
                throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());

            return Optional.empty();

        }

    }

    @Nonnull
    public Page<PaintingDTO> findAll(@Nullable String name,
                                     @Nullable UUID artistId,
                                     Pageable pageable
    ) {

        try {
            return PaintingMapper.toPageDTO(
                    paintingsServiceStub.findAll(
                            PaintingMapper.toFilter(name, artistId, pageable)));
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    @Nonnull
    public PaintingDTO update(UpdatePaintingRequestDTO requestDTO) {

        try {
            return PaintingMapper.toDTO(
                    paintingsServiceStub.update(
                            PaintingMapper.toGrpcModel(requestDTO)));
        } catch (StatusRuntimeException ex) {

            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND)
                throw new PaintingNotFoundException(requestDTO.id());

            if (ex.getStatus().getCode() == Status.Code.ALREADY_EXISTS)
                throw new PaintingAlreadyExistException(requestDTO.title());

            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());

        }

    }

    public void delete(UUID id) {

        try {
            paintingsServiceStub.removeById(
                    IdType.newBuilder()
                            .setId(id.toString())
                            .build());
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

}
