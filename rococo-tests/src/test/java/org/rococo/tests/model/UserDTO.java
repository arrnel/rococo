package org.rococo.tests.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonIgnore
    private String pathToPhoto;

    @JsonIgnore
    @Builder.Default
    private transient TestData testData = new TestData();

    public String password() {
        return testData.getPassword();
    }

    @Nonnull
    public UserDTO password(String password) {
        this.testData.setPassword(password);
        return this;
    }

    public String token() {
        return testData.getToken();
    }

    @Nonnull
    public UserDTO token(String token) {
        this.testData.setToken(token);
        return this;
    }


    @Override
    public String toString() {
        return """
                {
                  "id": %s,
                  "username": %s
                  "firstName": %s,
                  "lastName": %s,
                  "photo": %s,
                  "pathToPhoto": %s
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
                photo != null && !photo.isBlank(),
                pathToPhoto == null
                        ? null
                        : "\"" + pathToPhoto + "\"");
    }

}
