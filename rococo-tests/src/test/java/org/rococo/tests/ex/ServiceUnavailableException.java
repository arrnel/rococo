package org.rococo.tests.ex;

import io.grpc.Status;
import lombok.Getter;
import retrofit2.Call;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class ServiceUnavailableException extends RuntimeException {

    public <T> ServiceUnavailableException(String serviceName, Call<T> call, int status, @Nullable String message) {
        super("Service [%s] is not available. %d %s: %s, message = [%s]"
                .formatted(serviceName, status, call.request().method(), call.request().url().encodedPath(), message));
    }

    public ServiceUnavailableException(final String serviceName, final Status status) {
        super("Service [%s] is not available. Status code = [%s], message = [%s]"
                .formatted(serviceName, status.getCode(), status.getDescription()));
    }

}
