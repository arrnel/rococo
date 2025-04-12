package org.rococo.tests.service.grpc;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.grpc.UsersGrpcClient;
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
@ParametersAreNonnullByDefault
public class UserServiceGrpc implements UserService {

    private final UsersGrpcClient userClient = new UsersGrpcClient();

    @Nonnull
    @Override
    @Step("Create new user: [{user.username}]")
    public UserDTO create(UserDTO user) {
        log.info("Create new user: {}", user);
        return userClient.add(user);
    }

    @Nonnull
    @Override
    @Step("Find user by id: [{id}]")
    public Optional<UserDTO> findById(UUID id) {
        log.info("Find user with id: {}", id);
        return userClient.findById(id);
    }

    @Nonnull
    @Override
    @Step("Find user by username: [{username}]")
    public Optional<UserDTO> findByUsername(String username) {
        log.info("Find user with username: {}", username);
        return userClient.findByUsername(username);
    }

    @Nonnull
    @Override
    @Step("Find all users")
    public List<UserDTO> findAll() {
        log.info("Find all users");
        return findUsers();
    }

    @Nonnull
    @Override
    @Step("Update user: [{user.username}]")
    public UserDTO update(UserDTO user) {
        log.info("Update user: {}", user);
        return userClient.update(user);
    }

    @Override
    @Step("Delete user: [{username}]")
    public void delete(String username) {
        log.info("Delete user: {}", username);
        userClient.findByUsername(username)
                .ifPresent(u -> userClient.delete(u.getId()));
    }

    @Override
    @Step("Delete all users")
    public void clearAll() {
        findUsers().forEach(u -> {
            userClient.delete(u.getId());
        });
    }

    @Nonnull
    private List<UserDTO> findUsers() {
        List<UserDTO> allUsers = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 10);

        while (true) {
            Page<UserDTO> users = userClient.findAll(pageable);
            allUsers.addAll(users.getContent());
            if (!users.hasNext()) break;
            pageable = pageable.next();
        }
        return allUsers;
    }

}
