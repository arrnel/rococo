package org.rococo.tests.mapper;

import com.google.protobuf.ByteString;
import org.rococo.grpc.files.EntityTypeGrpc;
import org.rococo.grpc.files.FindImageGrpcRequest;
import org.rococo.grpc.files.ImageGrpcRequest;
import org.rococo.grpc.files.ImageGrpcResponse;
import org.rococo.tests.data.entity.ImageContentEntity;
import org.rococo.tests.data.entity.ImageMetadataEntity;
import org.rococo.tests.enums.EntityType;
import org.rococo.tests.model.ImageDTO;
import org.rococo.tests.util.HashUtil;
import org.rococo.tests.util.ImageUtil;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class ImageMapper {

    private ImageMapper() {
    }

    @Nonnull
    public static ImageGrpcRequest toGrpcRequest(EntityType entityType, UUID entityId, String content) {
        return ImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.valueOf(entityType.name()))
                .setEntityId(entityId.toString())
                .setContent(ByteString.copyFrom(content, StandardCharsets.UTF_8))
                .build();
    }

    @Nonnull
    public static ImageDTO toDTO(ImageGrpcResponse grpcResponse) {
        return ImageDTO.builder()
                .entityId(grpcResponse.getEntityId().isEmpty()
                        ? null
                        : UUID.fromString(grpcResponse.getEntityId()))
                .content(grpcResponse.getContent().toString(Charset.defaultCharset()))
                .build();
    }

    public static FindImageGrpcRequest toFindGrpcRequest(EntityType entityType, UUID id) {
        return FindImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.valueOf(entityType.name()))
                .setEntityId(id.toString())
                .build();
    }

    @Nonnull
    public static ImageMetadataEntity fromBase64Image(EntityType entityType,
                                                      UUID entityId,
                                                      String base64Image
    ) {
        var imageFormat = ImageUtil.getFormatName(base64Image);

        return ImageMetadataEntity.builder()
                .entityType(entityType)
                .entityId(entityId)
                .format(imageFormat)
                .contentHash(HashUtil.getHash(base64Image))
                .content(
                        ImageContentEntity.builder()
                                .data(base64Image.getBytes(StandardCharsets.UTF_8))
                                .thumbnailData(
                                        ImageUtil.resizeImage(base64Image, imageFormat, 100, 100, 1.0))
                                .build()
                )
                .build();
    }

}
