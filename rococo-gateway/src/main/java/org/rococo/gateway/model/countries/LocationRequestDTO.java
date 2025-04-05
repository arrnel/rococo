package org.rococo.gateway.model.countries;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Objects;

@Builder
public record LocationRequestDTO(

        @NotBlank(message = "{errors.validation.museums.city.not_blank}")
        @Size(min = 3, max = 255, message = "{errors.validation.museums.city.size}")
        @JsonProperty("city")
        String city,

        @JsonProperty("country")
        CountryIdDTO country

) {

    @Override
    public String toString() {
        return """
                {
                  "city": %s,
                  "country": %s,
                }""".formatted(
                city == null
                        ? null
                        : "\"" + city + "\"",
                country == null
                        ? null
                        : "\"" + country + "\""
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationRequestDTO that = (LocationRequestDTO) o;
        return Objects.equals(city, that.city) && Objects.equals(country, that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, country);
    }

}
