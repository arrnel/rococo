package org.rococo.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public class ApiError implements Serializable {

    private final String apiVersion;
    private final Error error;

    public ApiError(String apiVersion, Error error) {
        this.apiVersion = apiVersion;
        this.error = error;
    }

    @Builder
    public ApiError(String apiVersion,
                    String code,
                    String message,
                    String domain,
                    String reason) {
        this.apiVersion = apiVersion;
        this.error = new Error(
                code,
                message,
                List.of(
                        new ErrorItem(
                                domain,
                                reason,
                                message
                        )
                )
        );
    }

    @Builder(builderMethodName = "builderErrors", buildMethodName = "buildErrors")
    public ApiError(String apiVersion,
                    String code,
                    String message,
                    List<ErrorItem> errorItems) {
        this.apiVersion = apiVersion;
        this.error = new Error(
                code,
                message,
                errorItems
        );
    }

    public static ApiError fromAttributesMap(String apiVersion, Map<String, Object> attributesMap) {
        return new ApiError(
                apiVersion,
                ((Integer) attributesMap.get("status")).toString(),
                ((String) attributesMap.getOrDefault("error", "No message found")),
                ((String) attributesMap.getOrDefault("path", "No path found")),
                ((String) attributesMap.getOrDefault("error", "No message found"))
        );
    }

    public Map<String, Object> toAttributesMap() {
        return Map.of(
                "apiVersion", apiVersion,
                "error", error
        );
    }

    public record Error(

            @JsonProperty("code")
            String code,

            @JsonProperty("message")
            String message,

            @JsonProperty("errors")
            List<ErrorItem> errors

    ) implements Serializable {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Error error = (Error) o;
            return Objects.equals(code, error.code) && Objects.equals(message, error.message) && Objects.equals(errors, error.errors);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, message, errors);
        }

    }

    public record ErrorItem(

            @JsonProperty("domain")
            String domain,

            @JsonProperty("reason")
            String reason,

            @JsonProperty("message")
            String itemMessage

    ) implements Serializable {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ErrorItem errorItem = (ErrorItem) o;
            return Objects.equals(domain, errorItem.domain) && Objects.equals(reason, errorItem.reason) && Objects.equals(itemMessage, errorItem.itemMessage);
        }

        @Override
        public int hashCode() {
            return Objects.hash(domain, reason, itemMessage);
        }

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiError apiError = (ApiError) o;
        return Objects.equals(apiVersion, apiError.apiVersion) && Objects.equals(error, apiError.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiVersion, error);
    }

}