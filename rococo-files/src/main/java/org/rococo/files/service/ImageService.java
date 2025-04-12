package org.rococo.files.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.rococo.files.config.AppProperty;
import org.rococo.files.data.entity.EntityType;
import org.rococo.files.data.entity.ImageMetadataEntity;
import org.rococo.files.data.repository.ImageContentRepository;
import org.rococo.files.data.repository.ImageMetadataRepository;
import org.rococo.files.ex.BadRequestException;
import org.rococo.files.ex.ImageAlreadyExistsException;
import org.rococo.files.ex.ImageNotFoundException;
import org.rococo.files.ex.InternalException;
import org.rococo.files.mapper.ImageMapper;
import org.rococo.files.specs.ImageSpecs;
import org.rococo.grpc.files.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class ImageService extends FilesServiceGrpc.FilesServiceImplBase {

    private final ImageMetadataRepository metadataRepository;
    private final ImageContentRepository contentRepository;

    private final ImageSpecs imageSpecs;

    @Override
    @Transactional
    public void addImage(ImageGrpcRequest request, StreamObserver<Empty> responseObserver) {

        log.info("Add new image: entityType = [{}], entityId = [{}]", request.getEntityType(), request.getEntityId());

        validateMetadataRequestParams(request.getEntityType(), request.getEntityId());
        validateContentRequestParams(request.getContent());
        validateContentPattern(request.getContent());

        try {
            var metadataEntity = ImageMapper.fromGrpcRequest(request);
            metadataRepository.save(metadataEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new ImageAlreadyExistsException(
                    EntityType.valueOf(request.getEntityType().name()),
                    UUID.fromString(request.getEntityId()));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new InternalException("Failed to save image\n" + ex.getCause().getMessage());
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();

    }

    @Override
    @Transactional(readOnly = true)
    public void findByEntityTypeAndId(FindImageGrpcRequest request,
                                      StreamObserver<ImageGrpcResponse> responseObserver
    ) {

        log.info("Find image by entityType = [{}] and entityId = [{}]", request.getEntityType(), request.getEntityId());

        validateMetadataRequestParams(request.getEntityType(), request.getEntityId());

        metadataRepository.findByEntityTypeAndEntityId(EntityType.valueOf(request.getEntityType().name()),
                        UUID.fromString(request.getEntityId()))
                .ifPresentOrElse(
                        metadata -> responseObserver.onNext(
                                ImageMapper.toGrpcResponse(metadata, true)),
                        () -> {
                            throw new ImageNotFoundException(
                                    EntityType.valueOf(request.getEntityType().name()),
                                    UUID.fromString(request.getEntityId()));
                        }
                );

        responseObserver.onCompleted();

    }

    @Override
    @Transactional(readOnly = true)
    public void findAllByEntityTypeAndIds(FindImagesGrpcRequest request,
                                          StreamObserver<ImageGrpcResponse> responseObserver
    ) {

        UUID[] entityIds = request.getEntityIdsList().stream()
                .map(UUID::fromString)
                .toArray(UUID[]::new);

        log.info("Find all images by params: entityType = [{}], entityIds = {}, isOriginalPhoto = {}",
                request.getEntityType(), Arrays.toString(entityIds), request.getIsOriginal());

        final var imageFilter = ImageMapper.fromFilterGrpc(request);

        metadataRepository.findAll(imageSpecs.findByCriteria(imageFilter))
                .forEach(content -> responseObserver.onNext(
                        ImageMapper.toGrpcResponse(content, request.getIsOriginal())));

        responseObserver.onCompleted();

    }

    /**
     * Updating metadata and content
     * 1) If old metadata not exists and request contains image -> create new image (important for user)
     * 2) If old metadata exists ->
     * a) update values if request contains image;
     * b) clear some metadata and content fields.
     */
    @Override
    @Transactional
    public void updateImage(ImageGrpcRequest request, StreamObserver<Empty> responseObserver) {

        final EntityType entityType = EntityType.valueOf(request.getEntityType().name());
        final UUID entityId = UUID.fromString(request.getEntityId());

        validateMetadataRequestParams(request.getEntityType(), request.getEntityId());

        var oldMetadata = metadataRepository
                .findByEntityTypeAndEntityId(entityType, entityId)
                .orElseThrow(() -> new ImageNotFoundException(entityType, entityId));

        if (request.getContent().isEmpty()) {
            ImageMapper.update(oldMetadata, ImageMetadataEntity.empty());
        } else {
            ImageMapper.updateFromGrpcRequest(oldMetadata, request);
        }

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();

    }

    @Override
    @Transactional
    public void deleteImage(FindImageGrpcRequest request,
                            StreamObserver<Empty> responseObserver
    ) {

        validateMetadataRequestParams(request.getEntityType(), request.getEntityId());

        log.info("Find image by entityType = [{}] and entityId = [{}]", request.getEntityType(), request.getEntityId());

        if (request.getEntityType() == EntityTypeGrpc.UNDEFINED)
            throw new IllegalArgumentException("Entity type can not equals UNDEFINED");

        if (request.getEntityId().isEmpty())
            throw new IllegalArgumentException("Entity id can not be empty");

        metadataRepository.findByEntityTypeAndEntityId(EntityType.valueOf(request.getEntityType().name()),
                        UUID.fromString(request.getEntityId()))
                .ifPresent(metadataRepository::delete);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();

    }

    private static void validateMetadataRequestParams(EntityTypeGrpc entityType,
                                                      String entityId
    ) {

        if (entityType == EntityTypeGrpc.UNDEFINED || entityId.isEmpty())
            throw new BadRequestException(
                    (entityType == EntityTypeGrpc.UNDEFINED)
                            ? "Entity type can not equals " + EntityTypeGrpc.UNDEFINED.name()
                            : "Entity id can not be empty"
            );

    }

    private static void validateContentRequestParams(ByteString content) {
        if (content.isEmpty())
            throw new BadRequestException("Content can not be empty");
    }

    private static void validateContentPattern(ByteString content) {
        if (!content.isEmpty() && !content.toString(Charset.defaultCharset()).matches(AppProperty.IMAGE_PATTERN))
            throw new BadRequestException("Content has invalid pattern");
    }

}
