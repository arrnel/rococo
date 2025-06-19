package org.rococo.tests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.rococo.tests.enums.Authority;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AuthorityDTO {

    private UUID id;
    private UUID userId;
    private Authority authority;

    @Override
    public String toString() {
        return """
                {
                  "id": %s,
                  "user": %s,
                  "authority": "%s"
                }""".formatted(
                id == null
                        ? null
                        : "\"" + id + "\"",
                userId == null
                        ? null
                        : "\"" + userId + "\"",
                authority);
    }

}
