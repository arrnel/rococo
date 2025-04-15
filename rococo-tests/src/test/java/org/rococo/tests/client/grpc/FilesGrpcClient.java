package org.rococo.tests.client.grpc;

import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.rococo.grpc.files.EntityTypeGrpc;
import org.rococo.grpc.files.FilesServiceGrpc;
import org.rococo.grpc.files.FindImagesGrpcRequest;
import org.rococo.tests.enums.EntityType;
import org.rococo.tests.ex.ImageNotFoundException;
import org.rococo.tests.ex.ServiceUnavailableException;
import org.rococo.tests.mapper.ImageMapper;
import org.rococo.tests.model.ImageDTO;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ParametersAreNonnullByDefault
public class FilesGrpcClient extends GrpcClient {

    private static final String SERVICE_NAME = "rococo-files";
    private final FilesServiceGrpc.FilesServiceBlockingStub filesServiceStub;

    public FilesGrpcClient() {
        super(CFG.filesGrpcHost(), CFG.filesPort());
        filesServiceStub = FilesServiceGrpc.newBlockingStub(channel);
    }

    public void addImage(EntityType entityType, UUID entityId, String image) {
        try {
            filesServiceStub.addImage(
                    ImageMapper.toGrpcRequest(entityType, entityId, image));
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    public Optional<ImageDTO> findImage(EntityType entityType, UUID entityId) {
        try {
            return Optional.of(
                    ImageMapper.toDTO(
                            filesServiceStub.findByEntityTypeAndId(
                                    ImageMapper.toFindGrpcRequest(entityType, entityId))));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() != Code.NOT_FOUND)
                throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
            return Optional.empty();
        }
    }

    public List<ImageDTO> findAll(final EntityType entityType, final List<UUID> entityIds) {

        try {
            final var grpcImagesStream = filesServiceStub.findAllByEntityTypeAndIds(
                    FindImagesGrpcRequest.newBuilder()
                            .setEntityType(EntityTypeGrpc.valueOf(entityType.name()))
                            .addAllEntityIds(entityIds.stream()
                                    .map(UUID::toString)
                                    .toList())
                            .build());
            final List<ImageDTO> images = new ArrayList<>();
            while (grpcImagesStream.hasNext()) {
                images.add(ImageMapper.toDTO(grpcImagesStream.next()));
            }
            return images;
        } catch (StatusRuntimeException ex) {
            log.info(ex.getMessage());
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }

    }

    public void update(EntityType entityType, UUID entityId, String image) {
        try {
            filesServiceStub.updateImage(
                    ImageMapper.toGrpcRequest(entityType, entityId, image));
        } catch (StatusRuntimeException ex) {
            throw (ex.getStatus().getCode() == Code.NOT_FOUND)
                    ? new ImageNotFoundException(entityType, entityId)
                    : new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    public void delete(EntityType entityType, UUID entityId) {
        try {
            filesServiceStub.deleteImage(
                    ImageMapper.toFindGrpcRequest(entityType, entityId));
        } catch (StatusRuntimeException ex) {
            throw (ex.getStatus().getCode() == Code.NOT_FOUND)
                    ? new ImageNotFoundException(entityType, entityId)
                    : new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

}
