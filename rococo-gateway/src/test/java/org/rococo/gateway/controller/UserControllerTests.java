package org.rococo.gateway.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rococo.gateway.client.UsersGrpcClient;
import org.rococo.gateway.ex.BadRequestException;
import org.rococo.gateway.ex.CurrentUserNotFoundException;
import org.rococo.gateway.model.users.UpdateUserRequestDTO;
import org.rococo.gateway.model.users.UserDTO;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController: Module tests")
class UserControllerTests {

    @Mock
    private UsersGrpcClient usersClient;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private UserController userController;

    private UserDTO user;
    private UserDTO updatedUser;
    private UpdateUserRequestDTO updateUserDTO;
    private UUID userId;
    private String username;

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();
        username = "jonny.doe.777";

        user = UserDTO.builder()
                .id(userId)
                .username("jonny.doe.777")
                .firstName("John")
                .lastName("Doe")
                .photo("new_image")
                .build();

        updatedUser = UserDTO.builder()
                .id(userId)
                .username("jonny.doe.777")
                .firstName("Jonny")
                .lastName("Dew")
                .photo("updated_image")
                .build();

        updateUserDTO = UpdateUserRequestDTO.builder()
                .username("jonny.doe.777")
                .firstName("Jonny")
                .lastName("Dew")
                .photo("updated_image")
                .build();

    }

    @Test
    @DisplayName("GetCurrentUser: returns user when user exists")
    void getCurrentUser_ReturnsUser_WhenUserExists() {

        // Steps
        UserDTO result = userController.getCurrentUser(user);

        // Assertions
        assertEquals(user, result);

    }

    @Test
    @DisplayName("GetCurrentUser: throws CurrentUserNotFoundException when user not found")
    void getCurrentUser_ThrowsCurrentUserNotFoundException_WhenUserNotFound() {

        // Data
        final Jwt jwt = mock(Jwt.class);

        // Stubs
        when(jwt.getClaimAsString("sub"))
                .thenReturn(username);
        when(usersClient.findByUsername(username))
                .thenReturn(Optional.empty());

        // Steps & Assertions
        assertThrows(CurrentUserNotFoundException.class, () -> userController.getUser(jwt));

        verify(usersClient).findByUsername(username);

    }

    @Test
    @DisplayName("UpdateCurrentUser: updates user when request is valid")
    void updateCurrentUser_UpdatesUser_WhenRequestIsValid() {

        // Stubs
        when(bindingResult.hasErrors())
                .thenReturn(false);
        when(usersClient.update(userId, updateUserDTO))
                .thenReturn(updatedUser);

        // Steps
        UserDTO result = userController.updateCurrentUser(user, updateUserDTO, bindingResult);

        // Assertions
        assertEquals(updatedUser, result);
        verify(usersClient).update(userId, updateUserDTO);
        verify(bindingResult).hasErrors();

    }

    @Test
    @DisplayName("UpdateCurrentUser: throws BadRequestException when request has errors")
    void updateCurrentUser_ThrowsBadRequestException_WhenRequestHasErrors() {

        // Stubs
        when(bindingResult.hasErrors())
                .thenReturn(true);

        // Steps & Assertions
        assertThrows(BadRequestException.class, () ->
                userController.updateCurrentUser(user, updateUserDTO, bindingResult));

        verify(bindingResult).hasErrors();
        verify(usersClient, never()).update(any(), any());

    }

}
