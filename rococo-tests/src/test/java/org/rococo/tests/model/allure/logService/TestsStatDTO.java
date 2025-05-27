package org.rococo.tests.model.allure.logService;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record TestsStatDTO(

        @JsonProperty("id")
        UUID id,

        @JsonProperty("failed")
        Integer failed,

        @JsonProperty("broken")
        Integer broken,

        @JsonProperty("skipped")
        Integer skipped,

        @JsonProperty("passed")
        Integer passed,

        @JsonProperty("unknown")
        Integer unknown,

        @JsonProperty("total")
        Integer total,

        @JsonProperty("is_passed")
        Boolean isPassed,

        @JsonProperty("passed_percentage")
        Double passed_percentage

) implements Serializable {

    @Override
    public String toString() {
        return """
                {
                	"id": %s,
                	"failed": %s,
                	"broken": %s,
                	"skipped": %s,
                	"passed": %s,
                	"unknown": %s,
                	"total": %s,
                	"is_passed": %s,
                	"passed_percentage": %s
                }""".formatted(id, failed, broken, skipped, passed, unknown, total);
    }
}
