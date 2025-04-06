package org.rococo.paintings.client;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.museums.MuseumsServiceGrpc;
import org.rococo.paintings.ex.MuseumNotFoundException;
import org.rococo.paintings.ex.ServiceUnavailableException;
import org.rococo.paintings.mapper.MuseumMapper;
import org.rococo.paintings.model.MuseumDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
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
    public MuseumDTO findById(UUID id) {

        log.info("Find museum by id: %s".formatted(id));

        try {
            var idType = IdType.newBuilder()
                    .setId(id.toString())
                    .build();
            return MuseumMapper.toDTO(
                    museumsServiceStub.findById(idType));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND)
                throw new MuseumNotFoundException(id);
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

}
