package org.rococo.auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserJson(
        @JsonProperty("username")
        String username) {

}
