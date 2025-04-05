package org.rococo.tests.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiError implements Serializable {

    @JsonProperty("apiVersion")
    private String apiVersion;

    @JsonProperty("error")
    private Error error;

    public record Error(

            @JsonProperty("code")
            String code,

            @JsonProperty("message")
            String message,

            @JsonProperty("errors")
            List<ErrorItem> errors

    ) implements Serializable {
    }

    public record ErrorItem(

            @JsonProperty("domain")
            String domain,

            @JsonProperty("reason")
            String reason,

            @JsonProperty("message")
            String itemMessage

    ) implements Serializable {
    }

}