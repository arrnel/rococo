package org.rococo.gateway.mapper;

import com.google.protobuf.ByteString;
import org.rococo.gateway.model.EntityType;
import org.rococo.gateway.model.files.ImageDTO;
import org.rococo.grpc.artists.ArtistsFilterGrpcRequest;
import org.rococo.grpc.files.EntityTypeGrpc;
import org.rococo.grpc.files.FindImageGrpcRequest;
import org.rococo.grpc.files.ImageGrpcRequest;
import org.rococo.grpc.files.ImageGrpcResponse;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class ImageMapper {

    private ImageMapper() {
    }

    @Nonnull
    public static ImageGrpcRequest toGrpcModel(final EntityType entityType,
                                               final UUID entityId,
                                               final String image) {
        return ImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.valueOf(entityType.name()))
                .setEntityId(entityId.toString())
                .setContent(ByteString.copyFrom(image, StandardCharsets.UTF_8))
                .build();
    }

    public static FindImageGrpcRequest toFindGrpcRequest(final EntityType entityType, final UUID entityId) {
        return FindImageGrpcRequest.newBuilder()
                .setEntityType(EntityTypeGrpc.valueOf(entityType.name()))
                .setEntityId(entityId.toString())
                .build();
    }

    @Nonnull
    public static ImageDTO toDTO(final ImageGrpcResponse grpcResponseModel) {
        return ImageDTO.builder()
                .entityId(grpcResponseModel.getEntityId().isEmpty()
                        ? null
                        : UUID.fromString(grpcResponseModel.getEntityId()))
                .content(grpcResponseModel.getContent().isEmpty()
                        ? null
                        : grpcResponseModel.getContent().toString(Charset.defaultCharset()))
                .build();
    }

    @Nonnull
    public static ArtistsFilterGrpcRequest toFilter(@Nullable final String name,
                                                    final Pageable pageable) {
        return ArtistsFilterGrpcRequest.newBuilder()
                .setQuery(name == null
                        ? ""
                        : name)
                .setPageable(
                        PageableMapper.toPageableGrpc(pageable))
                .build();
    }

}
