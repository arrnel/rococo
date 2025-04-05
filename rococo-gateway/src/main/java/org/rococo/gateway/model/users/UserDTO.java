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
public class UserDTO implements Serializable {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("firstname")
    private String firstName;

    @JsonProperty("lastname")
    private String lastName;

    @JsonProperty("avatar")
    private String photo;

    @Override
    public String toString() {
        return """
                {
                  "id": %s,
                  "username": %s
                  "firstName": %s,
                  "lastName": %s,
                  "photo": %s
                }""".formatted(
                id == null
                        ? null
                        : "\"" + id + "\"",
                username == null
                        ? null
                        : "\"" + username + "\"",
                firstName == null
                        ? null
                        : "\"" + firstName + "\"",
                lastName == null
                        ? null
                        : "\"" + lastName + "\"",
                photo != null && !photo.isBlank()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) && Objects.equals(photo, userDTO.photo) && Objects.equals(username, userDTO.username) && Objects.equals(lastName, userDTO.lastName) && Objects.equals(firstName, userDTO.firstName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, firstName, lastName, photo);
    }

}
