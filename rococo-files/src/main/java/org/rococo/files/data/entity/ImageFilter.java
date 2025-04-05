package org.rococo.files.data.entity;

import lombok.Builder;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Builder
public record ImageFilter(

        EntityType entityType,

        UUID[] entityIds,

        boolean isThumbnails

) {

    @Override
    public String toString() {
        return """
                {
                    "entityType": "%s",
                    "entityIds": %s,
                    "isThumbnails": %b
                }""".formatted(
                entityType,
                entityIds == null || entityIds.length == 0
                        ? "[]"
                        : Arrays.toString(entityIds),
                isThumbnails);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageFilter that = (ImageFilter) o;
        return isThumbnails == that.isThumbnails && Objects.deepEquals(entityIds, that.entityIds) && entityType == that.entityType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityType, Arrays.hashCode(entityIds), isThumbnails);
    }

}
