package org.rococo.users.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.users.*;
import org.rococo.users.data.UserRepository;
import org.rococo.users.ex.UserAlreadyExistException;
import org.rococo.users.ex.UserNotFoundException;
import org.rococo.users.mapper.UserMapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class UserGrpcService extends UsersServiceGrpc.UsersServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void create(CreateUserGrpcRequest request,
                       StreamObserver<UserGrpcResponse> responseObserver
    ) {

        log.info("Add new user: {}", request);

        userRepository.findByUsername(request.getUsername())
                .ifPresentOrElse(
                        user -> {
                            throw new UserAlreadyExistException(request.getUsername());
                        },
                        () -> responseObserver.onNext(
                                UserMapper.toGrpcResponse(
                                        userRepository.save(
                                                UserMapper.fromGrpcRequest(request)))));

        responseObserver.onCompleted();

    }

    @Override
    public void findById(IdType request,
                         StreamObserver<UserGrpcResponse> responseObserver
    ) {

        log.info("Find user by id: {}", request.getId());

        userRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        user -> responseObserver.onNext(UserMapper.toGrpcResponse(user)),
                        () -> {
                            throw new UserNotFoundException(UUID.fromString(request.getId()));
                        });

        responseObserver.onCompleted();

    }

    @Override
    public void findByUsername(NameType request,
                               StreamObserver<UserGrpcResponse> responseObserver
    ) {

        log.info("Find user by username: {}", request.getName());

        userRepository.findByUsername(request.getName())
                .ifPresentOrElse(
                        user -> responseObserver.onNext(UserMapper.toGrpcResponse(user)),
                        () -> {
                            throw new UserNotFoundException(request.getName());
                        });

        responseObserver.onCompleted();

    }

    @Override
    public void findAll(UsersFilterGrpcRequest filter,
                        StreamObserver<UsersGrpcResponse> responseObserver
    ) {

        log.info("Find all users by params: {}", filter);

        responseObserver.onNext(
                UserMapper.toPageGrpc(
                        userRepository.findAll(
                                UserMapper.fromPageableGrpc(filter.getPageable()))));

        responseObserver.onCompleted();

    }

    @Override
    public void update(UpdateUserGrpcRequest request,
                       StreamObserver<UserGrpcResponse> responseObserver
    ) {

        log.info("Update user: {}", request);

        userRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        user -> responseObserver.onNext(
                                UserMapper.toGrpcResponse(
                                        userRepository.save(
                                                UserMapper.updateFromGrpcRequest(user, request)))),
                        () -> {
                            throw new UserNotFoundException(UUID.fromString(request.getId()));
                        });

        responseObserver.onCompleted();

    }

    @Override
    public void removeById(IdType request,
                           StreamObserver<Empty> responseObserver
    ) {

        log.info("Delete user by id: {}", request.getId());

        userRepository.findById(UUID.fromString(request.getId()))
                .ifPresent(
                        user -> {
                            userRepository.delete(user);
                            responseObserver.onNext(Empty.newBuilder().build());
                        });

        responseObserver.onCompleted();

    }

}
