package org.rococo.tests.client.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Step;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.paintings.PaintingsServiceGrpc;
import org.rococo.tests.ex.*;
import org.rococo.tests.mapper.PaintingMapper;
import org.rococo.tests.model.PaintingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
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
    @Step("[GRPC] Send add painting request")
    public PaintingDTO add(PaintingDTO requestDTO) {
        try {
            return PaintingMapper.toDTO(
                    paintingsServiceStub.add(
                            PaintingMapper.toGrpcRequest(requestDTO)));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.ALREADY_EXISTS)
                throw new PaintingAlreadyExistsException(requestDTO.getTitle());
            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw Objects.requireNonNull(ex.getStatus().getDescription()).contains("Artist")
                        ? new ArtistNotFoundException(requestDTO.getArtist().getId())
                        : new MuseumNotFoundException(requestDTO.getMuseum().getId());
            }
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    @Nonnull
    @Step("[GRPC] Send find by id painting request")
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
    @Step("[GRPC] Send find by title painting request")
    public Optional<PaintingDTO> findByTitle(String title) {
        try {
            return Optional.of(
                    PaintingMapper.toDTO(
                            paintingsServiceStub.findByTitle(
                                    NameType.newBuilder()
                                            .setName(title)
                                            .build())));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() != Status.Code.NOT_FOUND)
                throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
            return Optional.empty();
        }
    }

    @Nonnull
    @Step("[GRPC] Send update painting request")
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
    @Step("[GRPC] Send update painting request")
    public PaintingDTO update(PaintingDTO requestDTO) {

        try {
            return PaintingMapper.toDTO(
                    paintingsServiceStub.update(
                            PaintingMapper.toUpdateGrpcRequest(requestDTO)));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND) {
                var errorMessage = Objects.requireNonNull(ex.getStatus().getDescription());
                if (errorMessage.contains("Painting")) {
                    throw new PaintingNotFoundException(requestDTO.getId());
                } else if (errorMessage.contains("Artist")) {
                    throw new ArtistNotFoundException(requestDTO.getArtist().getId());
                } else {
                    throw new MuseumNotFoundException(requestDTO.getMuseum().getId());
                }
            } else if (ex.getStatus().getCode() == Status.Code.ALREADY_EXISTS) {
                throw new PaintingAlreadyExistsException(requestDTO.getTitle());
            }
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    @Step("[GRPC] Send delete painting request")
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
