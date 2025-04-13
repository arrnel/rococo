package org.rococo.files.ex.advice;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@Slf4j
@GrpcAdvice
public class GlobalExceptionHandler {

    @GrpcExceptionHandler(Exception.class)
    public StatusRuntimeException handleGlobalException(Exception ex) {
        log.error("Message: {}.\nStack trace: {}", ex.getMessage(), ex.getStackTrace());
        return Status.UNKNOWN
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

}
