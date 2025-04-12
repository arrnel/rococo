package org.rococo.paintings.ex.advice;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.rococo.paintings.ex.ServiceUnavailableException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Slf4j
@GrpcAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExternalServiceExceptionHandler {

    @GrpcExceptionHandler(ServiceUnavailableException.class)
    public StatusRuntimeException handleServiceUnavailableException(ServiceUnavailableException ex) {
        log.error("{}.\nStack trace: {}", ex.getMessage(), ex.getStackTrace());
        return Status.UNAVAILABLE
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

}
