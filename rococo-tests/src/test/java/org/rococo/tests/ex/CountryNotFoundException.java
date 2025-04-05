package org.rococo.tests.ex;

import lombok.Getter;
import org.rococo.tests.enums.CountryCode;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@Getter
@ParametersAreNonnullByDefault
public class CountryNotFoundException extends RuntimeException {

    public CountryNotFoundException(final UUID id) {
        super("Country with id = [%s] not found".formatted(id));
    }

    public CountryNotFoundException(final UUID id, final CountryCode code) {
        super("Country with id = [%s] or code = [%s] not found".formatted(id.toString(), code));
    }

    public CountryNotFoundException(final CountryCode code) {
        super("Country with code = [%s] not found".formatted(code));
    }

    public CountryNotFoundException(final String message) {
        super(message);
    }


}
