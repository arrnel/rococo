package org.rococo.tests.jupiter.annotation.meta;

import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rococo.tests.jupiter.extension.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag("Fake")
@Tag("Grpc")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({
        AllureJunit5.class,
        UserExtension.class,
        UsersExtension.class,
        ArtistExtension.class,
        ArtistsExtension.class,
        CountryExtension.class,
        MuseumExtension.class,
        MuseumsExtension.class,
        PaintingExtension.class,
        PaintingsExtension.class
})
public @interface GrpcTest {

}
