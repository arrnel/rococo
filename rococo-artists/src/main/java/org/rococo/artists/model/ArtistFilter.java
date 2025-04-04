package org.rococo.artists.model;

import lombok.Builder;

@Builder
public record ArtistFilter(
        String query
) {

    @Override
    public String toString() {
        return """
                {
                  "query": %s,
                }""".formatted(
                query == null ? null : "\"" + query + "\"");
    }

}
