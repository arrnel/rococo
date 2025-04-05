package org.rococo.gateway.model.paintings;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.rococo.gateway.model.artists.ArtistDTO;
import org.rococo.gateway.model.museums.MuseumDTO;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaintingDTO implements Serializable {

    @JsonProperty("id")
    UUID id;

    @JsonProperty("title")
    String title;

    @JsonProperty("description")
    String description;

    @JsonProperty("artist")
    ArtistDTO artist;

    @JsonProperty("museum")
    MuseumDTO museum;

    @JsonProperty("content")
    String photo;

    @Override
    public String toString() {
        return """
                {
                  "id": %s,
                  "title": %s,
                  "description": %s,
                  "artist": %s,
                  "museum": %s,
                  "photo": %s
                }""".formatted(
                id == null
                        ? null
                        : "\"" + id + "\"",
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
        PaintingDTO that = (PaintingDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(photo, that.photo) && Objects.equals(artist, that.artist) && Objects.equals(museum, that.museum) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, artist, museum, photo);
    }

}
