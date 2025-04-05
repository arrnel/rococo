package org.rococo.files.ex.advice;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.rococo.files.ex.BadRequestException;
import org.rococo.files.ex.ImageAlreadyExistException;
import org.rococo.files.ex.ImageNotFoundException;
import org.rococo.files.ex.InternalException;

@Slf4j
@GrpcAdvice
public class ImageControllerAdvice {

    @GrpcExceptionHandler(ImageAlreadyExistException.class)
    public StatusRuntimeException handleImageAlreadyExistException(ImageAlreadyExistException ex) {
        log.error(ex.getMessage());
        return Status.ALREADY_EXISTS
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler(ImageNotFoundException.class)
    public StatusRuntimeException handleImageNotFoundException(ImageNotFoundException ex) {
        log.error(ex.getMessage());
        return Status.NOT_FOUND
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler(BadRequestException.class)
    public StatusRuntimeException handleBadRequestException(BadRequestException ex) {
        log.error(ex.getMessage());
        return Status.INVALID_ARGUMENT
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler({InternalException.class, Exception.class})
    public StatusRuntimeException handleInternalException(Exception ex) {
        log.error(ex.getMessage());
        return Status.INTERNAL
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

}
