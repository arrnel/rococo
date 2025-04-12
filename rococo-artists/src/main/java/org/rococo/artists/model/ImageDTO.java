package org.rococo.artists.model;

import lombok.Builder;

import java.util.Objects;
import java.util.UUID;

@Builder
public record ImageDTO(

        UUID entityId,

        String content

) {

    public static ImageDTO empty() {
        return new ImageDTO(null, null);
    }

    @Override
    public String toString() {
        return """
                {
                  "id": %s,
                  "content": %b
                }""".formatted(
                entityId == null
                        ? null
                        : "\"" + entityId + "\"",
                content != null && content.isEmpty()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageDTO imageDTO = (ImageDTO) o;
        return Objects.equals(entityId, imageDTO.entityId) && Objects.equals(content, imageDTO.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId, content);
    }

}
