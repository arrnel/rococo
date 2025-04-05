package org.rococo.tests.ex;

import org.rococo.tests.model.ApiError;
import retrofit2.Call;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String method, String path, String message) {
        super("[BAD_REQUEST] %s %s. Response body: %s".formatted(method, path, message));
    }

    public <T> BadRequestException(Call<T> call, String message) {
        super("[BAD_REQUEST] %s %s. Response body: %s".formatted(call.request().method(), call.request().url().encodedPath(), message));
    }

    public BadRequestException(String method, String path, ApiError apiError) {
        super("[BAD_REQUEST] %s %s. Response body: %s".formatted(method, path, apiError.getError().message()));
    }

    public <T> BadRequestException(Call<T> call, ApiError apiError) {
        super("[BAD_REQUEST] %s %s. Response body: %s".formatted(call.request().method(), call.request().url().encodedPath(), apiError.getError().message()));
    }

}
