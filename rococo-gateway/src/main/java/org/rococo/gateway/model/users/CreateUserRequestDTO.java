package org.rococo.gateway.model.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.rococo.gateway.config.AppProperty;
import org.rococo.gateway.validation.Image;

import java.io.Serializable;
import java.util.Objects;

@Builder
public record CreateUserRequestDTO(

        @NotBlank(message = "{errors.validation.users.username.not_blank}")
        @Pattern(regexp = AppProperty.USERNAME_PATTERN, message = "{errors.validation.users.username.pattern}")
        @JsonProperty("username")
        String username,

        @Size(min = 2, max = 255, message = "{errors.validation.users.first_name.size}")
        @JsonProperty("firstname")
        String firstName,

        @Size(min = 2, max = 255, message = "{errors.validation.users.last_name.size}")
        @JsonProperty("lastname")
        String lastName,

        @Image
        @JsonProperty("avatar")
        String photo

) implements Serializable {

    @Override
    public String toString() {
        return """
                {
                  "username": %s
                  "firstName": %s,
                  "lastName": %s,
                  "photo": %s
                }""".formatted(
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
        CreateUserRequestDTO that = (CreateUserRequestDTO) o;
        return Objects.equals(photo, that.photo) && Objects.equals(username, that.username) && Objects.equals(lastName, that.lastName) && Objects.equals(firstName, that.firstName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, firstName, lastName, photo);
    }

}