package org.rococo.museum.ex.advice;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.rococo.museum.ex.BadRequestException;
import org.rococo.museum.ex.MuseumAlreadyExistException;
import org.rococo.museum.ex.MuseumNotFoundException;

@GrpcAdvice
public class MuseumControllerAdvice {

    @GrpcExceptionHandler(MuseumAlreadyExistException.class)
    public StatusRuntimeException handleArtistAlreadyExistException(MuseumAlreadyExistException ex) {
        return Status.ALREADY_EXISTS
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler(MuseumNotFoundException.class)
    public StatusRuntimeException handleArtistNotFoundException(MuseumNotFoundException ex) {
        return Status.NOT_FOUND
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler(BadRequestException.class)
    public StatusRuntimeException handleBadRequestException(BadRequestException ex) {
        return Status.INVALID_ARGUMENT
                .withDescription("Bad request. Errors: " + ex.getErrors().toString())
                .withCause(ex)
                .asRuntimeException();
    }


}
