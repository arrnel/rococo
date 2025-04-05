package org.rococo.tests.service.grpc;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.client.grpc.FilesGrpcClient;
import org.rococo.tests.client.grpc.UsersGrpcClient;
import org.rococo.tests.model.ImageDTO;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collectors;

import static org.rococo.tests.enums.EntityType.USER;

@Slf4j
@ParametersAreNonnullByDefault
public class UserServiceGrpc implements UserService {

    private final UsersGrpcClient userClient = new UsersGrpcClient();
    private final FilesGrpcClient filesClient = new FilesGrpcClient();

    @Nonnull
    @Override
    @Step("Create new user: [{user.username}]")
    public UserDTO create(UserDTO user) {
        log.info("Create new user: {}", user);
        var newUser = userClient.add(user);
        filesClient.addImage(USER, newUser.getId(), user.getPhoto());
        return newUser.setPhoto(user.getPhoto());
    }

    @Nonnull
    @Override
    @Step("Find user by id: [{id}]")
    public Optional<UserDTO> findById(UUID id) {
        log.info("Find user with id: {}", id);
        return userClient.findById(id)
                .map(user -> user.setPhoto(
                        filesClient.findImage(USER, user.getId())
                                .map(ImageDTO::getContent)
                                .orElse(null)));
    }

    @Nonnull
    @Override
    @Step("Find user by username: [{username}]")
    public Optional<UserDTO> findByUsername(String username) {
        log.info("Find user with username: {}", username);
        return userClient.findByUsername(username)
                .map(user -> user.setPhoto(
                        filesClient.findImage(USER, user.getId())
                                .map(ImageDTO::getContent)
                                .orElse(null)));
    }

    @Nonnull
    @Override
    @Step("Find all users")
    public List<UserDTO> findAll() {
        log.info("Find all users");
        return enrichAll(findUsers());
    }

    @Nonnull
    @Override
    @Step("Update user: [{user.username}]")
    public UserDTO update(UserDTO user) {
        log.info("Update user: {}", user);
        var updatedUser = userClient.update(user);
        filesClient.findImage(USER, user.getId())
                .ifPresentOrElse(
                        image -> filesClient.update(USER, user.getId(), user.getPhoto()),
                        () -> filesClient.addImage(USER, user.getId(), user.getPhoto()));
        return updatedUser.setPhoto(user.getPhoto());
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
            filesClient.delete(USER, u.getId());
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

    @Nonnull
    private List<UserDTO> enrichAll(List<UserDTO> users) {

        var usersIds = users.stream()
                .map(UserDTO::getId)
                .toList();

        Map<UUID, String> usersBase64ImageMap = filesClient.findAll(USER, usersIds).stream()
                .collect(Collectors.toMap(
                        ImageDTO::getEntityId,
                        ImageDTO::getContent));

        return users.stream()
                .map(user -> user.setPhoto(usersBase64ImageMap.get(user.getId())))
                .toList();
    }

}
