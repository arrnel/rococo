package org.rococo.tests.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AllureResults {

    @JsonProperty("results")
    private final List<DecodedAllureFile> results;

}
