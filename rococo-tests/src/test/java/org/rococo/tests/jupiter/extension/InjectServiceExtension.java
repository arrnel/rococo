package org.rococo.tests.jupiter.extension;

import org.apache.commons.lang3.EnumUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.rococo.tests.enums.ServiceType;
import org.rococo.tests.jupiter.annotation.meta.InjectService;
import org.rococo.tests.model.Token;
import org.rococo.tests.service.*;
import org.rococo.tests.service.db.*;
import org.rococo.tests.service.gateway.*;
import org.rococo.tests.service.grpc.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class InjectServiceExtension implements TestInstancePostProcessor {

    private static final ServiceType DEFAULT_SERVICE_TYPE = EnumUtils.getEnumIgnoreCase(
            ServiceType.class, System.getProperty("test.service.precondition", "JDBC"), ServiceType.DB);


    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {

        var validTypes = List.of(
                ArtistService.class,
                CountryService.class,
                MuseumService.class,
                PaintingService.class,
                UserService.class,
                RococoService.class);

        Arrays.stream(testInstance.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(InjectService.class))
                .filter(field -> validTypes.contains(field.getType()))
                .forEach(field -> {
                    ServiceType serviceType = field.getAnnotation(InjectService.class).value();
                    serviceType = serviceType == ServiceType.DEFAULT ? DEFAULT_SERVICE_TYPE : serviceType;

                    try {
                        Object service = serviceFactories.get(field.getType()).apply(serviceType);
                        inject(field, testInstance, service);
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to inject service for field: " + field.getName(), ex);
                    }
                });
    }

    private void inject(Field field,
                        Object testInstance,
                        Object object
    ) throws Exception {
        field.setAccessible(true);
        field.set(testInstance, object);
        field.setAccessible(false);
    }

    private final Map<Class<?>, Function<ServiceType, Object>> serviceFactories = Map.of(
            ArtistService.class, this::createArtistService,
            CountryService.class, this::createCountryService,
            MuseumService.class, this::createMuseumService,
            PaintingService.class, this::createPaintingService,
            UserService.class, this::createUserService,
            RococoService.class, this::createRococoService
    );

    private ArtistService createArtistService(ServiceType type) {
        return switch (type) {
            case DB -> new ArtistServiceDb();
            case GRPC -> new ArtistServiceGrpc();
            case API -> new ArtistServiceGateway(getUserToken());
            default -> throw new IllegalStateException("Unexpected service type: " + type);
        };
    }

    private CountryService createCountryService(ServiceType type) {
        return switch (type) {
            case DB -> new CountryServiceDb();
            case GRPC -> new CountryServiceGrpc();
            case API -> new CountryServiceGateway();
            default -> throw new IllegalStateException("Unexpected service type: " + type);
        };
    }

    private MuseumService createMuseumService(ServiceType type) {
        return switch (type) {
            case DB -> new MuseumServiceDb();
            case GRPC -> new MuseumServiceGrpc();
            case API -> new MuseumServiceGateway(getUserToken());
            default -> throw new IllegalStateException("Unexpected service type: " + type);
        };
    }

    private PaintingService createPaintingService(ServiceType type) {
        return switch (type) {
            case DB -> new PaintingServiceDb();
            case GRPC -> new PaintingServiceGrpc();
            case API -> new PaintingServiceGateway(getUserToken());
            default -> throw new IllegalStateException("Unexpected service type: " + type);
        };
    }

    private UserService createUserService(ServiceType type) {
        return switch (type) {
            case DB -> new UserServiceDb();
            case GRPC -> new UserServiceGrpc();
            case API -> new UserServiceGateway(getUserToken());
            default -> throw new IllegalStateException("Unexpected service type: " + type);
        };
    }

    private RococoService createRococoService(ServiceType type) {
        return switch (type) {
            case DB -> new RococoServiceDb();
            case GRPC -> new RococoServiceGrpc();
            case API -> new RococoServiceGateway(getUserToken());
            default -> throw new IllegalStateException("Unexpected service type: " + type);
        };
    }

    private static Token getUserToken() {
        return DefaultUserTokenUpdater.INSTANCE.getToken();
    }

}
