package org.rococo.tests.ex;

import retrofit2.Call;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(final String method, final String path) {
        super("Unauthorized exception. [%s]: [%s]".formatted(method, path));
    }

    public <T> UnauthorizedException(final Call<T> call) {
        super("Unauthorized exception. [%s]: [%s]. Authorization header = [%s]"
                .formatted(call.request().method(), call.request().url().encodedPath(), call.request().header("Authorization")));
    }

}
