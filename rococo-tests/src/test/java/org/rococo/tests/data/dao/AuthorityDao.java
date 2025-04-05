package org.rococo.tests.data.dao;

import org.rococo.tests.data.entity.AuthorityEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface AuthorityDao {

    void create(AuthorityEntity... authority);

    @Nonnull
    Optional<AuthorityEntity> findById(UUID id);

    @Nonnull
    List<AuthorityEntity> findByUserId(UUID userId);

    @Nonnull
    List<AuthorityEntity> findAll();

    void update(AuthorityEntity... authority);

    void remove(AuthorityEntity... authority);

    void removeAll();

}
