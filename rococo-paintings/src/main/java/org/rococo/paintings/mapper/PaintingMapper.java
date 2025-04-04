package org.rococo.paintings.mapper;

import org.rococo.grpc.common.page.DirectionGrpc;
import org.rococo.grpc.common.page.PageableGrpc;
import org.rococo.grpc.paintings.*;
import org.rococo.paintings.data.PaintingEntity;
import org.rococo.paintings.model.PaintingFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class PaintingMapper {

    private PaintingMapper() {
    }

    @Nonnull
    public static PaintingEntity fromGrpcRequest(AddPaintingGrpcRequest request) {
        return PaintingEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription().isEmpty()
                        ? null
                        : request.getDescription())
                .artistId(UUID.fromString(request.getArtistId()))
                .museumId(UUID.fromString(request.getMuseumId()))
                .build();
    }

    @Nonnull
    public static PaintingEntity updateFromGrpcRequest(PaintingEntity entity, UpdatePaintingGrpcRequest request) {
        return PaintingEntity.builder()
                .id(entity.getId())
                .title(request.getTitle())
                .description(request.getDescription().isEmpty()
                        ? null
                        : request.getDescription())
                .artistId(UUID.fromString(request.getArtistId()))
                .museumId(UUID.fromString(request.getMuseumId()))
                .build();
    }

    @Nonnull
    public static PaintingGrpcResponse toGrpcResponse(PaintingEntity entity) {
        return PaintingGrpcResponse.newBuilder()
                .setId(entity.getId().toString())
                .setTitle(entity.getTitle())
                .setDescription(entity.getDescription() == null
                        ? ""
                        : entity.getDescription())
                .setArtistId(entity.getArtistId().toString())
                .setMuseumId(entity.getMuseumId().toString())
                .build();
    }

    @Nonnull
    public static PaintingFilter fromGrpcFilter(PaintingsFilterGrpcRequest request) {
        return PaintingFilter.builder()
                .query(request.getQuery().isEmpty()
                        ? null
                        : request.getQuery())
                .artistId(request.getArtistId().isEmpty()
                        ? null
                        : UUID.fromString(request.getArtistId()))
                .museumId(request.getMuseumId().isEmpty()
                        ? null
                        : UUID.fromString(request.getMuseumId()))
                .build();
    }

    @Nonnull
    public static Pageable fromPageableGrpc(PageableGrpc pageable) {

        final var grpcDirection = pageable.getSort().getDirection();
        final var direction = (grpcDirection == DirectionGrpc.DEFAULT || grpcDirection == DirectionGrpc.ASC)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return PageRequest.of(
                pageable.getPage(),
                pageable.getSize(),
                pageable.getSort().getOrder().isEmpty()
                        ? Sort.unsorted()
                        : Sort.by(direction, pageable.getSort().getOrder().split(","))
        );
    }

    @Nonnull
    public static PaintingsGrpcResponse toPageGrpc(Page<PaintingEntity> page) {
        return PaintingsGrpcResponse.newBuilder()
                .setCurrentPage(page.getPageable().getPageNumber())
                .setItemsPerPage(page.getSize())
                .setTotalItems(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .addAllData(page.getContent().stream()
                        .map(PaintingMapper::toGrpcResponse)
                        .toList())
                .build();
    }

}
