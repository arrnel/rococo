package org.rococo.users.mapper;

import org.rococo.grpc.common.page.DirectionGrpc;
import org.rococo.grpc.common.page.PageableGrpc;
import org.rococo.grpc.users.CreateUserGrpcRequest;
import org.rococo.grpc.users.UpdateUserGrpcRequest;
import org.rococo.grpc.users.UserGrpcResponse;
import org.rococo.grpc.users.UsersGrpcResponse;
import org.rococo.users.data.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class UserMapper {

    private UserMapper() {
    }

    @Nonnull
    public static UserEntity fromGrpcRequest(CreateUserGrpcRequest request) {
        return UserEntity.builder()
                .username(request.getUsername().toLowerCase())
                .firstName(request.getFirstName().isEmpty()
                        ? null
                        : request.getFirstName())
                .lastName(request.getLastName().isEmpty()
                        ? null
                        : request.getLastName())
                .build();
    }

    @Nonnull
    public static UserEntity updateFromGrpcRequest(UserEntity user, UpdateUserGrpcRequest request) {
        return UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(request.getFirstName().isEmpty()
                        ? null
                        : request.getFirstName())
                .lastName(request.getLastName().isEmpty()
                        ? null
                        : request.getLastName())
                .build();
    }

    @Nonnull
    public static UserGrpcResponse toGrpcResponse(UserEntity entity) {
        return UserGrpcResponse.newBuilder()
                .setId(entity.getId().toString())
                .setUsername(entity.getUsername())
                .setFirstName(entity.getFirstName() == null
                        ? ""
                        : entity.getFirstName())
                .setLastName(entity.getLastName() == null
                        ? ""
                        : entity.getLastName())
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
    public static UsersGrpcResponse toPageGrpc(Page<UserEntity> page) {
        return UsersGrpcResponse.newBuilder()
                .setCurrentPage(page.getPageable().getPageNumber())
                .setItemsPerPage(page.getSize())
                .setTotalItems(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .addAllData(page.getContent().stream()
                        .map(UserMapper::toGrpcResponse)
                        .toList())
                .build();
    }

}
