package org.rococo.tests.client.grpc;

import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.grpc.artists.ArtistsServiceGrpc;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.tests.enums.EntityType;
import org.rococo.tests.ex.ArtistAlreadyExistsException;
import org.rococo.tests.ex.ArtistNotFoundException;
import org.rococo.tests.ex.ImageNotFoundException;
import org.rococo.tests.ex.ServiceUnavailableException;
import org.rococo.tests.mapper.ArtistMapper;
import org.rococo.tests.model.ArtistDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static io.grpc.Status.Code.ALREADY_EXISTS;

@Slf4j
@ParametersAreNonnullByDefault
public class ArtistsGrpcClient extends GrpcClient {

    private static final String SERVICE_NAME = "rococo-artists";
    private final ArtistsServiceGrpc.ArtistsServiceBlockingStub artistsServiceStub;

    public ArtistsGrpcClient() {
        super(CFG.artistsGrpcHost(), CFG.artistsPort());
        artistsServiceStub = ArtistsServiceGrpc.newBlockingStub(channel);
    }

    @Nonnull
    @Step("[GRPC] Send add artist request")
    public ArtistDTO add(ArtistDTO requestDTO) {
        try {
            return ArtistMapper.toDTO(
                    artistsServiceStub.add(
                            ArtistMapper.toGrpcRequest(requestDTO)));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Code.ALREADY_EXISTS) {
                throw new ArtistAlreadyExistsException(requestDTO.getName());
            }
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    @Nonnull
    @Step("[GRPC] Send find artist by id request")
    public Optional<ArtistDTO> findById(UUID id) {
        try {
            return Optional.of(
                    ArtistMapper.toDTO(
                            artistsServiceStub.findById(
                                    IdType.newBuilder()
                                            .setId(id.toString())
                                            .build())));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Code.NOT_FOUND) {
                if (Objects.requireNonNull(ex.getStatus().getDescription()).contains("entity_type")) {
                    throw new ImageNotFoundException(EntityType.ARTIST, id);
                }
                return Optional.empty();
            }
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    @Nonnull
    @Step("[GRPC] Send find artist by name request")
    public Optional<ArtistDTO> findByName(String name) {
        try {
            final var artistResponse = artistsServiceStub.findByName(
                    NameType.newBuilder()
                            .setName(name)
                            .build());
            return Optional.of(ArtistMapper.toDTO(artistResponse));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() != Code.NOT_FOUND)
                throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
            return Optional.empty();
        }
    }

    @Nonnull
    @Step("[GRPC] Send find all artists request")
    public Page<ArtistDTO> findAll(@Nullable String name, Pageable pageable) {
        try {
            return ArtistMapper.toPageDTO(
                    artistsServiceStub.findAll(
                            ArtistMapper.toFilter(name, pageable)));
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    @Nonnull
    @Step("[GRPC] Send update artist request")
    public ArtistDTO update(ArtistDTO requestDTO) {
        try {
            return ArtistMapper.toDTO(
                    artistsServiceStub.update(
                            ArtistMapper.toUpdateGrpcRequest(requestDTO)));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Code.NOT_FOUND)
                throw new ArtistNotFoundException(requestDTO.getId());
            if (ex.getStatus().getCode() == ALREADY_EXISTS)
                throw new ArtistAlreadyExistsException(requestDTO.getName());
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    @Step("[GRPC] Send delete artist request")
    public void delete(UUID id) {
        try {
            artistsServiceStub.removeById(
                    IdType.newBuilder()
                            .setId(id.toString())
                            .build());
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

}
