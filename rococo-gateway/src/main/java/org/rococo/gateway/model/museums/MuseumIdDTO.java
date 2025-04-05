package org.rococo.gateway.model.museums;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Builder
public record MuseumIdDTO(

        @NotNull(message = "{errors.validation.paintings.museum_id.not_null}")
        @JsonProperty("id")
        UUID id

) implements Serializable {

    @Override
    public String toString() {
        return """
                {
                  "id": %s
                }""".formatted(
                id == null
                        ? null
                        : "\"" + id + "\""
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MuseumIdDTO that = (MuseumIdDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
