package org.rococo.paintings.client;

import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.grpc.artists.ArtistGrpcResponse;
import org.rococo.grpc.artists.ArtistsByIdsGrpcRequest;
import org.rococo.grpc.artists.ArtistsServiceGrpc;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.IdsType;
import org.rococo.paintings.ex.ArtistNotFoundException;
import org.rococo.paintings.ex.ServiceUnavailableException;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class ArtistsGrpcClient {

    private static final String SERVICE_NAME = "rococo-artists";

    @GrpcClient("grpcArtistsClient")
    private ArtistsServiceGrpc.ArtistsServiceBlockingStub artistsServiceStub;

    public Optional<ArtistGrpcResponse> findById(UUID id) {

        log.info("Find artist by id: {}", id);

        try {
            return Optional.of(
                    artistsServiceStub.findById(
                            IdType.newBuilder()
                                    .setId(id.toString())
                                    .build()));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Code.NOT_FOUND)
                return Optional.empty();
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    public List<ArtistGrpcResponse> findAllByIds(List<UUID> ids) {

        log.info("Find all artists by ids: {}", ids);

        try {
            var idsText = ids.stream()
                    .map(UUID::toString)
                    .toList();
            var response = artistsServiceStub.findAllByIds(
                    ArtistsByIdsGrpcRequest.newBuilder()
                            .setIds(
                                    IdsType.newBuilder()
                                            .addAllId(idsText)
                                            .build())
                            .setOriginalPhoto(false)
                    .build());
            return response.getArtistsList();
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

}
