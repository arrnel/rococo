package org.rococo.tests.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class LocationDTO implements Serializable {

    @JsonProperty("city")
    private String city;

    @JsonProperty("country")
    private CountryDTO country;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationDTO that = (LocationDTO) o;
        return Objects.equals(city, that.city) && Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, country);
    }

}