package org.rococo.tests.data.repository;


import org.rococo.tests.data.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    UserEntity create(UserEntity user);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findAll();

    UserEntity update(UserEntity user);

    void remove(UserEntity user);

    void removeAll();

}
