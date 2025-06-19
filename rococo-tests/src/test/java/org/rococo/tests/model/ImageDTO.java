package org.rococo.tests.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class ImageDTO implements Serializable {

    private UUID entityId;
    private String content;

    @Override
    public String toString() {
        return """
                {
                  "entityId": %s,
                  "content": %b
                }""".formatted(
                entityId == null
                        ? null
                        : "\"" + entityId + "\"",
                content != null && content.isEmpty());
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
