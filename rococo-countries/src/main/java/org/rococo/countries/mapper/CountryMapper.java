package org.rococo.countries.mapper;

import org.rococo.countries.data.CountryEntity;
import org.rococo.grpc.common.page.DirectionGrpc;
import org.rococo.grpc.common.page.PageableGrpc;
import org.rococo.grpc.countries.CountriesGrpcResponse;
import org.rococo.grpc.countries.CountryGrpcResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CountryMapper {

    private CountryMapper() {
    }

    @Nonnull
    public static CountryGrpcResponse toGrpcResponse(CountryEntity entity) {
        return CountryGrpcResponse.newBuilder()
                .setId(entity.getId().toString())
                .setName(entity.getName() == null
                        ? ""
                        : entity.getName())
                .setCode(entity.getCode() == null
                        ? ""
                        : entity.getCode().name())
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
    public static CountriesGrpcResponse toPageGrpc(Page<CountryEntity> page) {
        return CountriesGrpcResponse.newBuilder()
                .setCurrentPage(page.getPageable().getPageNumber())
                .setItemsPerPage(page.getSize())
                .setTotalItems(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .addAllData(page.getContent().stream()
                        .map(CountryMapper::toGrpcResponse)
                        .toList())
                .build();
    }

}
