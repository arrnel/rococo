package org.rococo.tests.client.grpc;

import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.rococo.grpc.artists.ArtistsServiceGrpc;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.tests.ex.ArtistAlreadyExistsException;
import org.rococo.tests.ex.ArtistNotFoundException;
import org.rococo.tests.ex.ServiceUnavailableException;
import org.rococo.tests.mapper.ArtistMapper;
import org.rococo.tests.model.ArtistDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ParametersAreNonnullByDefault
public class ArtistsGrpcClient extends GrpcClient {

    private static final String SERVICE_NAME = "rococo-artists";
    private final ArtistsServiceGrpc.ArtistsServiceBlockingStub artistsServiceStub;

    public ArtistsGrpcClient() {
        super(CFG.artistsGrpcHost(), CFG.artistsPort());
        artistsServiceStub = ArtistsServiceGrpc.newBlockingStub(channel);
    }

    public ArtistDTO add(ArtistDTO requestDTO) {

        try {
            return ArtistMapper.toDTO(
                    artistsServiceStub.add(
                            ArtistMapper.toGrpcRequest(requestDTO)));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Code.ALREADY_EXISTS) {
                throw new ArtistAlreadyExistsException(requestDTO.getName());
            }
            log.error("Unexpected response status: code = [{}], description = [{}]", ex.getStatus().getCode(), ex.getStatus().getDescription());
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    public Optional<ArtistDTO> findById(UUID id) {

        try {
            return Optional.of(
                    ArtistMapper.toDTO(
                            artistsServiceStub.findById(
                                    IdType.newBuilder()
                                            .setId(id.toString())
                                            .build())));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() != Code.NOT_FOUND)
                throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
            return Optional.empty();
        }

    }

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

    public Page<ArtistDTO> findAll(@Nullable String name, Pageable pageable) {

        try {
            return ArtistMapper.toPageDTO(
                    artistsServiceStub.findAll(
                            ArtistMapper.toFilter(name, pageable)));
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    public ArtistDTO update(ArtistDTO requestDTO) {

        try {
            return ArtistMapper.toDTO(
                    artistsServiceStub.update(
                            ArtistMapper.toUpdateGrpcRequest(requestDTO)));
        } catch (StatusRuntimeException ex) {

            if (ex.getStatus().getCode() == Code.NOT_FOUND)
                throw new ArtistNotFoundException(requestDTO.getId());

            if (ex.getStatus().getCode() == Code.ALREADY_EXISTS)
                throw new ArtistAlreadyExistsException(requestDTO.getName());

            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());

        }

    }

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
