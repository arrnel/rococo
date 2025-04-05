package org.rococo.tests.data.dao;

import org.rococo.tests.data.entity.UserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserDao {

    @Nonnull
    UserEntity create(UserEntity user);

    @Nonnull
    Optional<UserEntity> findById(UUID id);

    @Nonnull
    Optional<UserEntity> findByUsername(String username);

    @Nonnull
    List<UserEntity> findAll();

    @Nonnull
    UserEntity update(UserEntity user);

    void remove(UserEntity user);

    void removeAll();

}
