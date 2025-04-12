package org.rococo.users.client;

import com.google.protobuf.ByteString;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.grpc.files.*;
import org.rococo.users.ex.ImageNotFoundException;
import org.rococo.users.ex.ServiceUnavailableException;
import org.springframework.stereotype.Service;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.rococo.grpc.files.EntityTypeGrpc.USER;


@Slf4j
@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class FilesGrpcClient {

    private static final String SERVICE_NAME = "rococo-files";

    @GrpcClient("grpcFilesClient")
    private FilesServiceGrpc.FilesServiceBlockingStub filesServiceStub;

    public void add(UUID entityId, String image) {
        try {
            var request = ImageGrpcRequest.newBuilder()
                    .setEntityType(USER)
                    .setEntityId(entityId.toString())
                    .setContent(ByteString.copyFromUtf8(image))
                    .build();
            filesServiceStub.addImage(request);
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    public Optional<ImageGrpcResponse> findImage(UUID entityId) {
        try {
            var request = FindImageGrpcRequest.newBuilder()
                    .setEntityType(USER)
                    .setEntityId(entityId.toString())
                    .build();
            return Optional.of(filesServiceStub.findByEntityTypeAndId(request));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND)
                return Optional.empty();
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    public List<ImageGrpcResponse> findAllByIds(final List<UUID> entityIds, boolean isOriginal) {

        try {
            final var grpcImagesStream = filesServiceStub.findAllByEntityTypeAndIds(
                    FindImagesGrpcRequest.newBuilder()
                            .setEntityType(USER)
                            .addAllEntityIds(entityIds.stream()
                                    .map(UUID::toString)
                                    .toList())
                            .setIsOriginal(isOriginal)
                            .build());

            List<ImageGrpcResponse> response = new ArrayList<>();
            while(grpcImagesStream.hasNext()) {
                response.add(grpcImagesStream.next());
            }
            return response;
        } catch (StatusRuntimeException ex) {
            log.info(ex.getMessage());
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    public void update(UUID entityId, String image) {
        try {
            filesServiceStub.updateImage(
                    ImageGrpcRequest.newBuilder()
                            .setEntityType(USER)
                            .setEntityId(entityId.toString())
                            .setContent(ByteString.copyFrom(image, StandardCharsets.UTF_8))
                            .build());
        } catch (StatusRuntimeException ex) {
            throw (ex.getStatus().getCode() == Status.Code.NOT_FOUND)
                    ? new ImageNotFoundException(entityId)
                    : new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    public void delete(UUID entityId) {
        try {
            filesServiceStub.deleteImage(
                    FindImageGrpcRequest.newBuilder()
                            .setEntityType(USER)
                            .setEntityId(entityId.toString())
                            .build());
        } catch (StatusRuntimeException ex) {
            throw (ex.getStatus().getCode() == Status.Code.NOT_FOUND)
                    ? new ImageNotFoundException(entityId)
                    : new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

}