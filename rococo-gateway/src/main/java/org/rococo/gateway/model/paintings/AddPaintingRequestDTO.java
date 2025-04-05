package org.rococo.gateway.model.paintings;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.rococo.gateway.model.artists.ArtistIdDTO;
import org.rococo.gateway.model.museums.MuseumIdDTO;
import org.rococo.gateway.validation.Image;

import java.io.Serializable;
import java.util.Objects;

@Builder
public record AddPaintingRequestDTO(

        @NotBlank(message = "{errors.validation.paintings.title.not_blank}")
        @Size(min = 3, max = 255, message = "{errors.validation.paintings.title.size}")
        @JsonProperty("title")
        String title,

        @Size(min = 10, max = 2000, message = "{errors.validation.paintings.description.size}")
        @JsonProperty("description")
        String description,

        @JsonProperty("artist")
        ArtistIdDTO artist,

        @JsonProperty("museum")
        MuseumIdDTO museum,

        @NotBlank(message = "{errors.validation.paintings.photo.not_blank}")
        @Image
        @JsonProperty("content")
        String photo

) implements Serializable {

    @Override
    public String toString() {
        return """
                {
                  "title": %s
                  "description": %s,
                  "artist": %s,
                  "museum": %s,
                  "photo": %s
                }""".formatted(
                title == null
                        ? null
                        : "\"" + title + "\"",
                description == null
                        ? null
                        : "\"" + description + "\"",
                artist,
                museum,
                photo != null && !photo.isBlank()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddPaintingRequestDTO that = (AddPaintingRequestDTO) o;
        return Objects.equals(title, that.title) && Objects.equals(photo, that.photo) && Objects.equals(description, that.description) && Objects.equals(artist, that.artist) && Objects.equals(museum, that.museum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, artist, museum, photo);
    }

}