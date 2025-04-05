package org.rococo.gateway.ex;

import io.grpc.Status;
import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@Getter
@ParametersAreNonnullByDefault
public class ServiceUnavailableException extends RuntimeException {

    private final String serviceName;

    public ServiceUnavailableException(final String serviceName) {
        super("Service [%s] is not available".formatted(serviceName));
        this.serviceName = serviceName;
    }

    public ServiceUnavailableException(final String serviceName, final Status status) {
        super("Service [%s] is not available. Status code = [%s], status description = [%s]".formatted(serviceName, status.getCode(), status.getDescription()));
        this.serviceName = serviceName;
    }

}
