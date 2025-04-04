package org.rococo.artists.mapper;

import org.rococo.artists.data.ArtistEntity;
import org.rococo.artists.model.ArtistFilter;
import org.rococo.grpc.artists.*;
import org.rococo.grpc.common.page.DirectionGrpc;
import org.rococo.grpc.common.page.PageableGrpc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ArtistMapper {

    private ArtistMapper() {
    }

    @Nonnull
    public static ArtistEntity fromGrpcRequest(AddArtistGrpcRequest request) {
        return ArtistEntity.builder()
                .name(request.getName().isEmpty()
                        ? null
                        : request.getName())
                .biography(request.getBiography().isEmpty()
                        ? null
                        : request.getBiography())
                .build();
    }

    @Nonnull
    public static ArtistEntity updateFromGrpcRequest(ArtistEntity entity, UpdateArtistGrpcRequest request) {
        return ArtistEntity.builder()
                .id(entity.getId())
                .name(request.getName().isEmpty()
                        ? null
                        : request.getName())
                .biography(request.getBiography().isEmpty()
                        ? null
                        : request.getBiography())
                .build();
    }

    @Nonnull
    public static ArtistGrpcResponse toGrpcResponse(ArtistEntity entity) {
        return ArtistGrpcResponse.newBuilder()
                .setId(entity.getId().toString())
                .setName(entity.getName() == null
                        ? ""
                        : entity.getName())
                .setBiography(entity.getBiography() == null
                        ? ""
                        : entity.getBiography())
                .build();
    }

    @Nonnull
    public static ArtistFilter fromGrpcFilter(ArtistsFilterGrpcRequest request) {
        return ArtistFilter.builder()
                .query(request.getQuery())
                .build();
    }

    @Nonnull
    public static Pageable fromPageableGrpc(PageableGrpc pageable) {

        final var grpcDirection = pageable.getSort().getDirection();
        final var direction = (grpcDirection == DirectionGrpc.DEFAULT || grpcDirection == DirectionGrpc.ASC)
                ? Direction.ASC
                : Direction.DESC;

        return PageRequest.of(
                pageable.getPage(),
                pageable.getSize(),
                pageable.getSort().getOrder().isEmpty()
                        ? Sort.unsorted()
                        : Sort.by(direction, pageable.getSort().getOrder().split(","))
        );
    }

    @Nonnull
    public static ArtistsGrpcResponse toPageGrpc(Page<ArtistEntity> page) {
        return ArtistsGrpcResponse.newBuilder()
                .setCurrentPage(page.getPageable().getPageNumber())
                .setItemsPerPage(page.getSize())
                .setTotalItems(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .addAllData(page.getContent().stream()
                        .map(ArtistMapper::toGrpcResponse)
                        .toList())
                .build();
    }

}
