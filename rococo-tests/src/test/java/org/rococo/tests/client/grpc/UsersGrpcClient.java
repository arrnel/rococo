package org.rococo.tests.client.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.users.UsersServiceGrpc;
import org.rococo.tests.ex.ServiceUnavailableException;
import org.rococo.tests.ex.UserAlreadyExistsException;
import org.rococo.tests.ex.UserNotFoundException;
import org.rococo.tests.mapper.UserMapper;
import org.rococo.tests.model.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@ParametersAreNonnullByDefault
public class UsersGrpcClient extends GrpcClient {

    private static final String SERVICE_NAME = "rococo-users";
    private final UsersServiceGrpc.UsersServiceBlockingStub usersServiceStub;

    public UsersGrpcClient() {
        super(CFG.usersGrpcHost(), CFG.usersPort());
        usersServiceStub = UsersServiceGrpc.newBlockingStub(channel);
    }

    @Nonnull
    public UserDTO add(UserDTO requestDTO) {

        try {
            return UserMapper.toDTO(
                    usersServiceStub.create(
                            UserMapper.toGrpcRequest(requestDTO)));
        } catch (StatusRuntimeException ex) {
            if (ex.getStatus().getCode() == Status.Code.ALREADY_EXISTS)
                throw new UserAlreadyExistsException(requestDTO.getUsername());
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
    public UserDTO update(UserDTO requestDTO) {

        try {
            return UserMapper.toDTO(
                    usersServiceStub.update(
                            UserMapper.toUpdateGrpcRequest(requestDTO)));
        } catch (StatusRuntimeException ex) {

            if (ex.getStatus().getCode() == Status.Code.NOT_FOUND)
                throw new UserNotFoundException(requestDTO.getId());

            if (ex.getStatus().getCode() == Status.Code.ALREADY_EXISTS)
                throw new UserAlreadyExistsException(requestDTO.getUsername());

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
