package org.rococo.logs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
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
        Double passedPercentage

) implements Serializable {

    @Override
    public String toString() {
        return """
                {
                  "id": "%s",
                  "failed": %s,
                  "broken": %s,
                  "skipped": %s,
                  "passed": %s,
                  "unknown": %s,
                  "total": %s,
                  "is_passed": %s,
                  "passed_percentage": %s
                }""".formatted(id.toString(), failed, broken, skipped, passed, unknown, total, isPassed, passedPercentage);
    }


}
