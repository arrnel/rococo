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
public class ArtistDTO implements Serializable {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("biography")
    private String biography;

    @JsonProperty("photo")
    private String photo;

    @JsonIgnore
    private String pathToPhoto;

    @Override
    public String toString() {
        return """
                {
                  "id": %s,
                  "name": %s,
                  "biography": %s,
                  "photo": %s,
                  "pathToPhoto": %s
                }""".formatted(
                id == null
                        ? null
                        : "\"" + id + "\"",
                name == null
                        ? null
                        : "\"" + name + "\"",
                biography == null
                        ? null
                        : "\"" + biography + "\"",
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
        ArtistDTO artistDTO = (ArtistDTO) o;
        return Objects.equals(id, artistDTO.id) && Objects.equals(name, artistDTO.name) && Objects.equals(biography, artistDTO.biography) && Objects.equals(photo, artistDTO.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, biography, photo);
    }

}
