package org.rococo.gateway.model.users;

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
public class UserShortDTO implements Serializable {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("username")
    private String username;

    @Override
    public String toString() {
        return """
                {
                  "id": %s,
                  "username": %s
                }""".formatted(
                id == null
                        ? null
                        : "\"" + id + "\"",
                username == null
                        ? null
                        : "\"" + username + "\""
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserShortDTO that = (UserShortDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

}
