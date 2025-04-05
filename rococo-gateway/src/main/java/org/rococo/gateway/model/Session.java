package org.rococo.gateway.model;

import jakarta.annotation.Nonnull;
import lombok.Builder;

import java.util.Date;

@Builder
public record Session(
        String username,
        Date issuedAt,
        Date expiresAt
) {
    public static @Nonnull Session empty() {
        return new Session(null, null, null);
    }
}
