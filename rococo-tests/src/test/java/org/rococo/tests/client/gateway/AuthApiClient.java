package org.rococo.tests.client.gateway;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.time.StopWatch;
import org.jetbrains.annotations.Nullable;
import org.rococo.tests.client.gateway.core.RestClient;
import org.rococo.tests.client.gateway.core.interceptor.AuthorizedCodeInterceptor;
import org.rococo.tests.client.gateway.core.store.AuthStore;
import org.rococo.tests.client.gateway.core.store.ThreadSafeCookieStore;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.util.OAuthUtil;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.rococo.tests.enums.CookieType.CSRF;

@Slf4j
@ParametersAreNonnullByDefault
public class AuthApiClient extends RestClient {

    private static final String REDIRECT_URI = CFG.frontUrl() + "/authorized",
            RESPONSE_TYPE = "code",
            CLIENT_ID = "client",
            SCOPE = "openid",
            CODE_CHALLENGE_METHOD = "S256",
            GRANT_TYPE = "authorization_code";

    private final AuthApi authApi;
    private final UsersApiClient userdataApiClient = new UsersApiClient();

    public AuthApiClient() {
        super(CFG.authUrl(),
                true,
                HttpLoggingInterceptor.Level.HEADERS,
                new AuthorizedCodeInterceptor());
        this.authApi = retrofit.create(AuthApi.class);
    }

    @Step("Send request POST:[rococo-auth]/register")
    public UserDTO register(String username, String password) {
        log.info("Register user: username = [{}], password = [{}]", username, password);
        clearCookieStore();
        execute(authApi.getCookies());
        execute(authApi.register(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue(CSRF.getCookieName())));
        return Optional.ofNullable(waitUserFromUsers(username))
                .orElseThrow(() -> new RuntimeException("Could not find user = [" + username + "]"))
                .password(password);

    }

    @Nonnull
    public UserDTO signIn(UserDTO user) {
        var token = signIn(user.getUsername(), user.password());
        return user.token(token);
    }

    @Nonnull
    @Step("Sign in by: username = [{username}], password = [{password}]")
    public String signIn(String username, String password) {
        log.info("Sign in user by: username = [{}], password = [{}]", username, password);
        clearCookieStore();

        final String codeVerifier = OAuthUtil.generateCodeVerifier();
        authorize(OAuthUtil.generateCodeChallenge(codeVerifier));
        login(username, password);
        var token = token(codeVerifier);

        AuthStore.INSTANCE.setToken(token);
        return token;
    }

    @Step("Send authorize request. GET:[rococo-auth]/oauth2/authorize")
    private void authorize(final String codeChallenge) {
        execute(authApi.authorize(
                RESPONSE_TYPE,
                CLIENT_ID,
                SCOPE,
                REDIRECT_URI,
                codeChallenge,
                CODE_CHALLENGE_METHOD));
    }

    @Step("Send login request. POST:[rococo-auth]/login")
    private void login(String username, String password) {
        execute(authApi.login(
                username,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue(CSRF.getCookieName())));
    }

    @Nonnull
    @Step("Send get token request. POST:[rococo-auth]/oauth2/token")
    private String token(String codeVerifier) {
        var code = Objects.requireNonNull(AuthStore.INSTANCE.getCode());
        return "Bearer " + Objects.requireNonNull(execute(
                authApi.token(
                        CLIENT_ID,
                        REDIRECT_URI,
                        GRANT_TYPE,
                        code,
                        codeVerifier))
                .get("id_token")
                .asText());
    }

    @Nullable
    private UserDTO waitUserFromUsers(String username) {
        StopWatch sw = StopWatch.createStarted();
        UserDTO user = null;

        while (user == null && sw.getTime(TimeUnit.SECONDS) < 30) {
            try {
                user = userdataApiClient.currentUser(username).orElse(null);
                if (user != null && user.getId() != null) {
                    break;
                } else {
                    Thread.sleep(1_000);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return user;
    }

    public void clearCookieStore() {
        ThreadSafeCookieStore.INSTANCE.removeAll();
    }

}
