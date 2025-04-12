package org.rococo.museum.ex;

import java.util.UUID;

public class CountryNotFoundException extends RuntimeException {

    public CountryNotFoundException(UUID countryId) {
        super("Country with id = [%s] not found".formatted(countryId.toString()));
    }

}
