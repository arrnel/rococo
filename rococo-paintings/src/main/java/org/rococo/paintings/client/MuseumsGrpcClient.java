package org.rococo.paintings.client;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.IdsType;
import org.rococo.grpc.museums.MuseumGrpcResponse;
import org.rococo.grpc.museums.MuseumsByIdsGrpcRequest;
import org.rococo.grpc.museums.MuseumsServiceGrpc;
import org.rococo.paintings.ex.ServiceUnavailableException;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class MuseumsGrpcClient {

    private static final String SERVICE_NAME = "rococo-museums";

    @GrpcClient("grpcMuseumsClient")
    private MuseumsServiceGrpc.MuseumsServiceBlockingStub museumsServiceStub;

    @Nonnull
    public Optional<MuseumGrpcResponse> findById(UUID id) {

        log.info("Find museum by id: %s".formatted(id));

        try {
            var idType = IdType.newBuilder()
                    .setId(id.toString())
                    .build();
            return Optional.of(museumsServiceStub.findById(idType));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND)
                return Optional.empty();
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    public List<MuseumGrpcResponse> findAllByIds(List<UUID> ids) {

        log.info("Find all museums by ids: {}", ids);

        try {
            var idsText = ids.stream()
                    .map(UUID::toString)
                    .toList();
            var response = museumsServiceStub.findAllByIds(
                    MuseumsByIdsGrpcRequest.newBuilder()
                            .setIds(
                                    IdsType.newBuilder()
                                            .addAllId(idsText)
                                            .build())
                            .setOriginalPhoto(false)
                            .build());
            return response.getMuseumsList();
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }
}
