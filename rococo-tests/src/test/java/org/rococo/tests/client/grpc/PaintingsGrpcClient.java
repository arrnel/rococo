package org.rococo.tests.client.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.paintings.PaintingsServiceGrpc;
import org.rococo.tests.ex.PaintingAlreadyExistException;
import org.rococo.tests.ex.PaintingNotFoundException;
import org.rococo.tests.ex.ServiceUnavailableException;
import org.rococo.tests.mapper.PaintingMapper;
import org.rococo.tests.model.PaintingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class PaintingsGrpcClient extends GrpcClient {

    private static final String SERVICE_NAME = "rococo-paintings";
    private final PaintingsServiceGrpc.PaintingsServiceBlockingStub paintingsServiceStub;

    public PaintingsGrpcClient() {
        super(CFG.paintingsGrpcHost(), CFG.paintingsPort());
        paintingsServiceStub = PaintingsServiceGrpc.newBlockingStub(channel);
    }

    @Nonnull
    public PaintingDTO add(PaintingDTO requestDTO) {
        try {
            return PaintingMapper.toDTO(
                    paintingsServiceStub.add(
                            PaintingMapper.toGrpcRequest(requestDTO)));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.ALREADY_EXISTS)
                throw new PaintingAlreadyExistException(requestDTO.getTitle());
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());

        }
    }

    @Nonnull
    public Optional<PaintingDTO> findById(UUID id) {

        try {
            return Optional.of(
                    PaintingMapper.toDTO(
                            paintingsServiceStub.findById(
                                    IdType.newBuilder()
                                            .setId(id.toString())
                                            .build())));
        } catch (StatusRuntimeException ex) {

            if (ex.getStatus().getCode() != Status.Code.NOT_FOUND)
                throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());

            return Optional.empty();

        }

    }

    @Nonnull
    public Page<PaintingDTO> findAll(@Nullable String title,
                                     @Nullable UUID artistId,
                                     Pageable pageable
    ) {

        try {
            return PaintingMapper.toPageDTO(
                    paintingsServiceStub.findAll(
                            PaintingMapper.toFilter(title, artistId, pageable)));
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    @Nonnull
    public PaintingDTO update(PaintingDTO requestDTO) {

        try {
            return PaintingMapper.toDTO(
                    paintingsServiceStub.update(
                            PaintingMapper.toUpdateGrpcRequest(requestDTO)));
        } catch (StatusRuntimeException ex) {

            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND)
                throw new PaintingNotFoundException(requestDTO.getId());

            if (ex.getStatus().getCode() == Status.Code.ALREADY_EXISTS)
                throw new PaintingAlreadyExistException(requestDTO.getTitle());

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
