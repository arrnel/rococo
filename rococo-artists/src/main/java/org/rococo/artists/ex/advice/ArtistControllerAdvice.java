package org.rococo.artists.ex.advice;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.rococo.artists.ex.ArtistAlreadyExistException;
import org.rococo.artists.ex.ArtistNotFoundException;
import org.rococo.artists.ex.BadRequestException;

@Slf4j
@GrpcAdvice
public class ArtistControllerAdvice {

    @GrpcExceptionHandler(ArtistAlreadyExistException.class)
    public StatusRuntimeException handleArtistAlreadyExistException(ArtistAlreadyExistException ex) {
        log.error(ex.getMessage());
        return Status.ALREADY_EXISTS
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler(ArtistNotFoundException.class)
    public StatusRuntimeException handleArtistNotFoundException(ArtistNotFoundException ex) {
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
                .withDescription("Bad request. Errors: " + ex.getErrors().toString())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler
    public StatusRuntimeException handleBadRequestException(Exception ex) {
        log.error(ex.getMessage());
        return Status.UNKNOWN
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }


}
