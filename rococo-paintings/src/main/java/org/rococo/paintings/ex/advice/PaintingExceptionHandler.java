package org.rococo.paintings.ex.advice;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.rococo.paintings.ex.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Slf4j
@GrpcAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PaintingExceptionHandler {

    @GrpcExceptionHandler({PaintingAlreadyExistsException.class, ImageAlreadyExistsException.class})
    public StatusRuntimeException handleAlreadyExistsExceptions(Exception ex) {
        log.info(ex.getMessage());
        return Status.ALREADY_EXISTS
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler({
            ArtistNotFoundException.class,
            MuseumNotFoundException.class,
            PaintingNotFoundException.class,
            ImageNotFoundException.class
    })
    public StatusRuntimeException handleNotFoundExceptions(Exception ex) {
        log.info(ex.getMessage());
        return Status.NOT_FOUND
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

}
