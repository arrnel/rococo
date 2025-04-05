package org.rococo.gateway.service;

import org.rococo.gateway.model.ApiError;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

public class AppErrorAttributes extends DefaultErrorAttributes {

    private final String apiVersion;

    public AppErrorAttributes(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest,
                                                  ErrorAttributeOptions errorAttributeOptions
    ) {
        Map<String, Object> defaultMap = super.getErrorAttributes(webRequest, errorAttributeOptions);
        ApiError apiError = ApiError.fromAttributesMap(apiVersion, defaultMap);
        return apiError.toAttributesMap();
    }

}