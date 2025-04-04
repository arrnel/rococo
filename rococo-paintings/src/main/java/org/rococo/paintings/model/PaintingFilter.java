package org.rococo.paintings.model;

import lombok.Builder;

import java.util.UUID;

@Builder
public record PaintingFilter(

        String query,

        UUID artistId,

        UUID museumId

) {
}
