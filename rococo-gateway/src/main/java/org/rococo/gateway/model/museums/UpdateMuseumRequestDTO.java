package org.rococo.gateway.model.museums;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.rococo.gateway.model.countries.LocationRequestDTO;
import org.rococo.gateway.validation.Image;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Builder
public record UpdateMuseumRequestDTO(

        @NotNull(message = "{errors.validation.museums.id.not_null}")
        @JsonProperty("id")
        UUID id,

        @NotBlank(message = "{errors.validation.museums.title.not_blank}")
        @Size(min = 3, max = 255, message = "{errors.validation.museums.title.size}")
        @JsonProperty("title")
        String title,

        @Size(min = 10, max = 2000, message = "{errors.validation.museums.description.size}")
        @JsonProperty("description")
        String description,

        @JsonProperty("geo")
        LocationRequestDTO location,

        @NotBlank(message = "{errors.validation.museums.photo.not_blank}")
        @Image
        @JsonProperty("photo")
        String photo

) implements Serializable {

    @Override
    public String toString() {
        return """
                {
                  "title": %s
                  "description": %s,
                  "location": %s,
                  "photo": %s
                }""".formatted(
                title == null
                        ? null
                        : "\"" + title + "\"",
                description == null
                        ? null
                        : "\"" + description + "\"",
                location,
                photo != null && !photo.isBlank()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateMuseumRequestDTO that = (UpdateMuseumRequestDTO) o;
        return Objects.equals(title, that.title) && Objects.equals(photo, that.photo) && Objects.equals(description, that.description) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, location, photo);
    }

}
