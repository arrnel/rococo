package org.rococo.tests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AuthUserDTO implements Serializable {

    private UUID id;
    private String username;
    private String password;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    @Builder.Default
    private List<AuthorityDTO> authorities = new ArrayList<>();

    @Override
    public String toString() {
        return """
                {
                  "id": %s,
                  "username": %s,
                  "password": %s,
                  "accountNonExpired": %s,
                  "accountNonLocked": %s,
                  "credentialsNonExpired": %s,
                  "enabled": %s,
                  "enabled": %s,
                  "authorities": %s
                }""".formatted(
                id == null
                        ? null
                        : "\"" + id + "\"",
                username == null
                        ? null
                        : "\"" + username + "\"",
                password == null
                        ? null
                        : "\"" + password + "\"",
                accountNonExpired,
                accountNonLocked,
                credentialsNonExpired,
                enabled,
                authorities == null
                        ? null
                        : authorities.toString());
    }

}