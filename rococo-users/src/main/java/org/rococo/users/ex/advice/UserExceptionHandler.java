package org.rococo.users.ex.advice;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.rococo.users.ex.ImageAlreadyExistsException;
import org.rococo.users.ex.ImageNotFoundException;
import org.rococo.users.ex.UserAlreadyExistsException;
import org.rococo.users.ex.UserNotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@GrpcAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class UserExceptionHandler {

    @GrpcExceptionHandler({UserAlreadyExistsException.class, ImageAlreadyExistsException.class})
    public StatusRuntimeException handleAlreadyExistsExceptions(Exception ex) {
        return Status.ALREADY_EXISTS
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler({UserNotFoundException.class, ImageNotFoundException.class})
    public StatusRuntimeException handleNotFoundExceptions(Exception ex) {
        return Status.NOT_FOUND
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

}
