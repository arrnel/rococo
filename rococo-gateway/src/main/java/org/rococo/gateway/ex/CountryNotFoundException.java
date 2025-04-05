package org.rococo.gateway.ex;

import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Getter
@ParametersAreNonnullByDefault
public class CountryNotFoundException extends RuntimeException {

    private final UUID id;
    private final String countryCode;

    public CountryNotFoundException(final UUID id) {
        super("Country with id = [%s] not found".formatted(id));
        this.id = id;
        this.countryCode = null;
    }

    public CountryNotFoundException(final String countryCode) {
        super("Country with id = [%s] not found".formatted(countryCode));
        this.id = null;
        this.countryCode = countryCode;
    }

}
