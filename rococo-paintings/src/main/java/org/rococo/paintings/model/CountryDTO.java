package org.rococo.paintings.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Objects;
import java.util.UUID;

@Builder
public record CountryDTO(

        @JsonProperty("id")
        UUID id,

        @JsonProperty("name")
        String name,

        @JsonProperty("code")
        String code

) {

    public static CountryDTO empty() {
        return new CountryDTO(null, null, null);
    }

    @Override
    public String toString() {
        return """
                {
                  "id": %s,
                  "name": %s,
                }""".formatted(
                id == null
                        ? null
                        : "\"" + id + "\"",
                name == null
                        ? null
                        : "\"" + name + "\"");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountryDTO that = (CountryDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

}
