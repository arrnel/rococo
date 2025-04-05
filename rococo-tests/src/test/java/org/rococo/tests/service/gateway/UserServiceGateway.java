package org.rococo.tests.service.gateway;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.gateway.UsersApiClient;
import org.rococo.tests.ex.TokenIsEmptyException;
import org.rococo.tests.model.Token;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@ParametersAreNonnullByDefault
public class UserServiceGateway implements UserService {

    private final UsersApiClient userClient = new UsersApiClient();

    @Getter
    @Setter
    private Token token;

    @Nonnull
    @Override
    public UserDTO create(UserDTO userDTO) {
        log.info("Create new user: {}", userDTO);
        checkToken();
        return userClient.createUser(token.token(), userDTO);
    }

    @Nonnull
    public Optional<UserDTO> currentUser() {
        log.info("Current user: {}", token);
        checkToken();
        return userClient.currentUser(token.token());
    }

    @Nonnull
    @Override
    public Optional<UserDTO> findById(UUID id) {
        log.info("Find user by id: {}", id);
        return findUsers().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Nonnull
    @Override
    public Optional<UserDTO> findByUsername(String username) {
        log.info("Find user by username: {}", username);
        return findUsers().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Nonnull
    @Override
    public List<UserDTO> findAll() {
        return findUsers();
    }

    @Nonnull
    @Override
    public UserDTO update(UserDTO userDTO) {
        log.info("Update user: {}", userDTO);
        checkToken();
        return userClient.createUser(token.token(), userDTO);
    }

    @Override
    public void delete(String username) {
        log.info("Delete user by username: {}", username);
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public void clearAll() {
        throw new UnsupportedOperationException("Not supported yet");
    }

    private List<UserDTO> findUsers() {
        List<UserDTO> allUsers = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);

        while (true) {
            Page<UserDTO> page = userClient.findAll(pageable.getPageNumber(), pageable.getPageSize());
            allUsers.addAll(page.getContent());
            if (!page.hasNext()) break;
            pageable = page.nextPageable();
        }

        return allUsers;
    }

    private void checkToken() {
        if (token == null || token.token() == null || token.token().isEmpty())
            throw new TokenIsEmptyException();
    }

}
