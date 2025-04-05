package org.rococo.tests.jupiter.extension;

import org.rococo.tests.client.gateway.AuthApiClient;
import org.rococo.tests.config.Config;
import org.rococo.tests.jupiter.annotation.meta.InjectService;
import org.rococo.tests.model.Token;
import org.rococo.tests.service.RococoService;
import org.rococo.tests.service.db.RococoServiceDb;
import org.rococo.tests.service.gateway.RococoServiceGateway;
import org.rococo.tests.service.grpc.RococoServiceGrpc;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

abstract class BaseExtension {

    protected static final Config CFG = Config.getInstance();

    @InjectService
    protected final RococoService rococoService;

    protected BaseExtension() {
        rococoService = switch (CFG.serviceType()) {
            case DB -> new RococoServiceDb();
            case GRPC -> new RococoServiceGrpc();
            case API ->
                    new RococoServiceGateway(new Token(new AuthApiClient().signIn(CFG.testUserName(), CFG.testUserPassword())));
            default -> throw new IllegalStateException("Unexpected service type: " + CFG.serviceType());
        };
    }

    protected static boolean isParameterListOfType(Parameter parameter, Class<?> clazz) {
        if (parameter.getType().isAssignableFrom(List.class)) {
            ParameterizedType type = (ParameterizedType) parameter.getParameterizedType();
            Type[] typeArgs = type.getActualTypeArguments();
            return typeArgs.length == 1 && typeArgs[0].equals(clazz);
        }
        return false;
    }

}
