package org.rococo.files.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.files.data.entity.EntityType;
import org.rococo.files.data.entity.ImageContentEntity;
import org.rococo.files.data.entity.ImageMetadataEntity;
import org.rococo.files.data.repository.ImageMetadataRepository;
import org.rococo.files.ex.BadRequestException;
import org.rococo.files.ex.ImageAlreadyExistsException;
import org.rococo.files.ex.ImageNotFoundException;
import org.rococo.files.specs.ImageSpecs;
import org.rococo.grpc.files.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImageService: Module tests")
class ImageGrpcServiceTests {

    public static final String IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg";
    public static final String UPDATED_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNgYGD4DwABBAEAwb9rAAAAAElFTkSuQmCC";
    public static final String THUMBNAIL_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYGD4DwABBAEAjaY5AAAAAElFTkSuQmCC";

    @Mock
    private ImageMetadataRepository metadataRepository;

    @Mock
    private ImageSpecs imageSpecs;

    @Mock
    private StreamObserver<ImageGrpcResponse> imageResponseObserver;

    @Mock
    private StreamObserver<Empty> emptyResponseObserver;

    @InjectMocks
    private ImageGrpcService imageService;

    private UUID entityId;
    private ImageMetadataEntity metadataEntity;
    private ImageGrpcRequest addRequest;
    private FindImageGrpcRequest findRequest;
    private FindImagesGrpcRequest findAllRequest;
    private ImageGrpcRequest updateRequest;
    private FindImageGrpcRequest deleteRequest;

    @BeforeEach
    void setUp() {

        entityId = UUID.randomUUID();

        final var contentEntity = ImageContentEntity.builder()
                .id(UUID.randomUUID())
                .data(IMAGE.getBytes(StandardCharsets.UTF_8))
                .thumbnailData(THUMBNAIL_IMAGE.getBytes(StandardCharsets.UTF_8))
                .build();

        metadataEntity = ImageMetadataEntity.builder()
                .id(UUID.randomUUID())
                .entityType(EntityType.USER)
                .entityId(entityId)
                .format("png")
                .contentHash("hash")
                .content(contentEntity)
                .createdDate(LocalDateTime.now())
                .build();

        addRequest = ImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.USER)
                .setEntityId(entityId.toString())
                .setContent(ByteString.copyFromUtf8(IMAGE))
                .build();

        findRequest = FindImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.USER)
                .setEntityId(entityId.toString())
                .build();

        findAllRequest = FindImagesGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.USER)
                .addEntityIds(entityId.toString())
                .setIsOriginal(true)
                .build();

        updateRequest = ImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.USER)
                .setEntityId(entityId.toString())
                .setContent(ByteString.copyFromUtf8(UPDATED_IMAGE))
                .build();

        deleteRequest = FindImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.USER)
                .setEntityId(entityId.toString())
                .build();

    }

    @Test
    @DisplayName("AddImage: add new image")
    void addImage_Success() {

        // Stubs
        when(metadataRepository.save(any(ImageMetadataEntity.class))).thenReturn(metadataEntity);

        // Steps
        imageService.addImage(addRequest, emptyResponseObserver);

        // Assertions
        verify(metadataRepository).save(any(ImageMetadataEntity.class));
        verify(emptyResponseObserver).onNext(Empty.getDefaultInstance());
        verify(emptyResponseObserver).onCompleted();
        verify(emptyResponseObserver, never()).onError(any());

    }

    @Test
    @DisplayName("AddImage: throws ImageAlreadyExistsException when image exists with same entity_type and entity_id")
    void addImage_ThrowsImageAlreadyExistsException_IfImageExists() {

        // Stubs
        when(metadataRepository.save(any(ImageMetadataEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate image"));

        // Steps & Assertions
        assertThrows(ImageAlreadyExistsException.class, () ->
                imageService.addImage(addRequest, emptyResponseObserver));

        verify(emptyResponseObserver, never()).onNext(any());
        verify(emptyResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("AddImage: throws BadRequestException when entity type is UNDEFINED")
    void addImage_ThrowsBadRequestException_IfEntityTypeIsUndefined() {

        // Data
        ImageGrpcRequest invalidRequest = ImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.UNDEFINED)
                .setEntityId(entityId.toString())
                .setContent(ByteString.copyFromUtf8(IMAGE))
                .build();

        // Steps & Assertions
        assertThrows(BadRequestException.class, () ->
                imageService.addImage(invalidRequest, emptyResponseObserver));

        verify(metadataRepository, never()).save(any());
        verify(emptyResponseObserver, never()).onNext(any());
        verify(emptyResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("AddImage: throws BadRequestException when entity ID is empty")
    void addImage_ThrowsBadRequestException_IfEntityIdIsEmpty() {

        // Data
        ImageGrpcRequest invalidRequest = ImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.USER)
                .setEntityId("")
                .setContent(ByteString.copyFromUtf8(IMAGE))
                .build();

        // Steps & Assertions
        assertThrows(BadRequestException.class, () ->
                imageService.addImage(invalidRequest, emptyResponseObserver));

        verify(metadataRepository, never()).save(any());
        verify(emptyResponseObserver, never()).onNext(any());
        verify(emptyResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("AddImage: throws BadRequestException when content is empty")
    void addImage_ThrowsBadRequestException_IfContentIsEmpty() {

        // Data
        ImageGrpcRequest invalidRequest = ImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.USER)
                .setEntityId(entityId.toString())
                .setContent(ByteString.EMPTY)
                .build();

        // Steps & Assertions
        assertThrows(BadRequestException.class, () ->
                imageService.addImage(invalidRequest, emptyResponseObserver));

        verify(metadataRepository, never()).save(any());
        verify(emptyResponseObserver, never()).onNext(any());
        verify(emptyResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("AddImage: throws BadRequestException when content has invalid pattern")
    void addImage_ThrowsBadRequestException_IfContentHasInvalidPattern() {

        // Data
        ImageGrpcRequest invalidRequest = ImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.USER)
                .setEntityId(entityId.toString())
                .setContent(ByteString.copyFromUtf8("image"))
                .build();

        // Steps & Assertions
        assertThrows(BadRequestException.class, () ->
                imageService.addImage(invalidRequest, emptyResponseObserver));

        verify(metadataRepository, never()).save(any());
        verify(emptyResponseObserver, never()).onNext(any());
        verify(emptyResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("FindByEntityTypeAndId: returns image")
    void findByEntityTypeAndId_Success() {

        // Stubs
        when(metadataRepository.findByEntityTypeAndEntityId(EntityType.USER, entityId))
                .thenReturn(Optional.of(metadataEntity));

        // Steps
        imageService.findByEntityTypeAndId(findRequest, imageResponseObserver);

        // Assertions
        verify(imageResponseObserver).onNext(any(ImageGrpcResponse.class));
        verify(imageResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("FindByEntityTypeAndId: throws ImageNotFoundException when image not exists")
    void findByEntityTypeAndId_ThrowsImageNotFoundException_IfImageDoesNotExist() {

        // Stubs
        when(metadataRepository.findByEntityTypeAndEntityId(EntityType.USER, entityId))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(ImageNotFoundException.class, () ->
                imageService.findByEntityTypeAndId(findRequest, imageResponseObserver));

        verify(imageResponseObserver, never()).onNext(any());
        verify(imageResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("FindByEntityTypeAndId: throws BadRequestException when entity type is UNDEFINED")
    void findByEntityTypeAndId_ThrowsBadRequestException_IfEntityTypeIsUndefined() {

        // Data
        FindImageGrpcRequest invalidRequest = FindImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.UNDEFINED)
                .setEntityId(entityId.toString())
                .build();

        // Steps & Assertions
        assertThrows(BadRequestException.class, () ->
                imageService.findByEntityTypeAndId(invalidRequest, imageResponseObserver));

        verify(metadataRepository, never()).findByEntityTypeAndEntityId(any(), any());
        verify(imageResponseObserver, never()).onNext(any());
        verify(imageResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("FindByEntityTypeAndId: throws BadRequestException if entity ID is empty")
    void findByEntityTypeAndId_ThrowsBadRequestException_IfEntityIdIsEmpty() {

        // Data
        FindImageGrpcRequest invalidRequest = FindImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.USER)
                .setEntityId("")
                .build();

        // Steps & Assertions
        assertThrows(BadRequestException.class, () ->
                imageService.findByEntityTypeAndId(invalidRequest, imageResponseObserver));

        verify(metadataRepository, never()).findByEntityTypeAndEntityId(any(), any());
        verify(imageResponseObserver, never()).onNext(any());
        verify(imageResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("FindAllByEntityTypeAndIds: returns images")
    void findAllByEntityTypeAndIds_Success() {

        // Stubs
        when(metadataRepository.findAll(ArgumentMatchers.<Specification<ImageMetadataEntity>>any()))
                .thenReturn(List.of(metadataEntity));

        // Steps
        imageService.findAllByEntityTypeAndIds(findAllRequest, imageResponseObserver);

        // Assertions
        verify(imageResponseObserver).onNext(any(ImageGrpcResponse.class));
        verify(imageResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("FindAllByEntityTypeAndIds: returns no images when images not found")
    void findAllByEntityTypeAndIds_ReturnsNoImages_IfNoneFound() {

        // Stubs
        when(metadataRepository.findAll(ArgumentMatchers.<Specification<ImageMetadataEntity>>any()))
                .thenReturn(Collections.emptyList());

        // Steps
        imageService.findAllByEntityTypeAndIds(findAllRequest, imageResponseObserver);

        // Assertions
        verify(imageResponseObserver, never()).onNext(any());
        verify(imageResponseObserver).onCompleted();
    }

    @Test
    @DisplayName("UpdateImage: updates metadata and content")
    void updateImage_Success_IfImageExists_AndRequestContainsContent() {

        // Stubs
        when(metadataRepository.findByEntityTypeAndEntityId(EntityType.USER, entityId))
                .thenReturn(Optional.of(metadataEntity));

        // Steps
        imageService.updateImage(updateRequest, emptyResponseObserver);

        // Assertions
        verify(metadataRepository).findByEntityTypeAndEntityId(EntityType.USER, entityId);
        verify(emptyResponseObserver).onNext(Empty.getDefaultInstance());
        verify(emptyResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("UpdateImage: clears metadata and content when request has no content")
    void updateImage_Success_IfImageExists_AndRequestHasNoContent() {

        // Data
        ImageGrpcRequest noContentRequest = ImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.USER)
                .setEntityId(entityId.toString())
                .setContent(ByteString.EMPTY)
                .build();

        // Stubs
        when(metadataRepository.findByEntityTypeAndEntityId(EntityType.USER, entityId))
                .thenReturn(Optional.of(metadataEntity));

        // Steps
        imageService.updateImage(noContentRequest, emptyResponseObserver);

        // Assertions
        verify(metadataRepository).findByEntityTypeAndEntityId(EntityType.USER, entityId);
        verify(emptyResponseObserver).onNext(Empty.getDefaultInstance());
        verify(emptyResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("UpdateImage: throws ImageNotFoundException when image not exists")
    void updateImage_ThrowsImageNotFoundException_IfImageNotExists() {

        // Stubs
        when(metadataRepository.findByEntityTypeAndEntityId(EntityType.USER, entityId))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(ImageNotFoundException.class, () ->
                imageService.updateImage(updateRequest, emptyResponseObserver));

        verify(emptyResponseObserver, never()).onNext(any());
        verify(emptyResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("UpdateImage: throws BadRequestException when entity type is UNDEFINED")
    void updateImage_ThrowsBadRequestException_IfEntityTypeIsUndefined() {

        // Data
        ImageGrpcRequest invalidRequest = ImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.UNDEFINED)
                .setEntityId(entityId.toString())
                .setContent(ByteString.copyFromUtf8(UPDATED_IMAGE))
                .build();

        // Steps & Assertions
        assertThrows(BadRequestException.class, () ->
                imageService.updateImage(invalidRequest, emptyResponseObserver));

        verify(metadataRepository, never()).findByEntityTypeAndEntityId(any(), any());
        verify(emptyResponseObserver, never()).onNext(any());
        verify(emptyResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("UpdateImage: throws BadRequestException when entity ID is empty")
    void updateImage_ThrowsBadRequestException_IfEntityIdIsEmpty() {

        // Data
        ImageGrpcRequest invalidRequest = ImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.USER)
                .setEntityId("")
                .setContent(ByteString.copyFromUtf8(UPDATED_IMAGE))
                .build();

        // Steps & Assertions
        assertThrows(BadRequestException.class, () ->
                imageService.updateImage(invalidRequest, emptyResponseObserver));

        verify(metadataRepository, never()).findByEntityTypeAndEntityId(any(), any());
        verify(emptyResponseObserver, never()).onNext(any());
        verify(emptyResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("DeleteImage: delete metadata")
    void deleteImage_Success_IfImageExists() {

        // Stubs
        when(metadataRepository.findByEntityTypeAndEntityId(EntityType.USER, entityId))
                .thenReturn(Optional.of(metadataEntity));

        // Steps
        imageService.deleteImage(deleteRequest, emptyResponseObserver);

        // Assertions
        verify(metadataRepository).delete(metadataEntity);
        verify(emptyResponseObserver).onNext(Empty.getDefaultInstance());
        verify(emptyResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("DeleteImage: does nothing when image not exists")
    void deleteImage_DoesNothing_IfImageNotExists() {

        // Stubs
        when(metadataRepository.findByEntityTypeAndEntityId(EntityType.USER, entityId))
                .thenReturn(Optional.empty());

        // Steps
        imageService.deleteImage(deleteRequest, emptyResponseObserver);

        // Assertions
        verify(metadataRepository, never()).delete(any(ImageMetadataEntity.class));
        verify(emptyResponseObserver).onNext(Empty.getDefaultInstance());
        verify(emptyResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("DeleteImage: throws BadRequestException when entity type is UNDEFINED")
    void deleteImage_ThrowsBadRequestException_IfEntityTypeIsUndefined() {

        // Data
        FindImageGrpcRequest invalidRequest = FindImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.UNDEFINED)
                .setEntityId(entityId.toString())
                .build();

        // Steps & Assertions
        assertThrows(BadRequestException.class, () ->
                imageService.deleteImage(invalidRequest, emptyResponseObserver));

        verify(metadataRepository, never()).findByEntityTypeAndEntityId(any(EntityType.class), any(UUID.class));
        verify(metadataRepository, never()).delete(any(ImageMetadataEntity.class));
        verify(emptyResponseObserver, never()).onNext(any());
        verify(emptyResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("DeleteImage: throws BadRequestException when entity ID is empty")
    void deleteImage_ThrowsBadRequestException_IfEntityIdIsEmpty() {

        // Data
        FindImageGrpcRequest invalidRequest = FindImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.USER)
                .setEntityId("")
                .build();

        // Steps & Assertions
        assertThrows(BadRequestException.class, () ->
                imageService.deleteImage(invalidRequest, emptyResponseObserver));

        verify(metadataRepository, never()).findByEntityTypeAndEntityId(any(EntityType.class), any(UUID.class));
        verify(metadataRepository, never()).delete(any(ImageMetadataEntity.class));
        verify(emptyResponseObserver, never()).onNext(any());
        verify(emptyResponseObserver, never()).onCompleted();

    }

}
