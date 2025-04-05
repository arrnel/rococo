package org.rococo.gateway.mapper;

import com.google.protobuf.ByteString;
import org.rococo.gateway.model.users.CreateUserRequestDTO;
import org.rococo.gateway.model.users.UpdateUserRequestDTO;
import org.rococo.gateway.model.users.UserDTO;
import org.rococo.gateway.model.users.UserShortDTO;
import org.rococo.grpc.users.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserMapper {

    private UserMapper() {
    }

    @Nonnull
    public static CreateUserGrpcRequest toGrpcModel(final CreateUserRequestDTO requestDTO) {
        return CreateUserGrpcRequest.newBuilder()
                .setUsername(requestDTO.username() == null
                        ? ""
                        : requestDTO.username())
                .setFirstName(requestDTO.firstName() == null
                        ? ""
                        : requestDTO.firstName())
                .setLastName(requestDTO.lastName() == null
                        ? ""
                        : requestDTO.lastName())
                .setPhoto(requestDTO.photo() == null
                        ? ByteString.EMPTY
                        : ByteString.copyFrom(requestDTO.photo(), StandardCharsets.UTF_8))
                .build();
    }

    @Nonnull
    public static UpdateUserGrpcRequest toGrpcModel(final UUID id,
                                                    final UpdateUserRequestDTO requestDTO
    ) {
        return UpdateUserGrpcRequest.newBuilder()
                .setId(id.toString())
                .setFirstName(requestDTO.firstName() == null
                        ? ""
                        : requestDTO.firstName())
                .setLastName(requestDTO.lastName() == null
                        ? ""
                        : requestDTO.lastName())
                .setPhoto(requestDTO.photo() == null
                        ? ByteString.EMPTY
                        : ByteString.copyFrom(requestDTO.photo(), StandardCharsets.UTF_8))
                .build();
    }

    @Nonnull
    public static UserDTO toDTO(final UserGrpcResponse grpcResponseModel) {
        return UserDTO.builder()
                .id(grpcResponseModel.getId().isEmpty()
                        ? null
                        : UUID.fromString(grpcResponseModel.getId()))
                .username(grpcResponseModel.getUsername().isEmpty()
                        ? null
                        : grpcResponseModel.getUsername())
                .firstName(grpcResponseModel.getFirstName().isEmpty()
                        ? null
                        : grpcResponseModel.getFirstName())
                .lastName(grpcResponseModel.getLastName().isEmpty()
                        ? null
                        : grpcResponseModel.getLastName())
                .photo(grpcResponseModel.getPhoto().isEmpty()
                        ? null
                        : grpcResponseModel.getPhoto().toString(StandardCharsets.UTF_8))
                .build();
    }

    @Nonnull
    public static UserShortDTO toShortDTO(final UserDTO userDTO) {
        return UserShortDTO.builder()
                .id(userDTO.getId())
                .username(userDTO.getUsername())
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

}
