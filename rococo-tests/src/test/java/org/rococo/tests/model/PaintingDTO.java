package org.rococo.tests.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaintingDTO implements Serializable {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("artist")
    private ArtistDTO artist;

    @JsonProperty("museum")
    private MuseumDTO museum;

    @JsonProperty("content")
    private String photo;

    @JsonIgnore
    private String pathToPhoto;

    @Override
    public String toString() {
        return """
                {
                  "id": %s,
                  "title": %s,
                  "description": %s,
                  "artist": %s,
                  "museum": %s,
                  "photo": %s,
                  "pathToPhoto": %s
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
                photo != null && !photo.isBlank(),
                pathToPhoto == null
                        ? null
                        : "\"" + pathToPhoto + "\""
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaintingDTO that = (PaintingDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(artist, that.artist) && Objects.equals(museum, that.museum) && Objects.equals(photo, that.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, artist, museum, photo);
    }

}
