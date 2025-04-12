package org.rococo.artists.mapper;

import org.rococo.artists.data.ArtistEntity;
import org.rococo.grpc.artists.ArtistsGrpcResponse;
import org.rococo.grpc.common.page.DirectionGrpc;
import org.rococo.grpc.common.page.PageableGrpc;
import org.rococo.grpc.common.page.SortGrpc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class PageableMapper {

    private PageableMapper() {
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
    public static PageableGrpc toPageableGrpc(Pageable pageable) {

        final var order = pageable
                .getSort().stream()
                .map(Order::getProperty)
                .collect(Collectors.joining(","));

        final var direction = DirectionGrpc
                .valueOf(pageable
                        .getSort().iterator().next()
                        .getDirection()
                        .name());

        return PageableGrpc.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize())
                .setSort(SortGrpc.newBuilder()
                        .setOrder(order)
                        .setDirection(direction)
                        .build())
                .build();
    }

}