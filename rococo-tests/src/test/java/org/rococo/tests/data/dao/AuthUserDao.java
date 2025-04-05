package org.rococo.tests.data.dao;

import org.rococo.tests.data.entity.AuthUserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface AuthUserDao {

    @Nonnull
    AuthUserEntity create(AuthUserEntity user);

    @Nonnull
    Optional<AuthUserEntity> findById(UUID id);

    @Nonnull
    Optional<AuthUserEntity> findByUsername(String username);

    @Nonnull
    List<AuthUserEntity> findAll();

    @Nonnull
    AuthUserEntity update(AuthUserEntity user);

    void remove(AuthUserEntity user);

    void removeAll();

}
