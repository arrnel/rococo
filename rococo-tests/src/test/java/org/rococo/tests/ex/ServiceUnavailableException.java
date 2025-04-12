package org.rococo.tests.ex;

import io.grpc.Status;
import lombok.Getter;
import retrofit2.Call;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class ServiceUnavailableException extends RuntimeException {

    // REST
    public <T> ServiceUnavailableException(String serviceName, Call<T> call, int status, @Nullable String message) {
        super("Service [%s] is not available. %d %s: %s.%nMessage = [%s]"
                .formatted(serviceName, status, call.request().method(), call.request().url().encodedPath(), message));
    }

    // gRPC
    public ServiceUnavailableException(final String serviceName, final Status status) {
        super("Service [%s] is not available. Status code = [%s].%nMessage = [%s]"
                .formatted(serviceName, status.getCode(), status.getDescription()));
    }

}
