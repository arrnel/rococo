package org.rococo.tests.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TestData {

    private String password;

    private String token;

    private String idToken;

    private String jSessionId;

    @Override
    public String toString() {
        return """
                {
                  "password": %s,
                  "token": %s,
                  "jsessionId": %s
                }""".formatted(
                password == null
                        ? null
                        : "\"" + password + "\"",
                token == null
                        ? null
                        : "\"" + token + "\"",
                jSessionId == null
                        ? null
                        : "\"" + jSessionId + "\"");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestData testData = (TestData) o;
        return Objects.equals(password, testData.password);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(password);
    }
}
