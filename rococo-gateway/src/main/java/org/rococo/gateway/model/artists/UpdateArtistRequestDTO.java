package org.rococo.gateway.model.artists;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.rococo.gateway.validation.Image;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Builder
public record UpdateArtistRequestDTO(

        @NotNull(message = "{errors.validation.artists.id.not_null}")
        @JsonProperty("id")
        UUID id,

        @NotBlank(message = "{errors.validation.artists.name.not_blank}")
        @Size(min = 3, max = 255, message = "{errors.validation.artists.name.size}")
        @JsonProperty("name")
        String name,

        @Size(min = 10, max = 2000, message = "{errors.validation.artists.biography.size}")
        @JsonProperty("biography")
        String biography,

        @NotBlank(message = "{errors.validation.artists.photo.not_blank}")
        @Image
        @JsonProperty("photo")
        String photo

) implements Serializable {

    @Override
    public String toString() {
        return """
                {
                  "name": %s
                  "biography": %s,
                  "photo": %s
                }""".formatted(
                name == null
                        ? null
                        : "\"" + name + "\"",
                biography == null
                        ? null
                        : "\"" + biography + "\"",
                photo != null && !photo.isBlank()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateArtistRequestDTO that = (UpdateArtistRequestDTO) o;
        return Objects.equals(name, that.name) && Objects.equals(photo, that.photo) && Objects.equals(biography, that.biography);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, biography, photo);
    }

}
