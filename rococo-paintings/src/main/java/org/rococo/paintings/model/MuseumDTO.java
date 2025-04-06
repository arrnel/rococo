package org.rococo.paintings.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.rococo.gateway.model.countries.LocationResponseDTO;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MuseumDTO implements Serializable {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("geo")
    private LocationDTO location;

    @JsonProperty("photo")
    private String photo;


    @Override
    public String toString() {
        return """
                {
                  "id": %s,
                  "title": %s
                  "description": %s,
                  "country": %s,
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
                location,
                photo != null && !photo.isBlank()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MuseumDTO museumDTO = (MuseumDTO) o;
        return Objects.equals(id, museumDTO.id) && Objects.equals(title, museumDTO.title) && Objects.equals(photo, museumDTO.photo) && Objects.equals(description, museumDTO.description) && Objects.equals(location, museumDTO.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, location, photo);
    }

}
