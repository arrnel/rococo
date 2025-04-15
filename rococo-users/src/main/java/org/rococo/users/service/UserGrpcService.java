package org.rococo.users.service;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.files.ImageGrpcResponse;
import org.rococo.grpc.users.*;
import org.rococo.users.client.FilesGrpcClient;
import org.rococo.users.data.UserEntity;
import org.rococo.users.data.UserRepository;
import org.rococo.users.ex.UserAlreadyExistsException;
import org.rococo.users.ex.UserNotFoundException;
import org.rococo.users.mapper.PageableMapper;
import org.rococo.users.mapper.UserMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
@ParametersAreNonnullByDefault
public class UserGrpcService extends UsersServiceGrpc.UsersServiceImplBase {

    private final UserRepository userRepository;
    private final FilesGrpcClient filesClient;

    @Override
    @Transactional
    public void create(CreateUserGrpcRequest request,
                       StreamObserver<UserGrpcResponse> responseObserver
    ) {

        log.info("Add new user with username: {}", request.getUsername());

        userRepository.findByUsername(request.getUsername())
                .ifPresentOrElse(
                        user -> {
                            throw new UserAlreadyExistsException(request.getUsername());
                        },
                        () -> {
                            var savedUser = userRepository.save(UserMapper.fromGrpcRequest(request));
                            filesClient.add(savedUser.getId(), request.getPhoto());
                            responseObserver.onNext(
                                    UserMapper.toGrpcResponse(savedUser, request.getPhoto()));
                        });

        responseObserver.onCompleted();

    }

    @Override
    @Transactional(readOnly = true)
    public void findById(IdType request,
                         StreamObserver<UserGrpcResponse> responseObserver
    ) {

        log.info("Find user by id: {}", request.getId());

        userRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(user -> {
                            var photo = filesClient.findImage(user.getId())
                                    .orElse(ImageGrpcResponse.getDefaultInstance());
                            responseObserver.onNext(UserMapper.toGrpcResponse(user, photo.getContent().toStringUtf8()));
                            responseObserver.onCompleted();
                        },
                        () -> {
                            throw new UserNotFoundException(UUID.fromString(request.getId()));
                        }
                );

    }

    @Override
    @Transactional(readOnly = true)
    public void findByUsername(NameType request,
                               StreamObserver<UserGrpcResponse> responseObserver
    ) {

        log.info("Find user by username: {}", request.getName());

        userRepository.findByUsername(request.getName())
                .ifPresentOrElse(user -> {
                            var photo = filesClient.findImage(user.getId())
                                    .orElse(ImageGrpcResponse.getDefaultInstance());
                            responseObserver.onNext(UserMapper.toGrpcResponse(user, photo.getContent().toStringUtf8()));
                            responseObserver.onCompleted();
                        },
                        () -> {
                            throw new UserNotFoundException(request.getName());
                        });

    }

    @Override
    @Transactional(readOnly = true)
    public void findAll(UsersFilterGrpcRequest request,
                        StreamObserver<UsersGrpcResponse> responseObserver
    ) {

        log.info("Find all users by params: {}", request);

        var isOriginalText = request.getOriginalPhoto()
                ? "original"
                : "thumbnail";
        log.info("Find all users with {} photos by params: {}", isOriginalText, request);

        var userEntities = userRepository.findAll();

        var userIds = userEntities.stream()
                .map(UserEntity::getId)
                .distinct()
                .toList();

        var photoMap = filesClient.findAllByIds(userIds, request.getOriginalPhoto()).stream()
                .collect(Collectors.toMap(
                        photo -> UUID.fromString(photo.getEntityId()),
                        photo -> photo.getContent().toStringUtf8()));

        responseObserver.onNext(
                UserMapper.toPageGrpc(
                        userRepository.findAll(PageableMapper.fromPageableGrpc(request.getPageable())),
                        photoMap
                ));

        responseObserver.onCompleted();

    }

    @Override
    @Transactional
    public void update(UpdateUserGrpcRequest request,
                       StreamObserver<UserGrpcResponse> responseObserver
    ) {

        log.info("Update user: {}", request);

        userRepository.findById(UUID.fromString(request.getId()))
                .ifPresentOrElse(
                        user -> {
                            var updatedUser = userRepository.save(
                                    UserMapper.updateFromGrpcRequest(user, request));

                            var existPhoto = filesClient.findImage(user.getId());
                            if (existPhoto.isPresent() && !request.getPhoto().isEmpty()) {
                                filesClient.update(user.getId(), request.getPhoto());
                            } else if (existPhoto.isPresent() && request.getPhoto().isEmpty()) {
                                filesClient.delete(user.getId());
                            } else if (existPhoto.isEmpty() && !request.getPhoto().isEmpty()) {
                                filesClient.add(user.getId(), request.getPhoto());
                            }

                            responseObserver.onNext(
                                    UserMapper.toGrpcResponse(
                                            updatedUser,
                                            request.getPhoto()
                                    ));
                            responseObserver.onCompleted();

                        },
                        () -> {
                            throw new UserNotFoundException(UUID.fromString(request.getId()));
                        }
                );

        responseObserver.onCompleted();

    }

    @Override
    @Transactional
    public void removeById(IdType request,
                           StreamObserver<Empty> responseObserver
    ) {

        log.info("Delete user by id: {}", request.getId());

        userRepository.findById(UUID.fromString(request.getId()))
                .ifPresent(user -> {
                    userRepository.delete(user);
                    filesClient.delete(user.getId());
                });

        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();

    }

}
