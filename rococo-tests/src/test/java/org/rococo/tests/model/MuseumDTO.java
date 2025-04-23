package org.rococo.tests.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Accessors;
import org.rococo.tests.enums.CountryCode;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MuseumDTO implements Serializable {

    @With
    @JsonProperty("id")
    private UUID id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @Builder.Default
    @JsonProperty("geo")
    private LocationDTO location = new LocationDTO();

    @JsonProperty("photo")
    private String photo;

    @JsonIgnore
    private String pathToPhoto;

    public MuseumDTO setCountryCode(CountryCode countryCode) {
        location.getCountry().setCode(countryCode);
        return this;
    }


    public MuseumDTO setCountryId(UUID id) {
        location.getCountry().setId(id);
        return this;
    }

    public MuseumDTO setCountry(CountryDTO country) {
        location.setCountry(country);
        return this;
    }

    @Override
    public String toString() {
        return """
                {
                  "id": %s,
                  "title": %s,
                  "description": %s,
                  "location": %s,
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
                location,
                photo != null && !photo.isBlank(),
                pathToPhoto == null
                        ? null
                        : "\"" + pathToPhoto + "\""
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MuseumDTO museumDTO = (MuseumDTO) o;
        return Objects.equals(id, museumDTO.id) && Objects.equals(title, museumDTO.title) && Objects.equals(description, museumDTO.description) && Objects.equals(location, museumDTO.location) && Objects.equals(photo, museumDTO.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, location, photo);
    }

}
