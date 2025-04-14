package org.rococo.tests.tests.api;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.rococo.tests.jupiter.annotation.User;
import org.rococo.tests.jupiter.annotation.Users;
import org.rococo.tests.jupiter.annotation.meta.ApiTest;
import org.rococo.tests.jupiter.annotation.meta.InjectService;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.service.UserService;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.rococo.tests.enums.ServiceType.API;

@ApiTest
@Feature("API")
@Story("[API] Users tests")
@DisplayName("[API] Users tests")
@ParametersAreNonnullByDefault
class UserApiTest {

    @InjectService(API)
    private UserService userService;

    @User
    @Test
    @DisplayName("Can get user by id")
    void canGetUserByIdTest(UserDTO user) {

        // Steps
        var result = userService.findById(user.getId()).orElse(null);

        // Assertions
        assertThat(result, allOf(
                hasProperty("id", is(user.getId())),
                hasProperty("username", is(user.getUsername()))
        ));

    }

    @Test
    @DisplayName("Returns Optional.empty() if search user by unknown id")
    void canGetEmptyUserByUnknownIdTest() {

        // Steps
        var result = userService.findById(UUID.randomUUID());

        // Assertions
        assertTrue(result.isEmpty());

    }

    @User
    @Test
    @DisplayName("Can get user by username")
    void canGetUserByUsername(UserDTO user) {

        // Steps
        var result = userService.findByUsername(user.getUsername()).orElse(null);

        // Assertions
        assertThat(result, allOf(
                hasProperty("id", is(user.getId())),
                hasProperty("username", is(user.getUsername()))
        ));

    }

    @Test
    @DisplayName("Returns Optional.empty() if search user by unknown username")
    void canGetEmptyUserByUnknownNameTest() {

        // Steps
        var result = userService.findByUsername(new Faker().internet().username());

        // Assertions
        assertTrue(result.isEmpty());

    }

    @Users(count = 3)
    @Test
    @DisplayName("Can get all users")
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
                                hasProperty("username", is(user.getUsername()))
                        ))
                        .toArray(Matcher[]::new)
                ));

    }


}
