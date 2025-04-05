package org.rococo.tests.mapper;

import org.rococo.grpc.common.page.DirectionGrpc;
import org.rococo.grpc.common.page.PageableGrpc;
import org.rococo.grpc.common.page.SortGrpc;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class PageableMapper {

    private PageableMapper() {
    }

    @Nonnull
    public static PageableGrpc toPageableGrpc(Pageable pageable) {

        final var order = pageable
                .getSort().stream()
                .map(Sort.Order::getProperty)
                .collect(Collectors.joining(","));


        final var direction = pageable.getSort().iterator().hasNext()
                ? DirectionGrpc.valueOf(pageable
                .getSort().iterator().next()
                .getDirection()
                .name())
                : DirectionGrpc.ASC;

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
