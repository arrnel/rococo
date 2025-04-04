package org.rococo.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rococo.users.data.UserEntity;
import org.rococo.users.data.UserRepository;
import org.rococo.users.model.UserDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserKafkaService {

    private final UserRepository userRepository;

    @Transactional
    @KafkaListener(topics = "users", groupId = "userdata")
    public void create(@Payload UserDTO user) {
        log.info("Create new user: {}", user);
        userRepository.findByUsername(user.username())
                .ifPresentOrElse(
                        u -> log.info("User with username = [{}] already exists", user.username()),
                        () -> userRepository.save(
                                UserEntity.builder()
                                        .username(user.username())
                                        .build())
                );
    }

}
