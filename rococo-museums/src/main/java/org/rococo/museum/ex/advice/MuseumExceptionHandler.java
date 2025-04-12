package org.rococo.museum.ex.advice;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.rococo.museum.ex.*;

@GrpcAdvice
public class MuseumExceptionHandler {

    @GrpcExceptionHandler({MuseumAlreadyExistsException.class, ImageAlreadyExistsException.class})
    public StatusRuntimeException handleAlreadyExistsExceptions(Exception ex) {
        return Status.ALREADY_EXISTS
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler({MuseumNotFoundException.class, CountryNotFoundException.class, ImageNotFoundException.class})
    public StatusRuntimeException handleNotFoundExceptions(Exception ex) {
        return Status.NOT_FOUND
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

}
