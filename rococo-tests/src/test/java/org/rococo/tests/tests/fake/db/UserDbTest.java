package org.rococo.tests.tests.fake.db;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.rococo.tests.jupiter.annotation.User;
import org.rococo.tests.jupiter.annotation.Users;
import org.rococo.tests.jupiter.annotation.meta.DbTest;
import org.rococo.tests.jupiter.annotation.meta.InjectService;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.service.UserService;
import org.rococo.tests.util.DataGenerator;
import org.springframework.dao.DuplicateKeyException;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.rococo.tests.enums.ServiceType.DB;

@Isolated
@DbTest
@Feature("FAKE")
@Story("[DB] Users tests")
@DisplayName("[DB] Users tests")
@ParametersAreNonnullByDefault
class UserDbTest {

    @InjectService(DB)
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
        var result = assertThrows(RuntimeException.class, () -> userService.create(user));

        // Assertions
        assertAll(
                () -> assertTrue(result.getCause() instanceof DuplicateKeyException),
                () -> assertTrue(result.getMessage().contains("(%s) already exists".formatted(user.getUsername())))
        );

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

    @User
    @Test
    @DisplayName("Can get user by username")
    void canGetUserByUsername(UserDTO user) {

        // Steps
        var result = userService.findByUsername(user.getUsername()).orElse(null);

        // Assertions
        assertEquals(user, result);

    }

    @Users(count = 3)
    @Test
    @DisplayName("Can get all user")
    void canGetAllUsersTest(
            List<UserDTO> users
    ) {

        // Steps
        var result = userService.findAll();

        // Assertions
        assertThat(result,
                hasItems(users.stream()
                        .map(user -> allOf(
                                hasProperty("id", is(user.getId())),
                                hasProperty("username", is(user.getUsername())),
                                hasProperty("firstName", is(user.getFirstName())),
                                hasProperty("lastName", is(user.getLastName()))
                        ))
                        .toArray(Matcher[]::new)
                ));

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
        assertTrue(userService.findById(user.getId()).isEmpty());

    }

}
