package org.rococo.museums.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record MuseumFilter(

        String query,

        UUID countryId,

        String city

) {

    @Override
    public String toString() {
        return """
                {
                  "query": %s,
                  "countryId": %s,
                  "city": %s
                }"""
                .formatted(
                        query != null
                                ? "\"" + query + "\""
                                : null,
                        countryId != null
                                ? "\"" + countryId + "\""
                                : null,
                        city != null
                                ? "\"" + city + "\""
                                : null);

    }

}
