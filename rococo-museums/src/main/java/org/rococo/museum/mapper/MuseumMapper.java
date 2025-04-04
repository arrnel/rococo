package org.rococo.museum.mapper;

import org.rococo.grpc.common.page.DirectionGrpc;
import org.rococo.grpc.common.page.PageableGrpc;
import org.rococo.grpc.museums.*;
import org.rococo.museum.data.MuseumEntity;
import org.rococo.museum.model.MuseumFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class MuseumMapper {

    private MuseumMapper() {
    }

    @Nonnull
    public static MuseumEntity fromGrpcRequest(AddMuseumGrpcRequest request) {
        return MuseumEntity.builder()
                .title(request.getTitle().isEmpty()
                        ? null
                        : request.getTitle())
                .description(request.getDescription().isEmpty()
                        ? null
                        : request.getDescription())
                .countryId(request.getCountryId().isEmpty()
                        ? null
                        : UUID.fromString(request.getCountryId()))
                .city(request.getCity())
                .build();
    }

    @Nonnull
    public static MuseumEntity updateFromGrpcRequest(MuseumEntity entity, UpdateMuseumGrpcRequest request) {
        return MuseumEntity.builder()
                .id(entity.getId())
                .title(request.getTitle().isEmpty()
                        ? null
                        : request.getTitle())
                .description(request.getDescription().isEmpty()
                        ? null
                        : request.getDescription())
                .countryId(request.getCountryId().isEmpty()
                        ? null
                        : UUID.fromString(request.getCountryId()))
                .city(request.getCity().isEmpty()
                        ? null
                        : request.getCity())
                .build();
    }

    @Nonnull
    public static MuseumGrpcResponse toGrpcResponse(MuseumEntity entity) {
        return MuseumGrpcResponse.newBuilder()
                .setId(entity.getId() == null
                        ? ""
                        : entity.getId().toString())
                .setTitle(entity.getTitle() == null
                        ? ""
                        : entity.getTitle())
                .setDescription(entity.getDescription() == null
                        ? ""
                        : entity.getDescription())
                .setCountryId(entity.getCountryId() == null
                        ? ""
                        : entity.getCountryId().toString())
                .setCity(entity.getCity() == null
                        ? ""
                        : entity.getCity())
                .build();
    }

    @Nonnull
    public static MuseumsGrpcResponse toPageGrpc(Page<MuseumEntity> page) {
        return MuseumsGrpcResponse.newBuilder()
                .setCurrentPage(page.getPageable().getPageNumber())
                .setItemsPerPage(page.getSize())
                .setTotalItems(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .addAllData(page.getContent().stream()
                        .map(MuseumMapper::toGrpcResponse)
                        .toList())
                .build();
    }

    public static MuseumFilter fromGrpcFilter(MuseumsFilterGrpcRequest request) {
        return MuseumFilter.builder()
                .query(request.getQuery().isEmpty()
                        ? null
                        : request.getQuery())
                .countryId(request.getCountryId().isEmpty()
                        ? null
                        : UUID.fromString(request.getCountryId()))
                .city(request.getCity().isEmpty()
                        ? null
                        : request.getCountryId())
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

}
