package org.rococo.users.data;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Nonnull
    Optional<UserEntity> findById(UUID id);

    @Nonnull
    Optional<UserEntity> findByUsername(String username);

}
