package org.rococo.tests.tests.fake.kafka;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import org.rococo.tests.client.gateway.AuthApi;
import org.rococo.tests.client.gateway.core.RestClient;
import org.rococo.tests.client.gateway.core.store.ThreadSafeCookieStore;
import org.rococo.tests.config.Config;
import org.rococo.tests.enums.CookieType;
import org.rococo.tests.jupiter.annotation.meta.KafkaTest;
import org.rococo.tests.service.kafka.KafkaConsumerService;
import org.rococo.tests.util.DataGenerator;

import javax.annotation.ParametersAreNonnullByDefault;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Isolated
@KafkaTest
@Feature("FAKE")
@Story("[Kafka] Users tests")
@DisplayName("[Kafka] Users tests")
@ParametersAreNonnullByDefault
class AuthKafkaTest {

    private static final Config CFG = Config.getInstance();

    private final AuthApi authApi = new RestClient.EmptyClient(CFG.authUrl()).create(AuthApi.class);

    @Test
    @DisplayName("Should produce user into Kafka")
    void userShouldBeProducedToKafkaTest() throws Exception {

        // Data
        var user = DataGenerator.generateUser();

        // Steps
        authApi.getCookies().execute();
        authApi.register(user.getUsername(),
                        user.getTestData().getPassword(),
                        user.getTestData().getPassword(),
                        ThreadSafeCookieStore.INSTANCE.cookieValue(CookieType.CSRF.getCookieName()))
                .execute();
        var result = KafkaConsumerService.getUser(user.getUsername());

        // Assertions
        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());

    }

}
