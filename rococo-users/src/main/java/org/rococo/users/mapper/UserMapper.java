package org.rococo.users.mapper;

import org.rococo.grpc.users.CreateUserGrpcRequest;
import org.rococo.grpc.users.UpdateUserGrpcRequest;
import org.rococo.grpc.users.UserGrpcResponse;
import org.rococo.grpc.users.UsersGrpcResponse;
import org.rococo.users.data.UserEntity;
import org.springframework.data.domain.Page;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;
import java.util.UUID;

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
                .createdDate(user.getCreatedDate())
                .build();
    }

    @Nonnull
    public static UserGrpcResponse toGrpcResponse(UserEntity entity, @Nullable String photo) {
        return UserGrpcResponse.newBuilder()
                .setId(entity.getId().toString())
                .setUsername(entity.getUsername())
                .setFirstName(entity.getFirstName() == null
                        ? ""
                        : entity.getFirstName())
                .setLastName(entity.getLastName() == null
                        ? ""
                        : entity.getLastName())
                .setPhoto(photo == null
                        ? ""
                        : photo)
                .build();
    }

    @Nonnull
    public static UsersGrpcResponse toPageGrpc(Page<UserEntity> page, Map<UUID, String> photos) {
        return UsersGrpcResponse.newBuilder()
                .setCurrentPage(page.getPageable().getPageNumber())
                .setItemsPerPage(page.getSize())
                .setTotalItems(page.getTotalElements())
                .setTotalPages(page.getTotalPages())
                .addAllData(page.getContent().stream()
                        .map(user -> UserMapper.toGrpcResponse(user, photos.get(user.getId())))
                        .toList())
                .build();
    }

}
