package org.rococo.users.ex.advice;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.rococo.users.ex.BadRequestException;
import org.rococo.users.ex.UserAlreadyExistException;
import org.rococo.users.ex.UserNotFoundException;

@GrpcAdvice
public class UserControllerAdvice {

    @GrpcExceptionHandler(UserAlreadyExistException.class)
    public StatusRuntimeException handleUserAlreadyExistException(UserAlreadyExistException ex) {
        return Status.ALREADY_EXISTS
                .withDescription(ex.getMessage())
                .withCause(ex)
                .asRuntimeException();
    }

    @GrpcExceptionHandler(UserNotFoundException.class)
    public StatusRuntimeException handleUserNotFoundException(UserNotFoundException ex) {
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
