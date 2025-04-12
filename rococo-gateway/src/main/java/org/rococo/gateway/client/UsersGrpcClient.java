package org.rococo.gateway.client;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.rococo.gateway.ex.ServiceUnavailableException;
import org.rococo.gateway.ex.UserAlreadyExistsException;
import org.rococo.gateway.ex.UserNotFoundException;
import org.rococo.gateway.mapper.UserMapper;
import org.rococo.gateway.model.users.CreateUserRequestDTO;
import org.rococo.gateway.model.users.UpdateUserRequestDTO;
import org.rococo.gateway.model.users.UserDTO;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.users.UsersServiceGrpc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class UsersGrpcClient {

    private static final String SERVICE_NAME = "rococo-users";

    @GrpcClient("grpcUsersClient")
    private UsersServiceGrpc.UsersServiceBlockingStub usersServiceStub;

    @Nonnull
    public UserDTO add(CreateUserRequestDTO requestDTO) {
        try {
            return UserMapper.toDTO(
                    usersServiceStub.create(
                            UserMapper.toGrpcModel(requestDTO)));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.ALREADY_EXISTS)
                throw new UserAlreadyExistsException(requestDTO.username());
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    @Nonnull
    public Optional<UserDTO> findById(UUID id) {
        try {
            return Optional.of(
                    UserMapper.toDTO(
                            usersServiceStub.findById(
                                    IdType.newBuilder()
                                            .setId(id.toString())
                                            .build())));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() != Status.Code.NOT_FOUND)
                throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
            return Optional.empty();
        }
    }

    public Optional<UserDTO> findByUsername(String username) {
        try {
            return Optional.of(
                    UserMapper.toDTO(
                            usersServiceStub.findByUsername(
                                    NameType.newBuilder()
                                            .setName(username)
                                            .build())));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() != Status.Code.NOT_FOUND)
                throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
            return Optional.empty();
        }
    }

    @Nonnull
    public Page<UserDTO> findAll(Pageable pageable) {
        try {
            return UserMapper.toPageDTO(
                    usersServiceStub.findAll(
                            UserMapper.toFilter(pageable)));
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    @Nonnull
    public UserDTO update(UUID id, UpdateUserRequestDTO requestDTO) {
        try {
            return UserMapper.toDTO(
                    usersServiceStub.update(
                            UserMapper.toGrpcModel(id, requestDTO)));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND)
                throw new UserNotFoundException(id);
            if (ex.getStatus().getCode() == Status.Code.ALREADY_EXISTS)
                throw new UserAlreadyExistsException(requestDTO.username());
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

    public void delete(UUID id) {
        try {
            usersServiceStub.removeById(
                    IdType.newBuilder()
                            .setId(id.toString())
                            .build());
        } catch (StatusRuntimeException ex) {
            throw new ServiceUnavailableException(SERVICE_NAME, ex.getStatus());
        }
    }

}