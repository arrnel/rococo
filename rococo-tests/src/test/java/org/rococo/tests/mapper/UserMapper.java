package org.rococo.tests.mapper;

import org.rococo.grpc.users.*;
import org.rococo.tests.data.entity.UserEntity;
import org.rococo.tests.jupiter.annotation.User;
import org.rococo.tests.model.AuthUserDTO;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.util.ImageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserMapper {

    @Nonnull
    public static UserEntity toEntity(UserDTO user) {
        return UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    @Nonnull
    public static AuthUserDTO toAuthDTO(UserDTO user) {
        return AuthUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getTestData().getPassword())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }

    @Nonnull
    public static UserDTO toDTO(UserEntity userEntity) {
        return UserDTO.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .build();
    }

    @Nonnull
    public static UserDTO toDTO(UserEntity userEntity, @Nullable byte[] photo) {
        return UserDTO.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .photo(
                        photo != null && photo.length > 0
                                ? new String(photo, StandardCharsets.UTF_8)
                                : null)
                .build();
    }

    @Nonnull
    public static UserDTO toDTO(UserGrpcResponse grpcResponse) {
        return UserDTO.builder()
                .id(grpcResponse.getId().isEmpty()
                        ? null
                        : UUID.fromString(grpcResponse.getId()))
                .username(grpcResponse.getUsername().isEmpty()
                        ? null
                        : grpcResponse.getUsername())
                .firstName(grpcResponse.getFirstName().isEmpty()
                        ? null
                        : grpcResponse.getFirstName())
                .lastName(grpcResponse.getLastName().isEmpty()
                        ? null
                        : grpcResponse.getLastName())
                .build();
    }

    public static UserEntity updateFromDTO(UserEntity userEntity, UserDTO userDTO) {
        return UserEntity.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .build();
    }

    public static CreateUserGrpcRequest toGrpcRequest(UserDTO userDTO) {
        return CreateUserGrpcRequest.newBuilder()
                .setUsername(userDTO.getUsername() == null
                        ? ""
                        : userDTO.getUsername())
                .setFirstName(userDTO.getId() == null
                        ? ""
                        : userDTO.getFirstName())
                .setLastName(userDTO.getId() == null
                        ? ""
                        : userDTO.getLastName())
                .build();
    }

    public static UpdateUserGrpcRequest toUpdateGrpcRequest(UserDTO userDTO) {
        return UpdateUserGrpcRequest.newBuilder()
                .setId(userDTO.getId() == null
                        ? ""
                        : userDTO.getId().toString())
                .setFirstName(userDTO.getId() == null
                        ? ""
                        : userDTO.getFirstName())
                .setLastName(userDTO.getId() == null
                        ? ""
                        : userDTO.getLastName())
                .build();
    }

    @Nonnull
    public static UsersFilterGrpcRequest toFilter(final Pageable pageable) {
        return UsersFilterGrpcRequest.newBuilder()
                .setPageable(
                        PageableMapper.toPageableGrpc(pageable))
                .build();
    }

    @Nonnull
    public static Page<UserDTO> toPageDTO(final UsersGrpcResponse response) {
        return new PageImpl<>(
                response.getDataList().stream()
                        .map(UserMapper::toDTO)
                        .toList(),
                PageRequest.of(response.getCurrentPage(),
                        response.getItemsPerPage()),
                response.getTotalItems()
        );
    }

    @Nonnull
    public static UserDTO updateFromAnno(UserDTO user, User anno) {
        return UserDTO.builder()
                .id(user.getId())
                .username(anno.username().isEmpty()
                        ? user.getUsername()
                        : anno.username())
                .firstName(anno.firstName().isEmpty()
                        ? user.getFirstName()
                        : anno.firstName())
                .lastName(anno.lastName().isEmpty()
                        ? user.getLastName()
                        : anno.lastName())
                .photo(anno.pathToPhoto().isEmpty()
                        ? user.getPhoto()
                        : ImageUtil.imageFileToBase64(Path.of(anno.pathToPhoto())))
                .build()
                .password(anno.password().isEmpty()
                        ? user.password()
                        : anno.password());
    }

}