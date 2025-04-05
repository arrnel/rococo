package org.rococo.gateway.client;

import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.gateway.ex.ArtistAlreadyExistException;
import org.rococo.gateway.ex.ArtistNotFoundException;
import org.rococo.gateway.ex.ServiceUnavailableException;
import org.rococo.gateway.mapper.ArtistMapper;
import org.rococo.gateway.model.artists.AddArtistRequestDTO;
import org.rococo.gateway.model.artists.ArtistDTO;
import org.rococo.gateway.model.artists.UpdateArtistRequestDTO;
import org.rococo.grpc.artists.ArtistsServiceGrpc;
import org.rococo.grpc.common.type.IdType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static io.grpc.Status.Code.ALREADY_EXISTS;

@Slf4j
@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class ArtistsGrpcClient {

    private static final String SERVICE_NAME = "rococo-artists";

    @GrpcClient("grpcArtistsClient")
    private ArtistsServiceGrpc.ArtistsServiceBlockingStub artistsServiceStub;

    public ArtistDTO add(AddArtistRequestDTO requestDTO) {

        try {
            return ArtistMapper.toDTO(
                    artistsServiceStub.add(
                            ArtistMapper.toGrpcModel(requestDTO)));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == ALREADY_EXISTS)
                throw new ArtistAlreadyExistException(requestDTO.name());
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    public Optional<ArtistDTO> findById(UUID id) {

        try {
            final var artistResponse = artistsServiceStub.findById(
                    IdType.newBuilder()
                            .setId(id.toString())
                            .build());
            return Optional.of(ArtistMapper.toDTO(artistResponse));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Code.NOT_FOUND)
                return Optional.empty();
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    public Page<ArtistDTO> findAll(String name, Pageable pageable) {

        try {
            return ArtistMapper.toPageDTO(
                    artistsServiceStub.findAll(
                            ArtistMapper.toFilter(name, pageable)));
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    public ArtistDTO update(UpdateArtistRequestDTO requestDTO) {

        try {
            return ArtistMapper.toDTO(
                    artistsServiceStub.update(
                            ArtistMapper.toGrpcModel(requestDTO)));
        } catch (StatusRuntimeException ex) {

            if (ex.getStatus().getCode() == Code.NOT_FOUND)
                throw new ArtistNotFoundException(requestDTO.id());

            if (ex.getStatus().getCode() == ALREADY_EXISTS)
                throw new ArtistAlreadyExistException(requestDTO.name());

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
