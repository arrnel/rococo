package org.rococo.gateway.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Getter
@ParametersAreNonnullByDefault
public class UserNotFoundException extends RuntimeException {

    private final UUID id;

    public UserNotFoundException(final UUID id) {
        super("User with id = [%s] not found".formatted(id));
        this.id = id;
    }

}
