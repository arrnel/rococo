package org.rococo.paintings.client;

import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.grpc.artists.ArtistsServiceGrpc;
import org.rococo.grpc.common.type.IdType;
import org.rococo.paintings.ex.ArtistNotFoundException;
import org.rococo.paintings.ex.ServiceUnavailableException;
import org.rococo.paintings.mapper.ArtistMapper;
import org.rococo.paintings.model.ArtistDTO;
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

    public ArtistDTO findById(UUID id) {

        log.info("Find artist by id: %s".formatted(id));

        try {
            final var artistResponse = artistsServiceStub.findById(
                    IdType.newBuilder()
                            .setId(id.toString())
                            .build());
            return ArtistMapper.toDTO(artistResponse);
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Code.NOT_FOUND)
                throw new ArtistNotFoundException(id);
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

}
