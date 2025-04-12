package org.rococo.files.ex.advice;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.rococo.files.ex.InternalException;

@Slf4j
@GrpcAdvice
public class GlobalExceptionHandler {

    @GrpcExceptionHandler(Exception.class)
    public StatusRuntimeException handleGlobalException(Exception ex) {
        log.error(ex.getMessage());
        return Status.UNKNOWN
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

}
