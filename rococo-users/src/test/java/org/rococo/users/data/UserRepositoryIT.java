package org.rococo.users.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Sql("/sql/users.sql")
@Transactional
@DataJpaTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("UserRepository: Integration tests")
class UserRepositoryIT {

    private static final UUID USER_ID = UUID.fromString("1b20be8f-8a1e-4c51-bfb1-a67415daf8dc");

    @Autowired
    UserRepository userRepository;

    private final UserEntity expectedUser = UserEntity.builder()
            .id(USER_ID)
            .username("john.doe.777")
            .firstName("John")
            .lastName("Doe")
            .createdDate(LocalDateTime.of(2024, 1, 1, 11, 15))
            .build();

    @Test
    @DisplayName("FindById: returns user")
    void findById_ReturnsUser() {

        // Steps
        var result = userRepository.findById(expectedUser.getId()).orElse(new UserEntity());

        // Assertions
        assertAll(
                () -> assertEquals(expectedUser.getId(), result.getId()),
                () -> assertEquals(expectedUser.getUsername(), result.getUsername()),
                () -> assertEquals(expectedUser.getFirstName(), result.getFirstName()),
                () -> assertEquals(expectedUser.getLastName(), result.getLastName())
        );

    }

    @Test
    @DisplayName("FindById: returns empty if user not found by id")
    void findById_ReturnsEmpty() {

        // Steps
        var result = userRepository.findById(UUID.randomUUID());

        // Assertions
        assertTrue(result.isEmpty());

    }

    @Test
    @DisplayName("FindByUsername: returns user")
    void findByTitle_ReturnsUser() {

        // Steps
        var result = userRepository.findByUsername(expectedUser.getUsername()).orElse(new UserEntity());

        // Assertions
        assertAll(
                () -> assertEquals(expectedUser.getId(), result.getId()),
                () -> assertEquals(expectedUser.getUsername(), result.getUsername()),
                () -> assertEquals(expectedUser.getFirstName(), result.getFirstName()),
                () -> assertEquals(expectedUser.getLastName(), result.getLastName())
        );

    }

    @Test
    @DisplayName("FindByUsername: returns empty when user not found by username")
    void findByTitle_ReturnsEmpty() {

        // Steps
        var result = userRepository.findByUsername("temp.user.1234");

        // Assertions
        assertTrue(result.isEmpty());

    }

}
