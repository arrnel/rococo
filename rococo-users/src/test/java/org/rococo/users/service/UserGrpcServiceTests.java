package org.rococo.users.service;

import com.google.protobuf.ByteString;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.grpc.common.page.DirectionGrpc;
import org.rococo.grpc.common.page.PageableGrpc;
import org.rococo.grpc.common.page.SortGrpc;
import org.rococo.grpc.common.type.IdType;
import org.rococo.grpc.common.type.NameType;
import org.rococo.grpc.files.ImageGrpcResponse;
import org.rococo.grpc.users.*;
import org.rococo.users.client.FilesGrpcClient;
import org.rococo.users.data.UserEntity;
import org.rococo.users.data.UserRepository;
import org.rococo.users.ex.UserAlreadyExistsException;
import org.rococo.users.ex.UserNotFoundException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserGrpcService: Module tests")
class UserGrpcServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FilesGrpcClient filesClient;

    @Mock
    private StreamObserver<UserGrpcResponse> userResponseObserver;

    @Mock
    private StreamObserver<UsersGrpcResponse> usersResponseObserver;

    @Mock
    private StreamObserver<Empty> emptyResponseObserver;

    @InjectMocks
    private UserGrpcService userGrpcService;

    private UUID userId;
    private UserEntity userEntity;
    private ImageGrpcResponse image;
    private CreateUserGrpcRequest createRequest;
    private UpdateUserGrpcRequest updateRequest;
    private IdType idRequest;
    private NameType usernameRequest;
    private UsersFilterGrpcRequest filterRequest;

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();

        userEntity = UserEntity.builder()
                .id(userId)
                .username("test.user")
                .firstName("John")
                .lastName("Doe")
                .createdDate(LocalDateTime.now())
                .build();

        image = ImageGrpcResponse.newBuilder()
                .setEntityId(userId.toString())
                .setContent(ByteString.copyFromUtf8("image-data"))
                .build();

        createRequest = CreateUserGrpcRequest.newBuilder()
                .setUsername("test.user")
                .setFirstName("John")
                .setLastName("Doe")
                .setPhoto("image-data")
                .build();

        updateRequest = UpdateUserGrpcRequest.newBuilder()
                .setId(userId.toString())
                .setFirstName("Jane")
                .setLastName("Doe")
                .setPhoto("updated-image")
                .build();

        idRequest = IdType.newBuilder()
                .setId(userId.toString())
                .build();

        usernameRequest = NameType.newBuilder()
                .setName("test.user")
                .build();

        filterRequest = UsersFilterGrpcRequest.newBuilder()
                .setOriginalPhoto(true)
                .setPageable(PageableGrpc.newBuilder()
                        .setPage(0)
                        .setSize(10)
                        .setSort(SortGrpc.newBuilder()
                                .setOrder("username")
                                .setDirection(DirectionGrpc.ASC)
                                .build())
                        .build())
                .build();

    }

    @Test
    @DisplayName("Create: create user")
    void create_Success() {

        // Stubs
        when(userRepository.findByUsername(userEntity.getUsername()))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(userEntity);

        // Steps
        userGrpcService.create(createRequest, userResponseObserver);

        // Assertions
        verify(userRepository).save(any(UserEntity.class));
        verify(filesClient).add(userId, image.getContent().toStringUtf8());
        verify(userResponseObserver).onNext(any(UserGrpcResponse.class));
        verify(userResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Create: throws UserAlreadyExistsException if username exists")
    void create_ThrowUserAlreadyExistsException_IfUserWithSameUsernameExists() {

        // Stubs
        when(userRepository.findByUsername(userEntity.getUsername()))
                .thenReturn(Optional.of(userEntity));

        // Steps & Assertions
        assertThrows(UserAlreadyExistsException.class, () ->
                userGrpcService.create(createRequest, userResponseObserver));

        verify(userRepository, never()).save(any());
        verify(filesClient, never()).add(any(), any());
        verify(userResponseObserver, never()).onNext(any());
        verify(userResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("FindById: returns user")
    void findById_Success() {

        // Stubs
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));
        when(filesClient.findImage(userId))
                .thenReturn(Optional.of(image));

        // Steps
        userGrpcService.findById(idRequest, userResponseObserver);

        // Assertions
        verify(userResponseObserver).onNext(any(UserGrpcResponse.class));
        verify(userResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("FindById: throws UserNotFoundException when user not exists")
    void findById_ThrowsUserNotFoundException_IfUserDoesNotExist() {

        // Stubs
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(UserNotFoundException.class, () ->
                userGrpcService.findById(idRequest, userResponseObserver));

        verify(userResponseObserver, never()).onNext(any());
        verify(userResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("FindByUsername: returns user")
    void findByUsername_Success() {

        // Stubs
        when(userRepository.findByUsername(userEntity.getUsername()))
                .thenReturn(Optional.of(userEntity));
        when(filesClient.findImage(userId))
                .thenReturn(Optional.of(image));

        // Steps
        userGrpcService.findByUsername(usernameRequest, userResponseObserver);

        // Assertions
        verify(userResponseObserver).onNext(any(UserGrpcResponse.class));
        verify(userResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("FindByUsername: throws UserNotFoundException if user not exists")
    void findByUsername_ThrowsUserNotFoundException_IfUserDoesNotExist() {

        // Stubs
        when(userRepository.findByUsername(userEntity.getUsername()))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(UserNotFoundException.class, () ->
                userGrpcService.findByUsername(usernameRequest, userResponseObserver));

        verify(userResponseObserver, never()).onNext(any());
        verify(userResponseObserver, never()).onCompleted();
    }

    @Test
    @DisplayName("FindAll: returns users")
    void findAll_Success() {

        // Data
        final var page = new PageImpl<>(List.of(userEntity), PageRequest.of(0, 10), 1);

        // Stubs
        when(userRepository.findAll(any(Pageable.class)))
                .thenReturn(page);
        when(filesClient.findAllByIds(List.of(userId), true))
                .thenReturn(List.of(image));

        // Steps
        userGrpcService.findAll(filterRequest, usersResponseObserver);

        // Assertions
        verify(usersResponseObserver).onNext(any(UsersGrpcResponse.class));
        verify(usersResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: update user and send request to update photo")
    void update_Success_IfUserPhotoExists_AndRequestContainsPhoto() {

        // Stubs
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(userEntity);
        when(filesClient.findImage(userId))
                .thenReturn(Optional.of(image));

        // Steps
        userGrpcService.update(updateRequest, userResponseObserver);

        verify(userRepository).save(any(UserEntity.class));
        verify(filesClient).update(userId, "updated-image");
        verify(userResponseObserver).onNext(any(UserGrpcResponse.class));
        verify(userResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: update user and send request to delete photo when request doesn't have photo")
    void update_Success_IfUserPhotoExists_AndRequestNotContainsPhoto() {

        // Data
        final var userRequest = UpdateUserGrpcRequest.newBuilder()
                .setId(userId.toString())
                .setFirstName("Jane")
                .setLastName("Doe")
                .setPhoto("")
                .build();

        final var savedUserEntity = UserEntity.builder()
                .id(userId)
                .username(userEntity.getUsername())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .build();

        final var userResponse = UserGrpcResponse.newBuilder()
                .setId(userRequest.getId())
                .setUsername(userEntity.getUsername())
                .setFirstName(userRequest.getFirstName())
                .setLastName(userRequest.getLastName())
                .setPhoto(userRequest.getPhoto())
                .build();

        // Stubs
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(savedUserEntity);
        when(filesClient.findImage(userId))
                .thenReturn(Optional.of(image));

        // Steps
        userGrpcService.update(userRequest, userResponseObserver);

        verify(userRepository).save(any(UserEntity.class));
        verify(filesClient).delete(userId);
        verify(userResponseObserver).onNext(userResponse);
        verify(userResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: update user and send request to add photo when photo not exists in files service")
    void update_Success_IfUserPhotoNotExists_AndRequestContainsPhoto() {

        // Data
        final var userRequest = UpdateUserGrpcRequest.newBuilder()
                .setId(userId.toString())
                .setFirstName("Jane")
                .setLastName("Doe")
                .setPhoto("new-user-photo")
                .build();

        final var savedUserEntity = UserEntity.builder()
                .id(userId)
                .username(userEntity.getUsername())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .build();

        final var userResponse = UserGrpcResponse.newBuilder()
                .setId(userRequest.getId())
                .setUsername(userEntity.getUsername())
                .setFirstName(userRequest.getFirstName())
                .setLastName(userRequest.getLastName())
                .setPhoto(userRequest.getPhoto())
                .build();

        // Stubs
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(savedUserEntity);
        when(filesClient.findImage(userId))
                .thenReturn(Optional.empty());

        // Steps
        userGrpcService.update(userRequest, userResponseObserver);

        // Assertions
        verify(userRepository).save(any(UserEntity.class));
        verify(filesClient).add(userId, userRequest.getPhoto());
        verify(userResponseObserver).onNext(userResponse);
        verify(userResponseObserver).onCompleted();

    }

    @Test
    @DisplayName("Update: throws UserNotFoundException when user not found")
    void update_ThrowsUserNotFoundException_IfUserNotFound() {

        // Stubs
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(UserNotFoundException.class, () ->
                userGrpcService.update(updateRequest, userResponseObserver));

        verify(userRepository, never()).save(any());
        verify(filesClient, never()).update(any(), any());
        verify(userResponseObserver, never()).onNext(any());
        verify(userResponseObserver, never()).onCompleted();

    }

    @Test
    @DisplayName("RemoveById: delete user and send delete user photo request when user founded")
    void removeById_Success() {

        // Steps
        userGrpcService.removeById(idRequest, emptyResponseObserver);

        verify(userRepository).deleteById(userId);
        verify(filesClient).delete(userId);
        verify(emptyResponseObserver).onNext(Empty.newBuilder().build());
        verify(emptyResponseObserver).onCompleted();

    }

}
