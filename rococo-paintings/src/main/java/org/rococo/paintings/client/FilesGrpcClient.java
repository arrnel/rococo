package org.rococo.paintings.client;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.grpc.files.EntityTypeGrpc;
import org.rococo.grpc.files.FilesServiceGrpc;
import org.rococo.grpc.files.FindImageGrpcRequest;
import org.rococo.paintings.ex.ServiceUnavailableException;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class FilesGrpcClient {

    private static final String SERVICE_NAME = "rococo-museums";

    @GrpcClient("grpcFilesClient")
    private FilesServiceGrpc.FilesServiceBlockingStub filesServiceStub;

    public String findImage(UUID paintingId) {
        try {
            var imageGrpcResponse = filesServiceStub.findByEntityTypeAndId(
                    FindImageGrpcRequest.newBuilder()
                            .setEntityType(EntityTypeGrpc.PAINTING)
                            .setEntityId(paintingId.toString())
                            .build());
            return imageGrpcResponse.getContent();
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND)
                throw new PaintingImageNotFoundException(paintingId);
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

}
