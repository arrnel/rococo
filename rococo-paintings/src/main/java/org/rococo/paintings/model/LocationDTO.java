package org.rococo.paintings.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record LocationDTO(

        @JsonProperty("city")
        String city,

        @JsonProperty("country")
        CountryDTO country

) {

    @Override
    public String toString() {
        return """
                {
                  "city": %2$s,
                  "country": %1$s
                }""".formatted(
                country,
                city == null
                        ? null
                        : "\"" + city + "\""
        );
    }

}
