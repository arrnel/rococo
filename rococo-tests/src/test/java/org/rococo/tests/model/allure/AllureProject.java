package org.rococo.tests.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AllureProject {

    @JsonProperty("id")
    private final String id;

}
