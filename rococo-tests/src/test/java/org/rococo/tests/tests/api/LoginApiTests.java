package org.rococo.tests.tests.api;

import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.rococo.tests.client.gateway.AuthApiClient;
import org.rococo.tests.client.gateway.core.store.ThreadSafeCookieStore;
import org.rococo.tests.config.Config;
import org.rococo.tests.enums.CookieType;
import org.rococo.tests.enums.HttpStatus;
import org.rococo.tests.jupiter.annotation.User;
import org.rococo.tests.jupiter.annotation.meta.ApiTest;
import org.rococo.tests.model.UserDTO;

import static org.junit.jupiter.api.Assertions.*;
import static org.rococo.tests.util.DataGenerator.generatePassword;
import static org.rococo.tests.util.OAuthUtil.generateCodeChallenge;
import static org.rococo.tests.util.OAuthUtil.generateCodeVerifier;

@ApiTest
@Feature("API")
@Story("[API] Login tests")
@DisplayName("[API] Login tests")
class LoginApiTests {

    private static final Config CFG = Config.getInstance();
    private static final String REDIRECT_URI = CFG.frontUrl() + "/authorized",
            RESPONSE_TYPE = "code",
            CLIENT_ID = "client",
            SCOPE = "openid",
            CODE_CHALLENGE_METHOD = "S256",
            GRANT_TYPE = "authorization_code";

    private final AuthApiClient authApi = new AuthApiClient();
    private final AuthApiClient authApiNoFollow = new AuthApiClient(false);

    @User
    @Test
    @DisplayName("Can login user with valid data")
    void canLoginUserWithValidDataTest(UserDTO user) {

        // Steps
        var token = authApi.signIn(user.getUsername(), user.password());

        // Assertions
        assertNotNull(token);

    }

    @User
    @Test
    @DisplayName("Should returns BAD_REQUEST if password is invalid")
    void shouldReturnBadRequestIfPasswordIsInvalidTest(UserDTO user) {

        // Data
        var codeChallenge = generateCodeChallenge(generateCodeVerifier());

        // Precondition
        ThreadSafeCookieStore.INSTANCE.removeAll();

        // Steps
        authApi.sendAuthorizeRequest(
                RESPONSE_TYPE, CLIENT_ID, SCOPE, REDIRECT_URI, codeChallenge, CODE_CHALLENGE_METHOD
        );
        var result = authApiNoFollow.sendLoginRequest(
                user.getUsername(), generatePassword(), ThreadSafeCookieStore.INSTANCE.cookieValue(CookieType.CSRF.getCookieName())
        );

        // Assertions
        assertAll(
                () -> assertEquals(HttpStatus.FOUND, result.code()),
                () -> assertEquals(CFG.authUrl() + "/login?error", result.headers().get("Location")));

    }

}
