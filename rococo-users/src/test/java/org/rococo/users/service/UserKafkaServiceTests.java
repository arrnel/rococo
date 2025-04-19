package org.rococo.users.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.users.data.UserEntity;
import org.rococo.users.data.UserRepository;
import org.rococo.users.model.UserDTO;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserKafkaService: Module tests")
class UserKafkaServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserKafkaService userKafkaService;

    private UserDTO user;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {

        user = new UserDTO("test_user");
        userEntity = UserEntity.builder()
                .username("test_user")
                .createdDate(LocalDateTime.now())
                .build();

    }

    @Test
    @DisplayName("Create: creates user when user not exists")
    void create_whenUserDoesNotExist_shouldSaveUser() {

        // Stubs
        when(userRepository.findByUsername(user.username()))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class)))
                .thenReturn(userEntity);

        // Steps
        userKafkaService.create(user);

        // Assertions
        verify(userRepository).findByUsername(user.username());
        verify(userRepository).save(any(UserEntity.class));

    }

    @Test
    @DisplayName("Create: not save user when user exists")
    void create_whenUserExists_shouldNotSaveUser() {

        // Data
        when(userRepository.findByUsername(user.username()))
                .thenReturn(Optional.of(userEntity));

        // Steps
        userKafkaService.create(user);

        // Assertions
        verify(userRepository).findByUsername(user.username());
        verify(userRepository, never()).save(any(UserEntity.class));

    }

}
