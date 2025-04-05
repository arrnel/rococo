package org.rococo.gateway.model.countries;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public record CountryIdDTO(

        @NotNull(message = "{errors.validation.museums.country_id.not_null}")
        @JsonProperty("id")
        UUID id

) {

    @Override
    public String toString() {
        return """
                {
                  "id": %s
                }""".formatted(
                id == null
                        ? null
                        : "\"" + id + "\"");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountryIdDTO that = (CountryIdDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
