package org.rococo.tests.model.allure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DecodedAllureFile {

    @JsonProperty("file_name")
    private final String fileName;

    @JsonProperty("content_base64")
    private final String contentBase64;

}
