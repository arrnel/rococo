package org.rococo.artists.ex.advice;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.rococo.artists.ex.ArtistAlreadyExistsException;
import org.rococo.artists.ex.ArtistNotFoundException;
import org.rococo.artists.ex.ImageAlreadyExistsException;
import org.rococo.artists.ex.ImageNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Slf4j
@GrpcAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ArtistExceptionHandler {

    @GrpcExceptionHandler({ArtistAlreadyExistsException.class, ImageAlreadyExistsException.class})
    public StatusRuntimeException handleArtistAlreadyExistsException(Exception ex) {
        log.error(ex.getMessage());
        return Status.ALREADY_EXISTS
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler({ArtistNotFoundException.class, ImageNotFoundException.class})
    public StatusRuntimeException handleArtistNotFoundException(Exception ex) {
        log.error(ex.getMessage());
        return Status.NOT_FOUND
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

}