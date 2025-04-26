package org.rococo.tests.service.grpc;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.grpc.UsersGrpcClient;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@ParametersAreNonnullByDefault
public class UserServiceGrpc implements UserService {

    @Nonnull
    @Override
    @Step("Create new user: [{user.username}]")
    public UserDTO create(UserDTO user) {
        log.info("Create new user: {}", user);
        return withClient(usersClient ->
                usersClient.add(user));
    }

    @Nonnull
    @Override
    @Step("Find user by id: [{id}]")
    public Optional<UserDTO> findById(UUID id) {
        log.info("Find user with id: {}", id);
        return withClient(usersClient ->
                usersClient.findById(id));
    }

    @Nonnull
    @Override
    @Step("Find user by username: [{username}]")
    public Optional<UserDTO> findByUsername(String username) {
        log.info("Find user with username: {}", username);
        return withClient(usersClient ->
                usersClient.findByUsername(username));
    }

    @Nonnull
    @Override
    @Step("Find all users")
    public List<UserDTO> findAll() {
        log.info("Find all users");
        return withClient(this::findUsers);
    }

    @Nonnull
    @Override
    @Step("Update user: [{user.username}]")
    public UserDTO update(UserDTO user) {
        log.info("Update user: {}", user);
        return withClient(usersClient ->
                usersClient.update(user));
    }

    @Override
    @Step("Delete user: [{username}]")
    public void delete(String username) {
        log.info("Delete user: {}", username);
        withClient(usersClient -> {
            usersClient.findByUsername(username).ifPresent(u ->
                    usersClient.delete(u.getId()));
            return null;
        });
    }

    @Override
    @Step("Delete all users")
    public void clearAll() {
        withClient(usersClient -> {
            findUsers(usersClient).forEach(u -> {
                usersClient.delete(u.getId());
            });
            return null;
        });
    }

    @Nonnull
    private List<UserDTO> findUsers(UsersGrpcClient usersClient) {
        // DON'T remove sort. Help to get all artists in parallel test execution
        List<UserDTO> allUsers = new ArrayList<>();
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by(
                        Sort.Order.asc("createdDate"),
                        Sort.Order.asc("id")
                ));

        while (true) {
            Page<UserDTO> users = usersClient.findAll(pageable);
            allUsers.addAll(users.getContent());
            if (!users.hasNext()) break;
            pageable = pageable.next();
        }
        return allUsers;
    }

    private <T> T withClient(Function<UsersGrpcClient, T> operation) {
        try (UsersGrpcClient client = new UsersGrpcClient()) {
            return operation.apply(client);
        }
    }

}
