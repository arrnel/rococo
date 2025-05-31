package org.rococo.tests.tests.fake.grpc;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.tests.ex.UserAlreadyExistsException;
import org.rococo.tests.jupiter.annotation.User;
import org.rococo.tests.jupiter.annotation.Users;
import org.rococo.tests.jupiter.annotation.meta.GrpcTest;
import org.rococo.tests.jupiter.annotation.meta.InjectService;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.service.UserService;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.rococo.tests.enums.ServiceType.GRPC;
import static org.rococo.tests.util.CompareUtil.containsUsers;

@GrpcTest
@Feature("FAKE")
@Story("[GRPC] Users tests")
@DisplayName("[GRPC] Users tests")
@ParametersAreNonnullByDefault
class UserGrpcTest {

    @InjectService(GRPC)
    private UserService userService;

    @Test
    @DisplayName("Can create user")
    void canCreateUserTest() {

        // Data
        var user = DataGenerator.generateUser();

        // Steps
        var result = userService.create(user);

        // Assertions
        assertNotNull(result.getId());

    }

    @User
    @Test
    @DisplayName("Can not create user with exists username")
    void canNotCreateUserWithExistsUsernameTest(UserDTO user) {

        // Steps
        var result = assertThrows(UserAlreadyExistsException.class, () -> userService.create(user));

        // Assertions
        assertEquals("User with username = [%s] already exists".formatted(user.getUsername()), result.getMessage());


    }

    @User
    @Test
    @DisplayName("Can get user by id")
    void canGetUserByIdTest(UserDTO user) {

        // Steps
        var result = userService.findById(user.getId()).orElse(null);

        // Assertions
        assertEquals(user, result);

    }

    @Test
    @DisplayName("Returns Optional.empty() if search user by unknown id")
    void canGetEmptyUserByUnknownIdTest() {

        // Steps
        var result = userService.findById(UUID.randomUUID());

        // Assertions
        assertTrue(result.isEmpty(), "Check user not found by unknown id");

    }

    @User
    @Test
    @DisplayName("Can get user by username")
    void canGetUserByUsername(UserDTO user) {

        // Steps
        var result = userService.findByUsername(user.getUsername()).orElse(null);

        // Assertions
        assertEquals(user, result);

    }

    @Test
    @DisplayName("Returns Optional.empty() if search user by unknown username")
    void canGetEmptyUserByUnknownUsernameTest() {

        // Steps
        var result = userService.findByUsername(new Faker().internet().username());

        // Assertions
        assertTrue(result.isEmpty(), "Check user not found by unknown username");

    }

    @Users(count = 3)
    @Test
    @DisplayName("Can get all user")
    void canGetAllUsersTest(List<UserDTO> users) {

        // Steps
        var result = userService.findAll();

        // Assertions
        assertTrue(containsUsers(users, result, false), "Check expected users exists in findAll request");

    }

    @User
    @Test
    @DisplayName("Can update user")
    void canUpdateUserTest(UserDTO oldUser) {

        // Data
        var newUser = DataGenerator.generateUser()
                .setId(oldUser.getId());

        // Steps
        var result = userService.update(newUser);

        // Assertions
        assertThat(result, allOf(
                hasProperty("id", is(oldUser.getId())),
                hasProperty("username", is(oldUser.getUsername())),
                hasProperty("firstName", is(newUser.getFirstName())),
                hasProperty("lastName", is(newUser.getLastName()))
        ));

    }

    @User
    @Test
    @DisplayName("Can delete user")
    void canDeleteUserTest(UserDTO user) {

        // Steps
        userService.delete(user.getUsername());

        // Assertions
        assertTrue(userService.findById(user.getId()).isEmpty(), "Check user not found by id after removing");

    }

}
