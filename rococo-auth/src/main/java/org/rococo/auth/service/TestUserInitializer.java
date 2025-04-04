package org.rococo.auth.service;

import org.rococo.auth.data.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TestUserInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(TestUserInitializer.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final String username;
    private final String password;

    public TestUserInitializer(UserRepository userRepository,
                               UserService userService,
                               @Value("${app.user.username}")
                               String username,
                               @Value("${app.user.password}")
                               String password
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.username = username;
        this.password = password;
    }

    @Profile({"local", "docker"})
    @EventListener(ApplicationReadyEvent.class)
    public void createTestUser() {

        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    userRepository.delete(user);
                    LOG.info("Successfully removed default user: username = [{}]", username);
                });

        userService.registerUser(username, password);
        LOG.info("Successfully created default user: username = [{}], password = [{}]", username, password);
    }

}