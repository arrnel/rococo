package org.rococo.tests.data.repository;

import org.rococo.tests.data.entity.AuthUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {

    AuthUserEntity create(AuthUserEntity user);

    Optional<AuthUserEntity> findById(UUID id);

    Optional<AuthUserEntity> findByUsername(String username);

    List<AuthUserEntity> findAll();

    AuthUserEntity update(AuthUserEntity user);

    void remove(AuthUserEntity user);

    void removeAll();

}
