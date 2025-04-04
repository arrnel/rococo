package org.rococo.paintings.ex.advice;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.rococo.paintings.ex.BadRequestException;
import org.rococo.paintings.ex.PaintingAlreadyExistException;
import org.rococo.paintings.ex.PaintingNotFoundException;

@GrpcAdvice
public class PaintingControllerAdvice {

    @GrpcExceptionHandler(PaintingAlreadyExistException.class)
    public StatusRuntimeException handlePaintingAlreadyExistException(PaintingAlreadyExistException ex) {
        return Status.ALREADY_EXISTS
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler(PaintingNotFoundException.class)
    public StatusRuntimeException handlePaintingNotFoundException(PaintingNotFoundException ex) {
        return Status.NOT_FOUND
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler(BadRequestException.class)
    public StatusRuntimeException handleBadRequestException(BadRequestException ex) {
        return Status.INVALID_ARGUMENT
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

}
