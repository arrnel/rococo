package org.rococo.gateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.client.UsersGrpcClient;
import org.rococo.gateway.model.users.UserDTO;
import org.rococo.gateway.model.users.UserShortDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsersController: Module tests")
class UsersControllerTests {

    @Mock
    private UsersGrpcClient usersClient;

    @InjectMocks
    private UsersController usersController;

    private UserDTO user1;
    private UserDTO user2;

    @BeforeEach
    void setUp() {

        user1 = UserDTO.builder()
                .id(UUID.randomUUID())
                .username("jonny.doe.777")
                .firstName("John")
                .lastName("Doe")
                .photo("new_image")
                .build();

        user2 = UserDTO.builder()
                .id(UUID.randomUUID())
                .username("test.user")
                .firstName("Jonny")
                .lastName("Dew")
                .photo("updated_image")
                .build();

    }

    @Test
    @DisplayName("GetAllUsers: returns page of users when users exist")
    void getAllUsers_ReturnsPageOfUsers_WhenUsersExist() {

        final var user1Short = UserShortDTO.builder()
                .id(user1.getId())
                .username(user1.getUsername())
                .build();
        final var user2Short = UserShortDTO.builder()
                .id(user2.getId())
                .username(user2.getUsername())
                .build();

        final var pageable = PageRequest.of(0, 20);
        final Page<UserDTO> userPage = new PageImpl<>(List.of(user1, user2), pageable, 1);

        // Stubs
        when(usersClient.findAll(pageable))
                .thenReturn(userPage);

        // Steps
        final var result = usersController.getAllUsers(pageable);

        // Assertions
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().containsAll(List.of(user1Short, user2Short)));
        verify(usersClient).findAll(pageable);

    }

    @Test
    @DisplayName("GetAllUsers: returns empty page when no users exist")
    void getAllUsers_ReturnsEmptyPage_WhenNoUsersExist() {

        // Data
        final var pageable = PageRequest.of(0, 20);
        final Page<UserDTO> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        // Stubs
        when(usersClient.findAll(pageable))
                .thenReturn(emptyPage);

        // Steps
        final var result = usersController.getAllUsers(pageable);

        // Assertions
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getContent().size());
        verify(usersClient).findAll(pageable);

    }

}
