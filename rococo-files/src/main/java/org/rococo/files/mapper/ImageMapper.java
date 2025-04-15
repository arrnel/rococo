package org.rococo.files.mapper;

import com.google.protobuf.ByteString;
import org.rococo.files.config.AppProperty;
import org.rococo.files.data.entity.EntityType;
import org.rococo.files.data.entity.ImageContentEntity;
import org.rococo.files.data.entity.ImageFilter;
import org.rococo.files.data.entity.ImageMetadataEntity;
import org.rococo.files.ex.BadRequestException;
import org.rococo.files.util.HashUtil;
import org.rococo.files.util.ImageUtil;
import org.rococo.grpc.common.page.PageableGrpc;
import org.rococo.grpc.files.EntityTypeGrpc;
import org.rococo.grpc.files.FindImagesGrpcRequest;
import org.rococo.grpc.files.ImageGrpcRequest;
import org.rococo.grpc.files.ImageGrpcResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.util.UUID;

public class ImageMapper {

    private ImageMapper() {
    }

    public static ImageMetadataEntity fromGrpcRequest(ImageGrpcRequest request) {

        final var originalImage = request.getContent().toString(Charset.defaultCharset());
        final var format = ImageUtil.getFormatName(originalImage);

        final var content = ImageContentEntity.builder()
                .data(request.getContent().toByteArray())
                .thumbnailData(
                        ImageUtil.resizeImage(
                                        originalImage,
                                        format,
                                        AppProperty.MIN_IMAGE_WIDTH,
                                        AppProperty.MIN_IMAGE_HEIGHT,
                                        AppProperty.QUALITY)
                                .orElseThrow(() -> new BadRequestException("Can not resize image"))
                )
                .build();

        return ImageMetadataEntity.builder()
                .format(format)
                .entityType(request.getEntityType() == EntityTypeGrpc.UNDEFINED
                        ? null
                        : EntityType.valueOf(request.getEntityType().name()))
                .entityId(request.getEntityId().isEmpty()
                        ? null
                        : UUID.fromString(request.getEntityId()))
                .content(content)
                .contentHash(HashUtil.getHash(originalImage))
                .build();
    }

    @Nonnull
    public static ImageMetadataEntity updateFromGrpcRequest(ImageMetadataEntity entity, ImageGrpcRequest request) {
        final var metadata = fromGrpcRequest(request);
        entity.setFormat(metadata.getFormat())
                .setContentHash(metadata.getContentHash())
                .getContent()
                .setData(metadata.getContent().getData())
                .setThumbnailData(metadata.getContent().getData());
        return metadata;
    }

    public static ImageGrpcResponse toGrpcResponse(ImageMetadataEntity entity, boolean isOriginal) {

        final var content = isOriginal
                ? entity.getContent().getData()
                : entity.getContent().getThumbnailData();

        return ImageGrpcResponse.newBuilder()
                .setEntityId(
                        entity.getEntityId() == null
                                ? ""
                                : entity.getEntityId().toString())
                .setContent(
                        content == null
                                ? ByteString.empty()
                                : ByteString.copyFrom(content))
                .build();
    }

    public static ImageFilter fromFilterGrpc(FindImagesGrpcRequest request) {
        return ImageFilter.builder()
                .entityType(
                        request.getEntityType() == EntityTypeGrpc.UNDEFINED
                                ? null
                                : EntityType.valueOf(request.getEntityType().name())
                )
                .entityIds(
                        request.getEntityIdsList().stream()
                                .map(UUID::fromString)
                                .toArray(UUID[]::new))
                .isThumbnails(true)
                .build();
    }

    public static Pageable fromPageableGrpc(PageableGrpc pageable) {
        final var direction = Direction.valueOf(pageable
                .getSort()
                .getDirection()
                .name());

        return PageRequest.of(
                pageable.getPage(),
                pageable.getSize(),
                Sort.by(direction,
                        pageable.getSort().getOrder().split(","))
        );
    }

    public static void update(ImageMetadataEntity oldMetadata, ImageMetadataEntity newMetadata) {
        oldMetadata.setFormat(newMetadata.getFormat())
                .setContentHash(newMetadata.getContentHash())
                .getContent()
                .setData(newMetadata.getContent().getData())
                .setThumbnailData(newMetadata.getContent().getThumbnailData());

    }
}
