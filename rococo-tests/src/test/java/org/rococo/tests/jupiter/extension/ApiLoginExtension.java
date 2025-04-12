package org.rococo.tests.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;
import org.rococo.tests.client.gateway.AuthApiClient;
import org.rococo.tests.client.gateway.core.store.ThreadSafeCookieStore;
import org.rococo.tests.enums.CookieType;
import org.rococo.tests.jupiter.annotation.ApiLogin;
import org.rococo.tests.model.UserDTO;
import org.rococo.tests.page.MainPage;
import org.rococo.tests.service.UserService;
import org.rococo.tests.service.db.UserServiceDb;
import org.rococo.tests.util.DataGenerator;

import java.util.Optional;

import static org.rococo.tests.enums.CookieType.JSESSIONID;

public class ApiLoginExtension implements BeforeEachCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);

    private final UserService userService = new UserServiceDb();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {

        AuthApiClient authClient = new AuthApiClient();

        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(anno -> {

                            var hasApiLoginCredentials = !(anno.username().isEmpty() || anno.password().isEmpty());
                            var hasUserCredentials = !(anno.value().username().isEmpty() || anno.value().password().isEmpty());

                            var hasApiLoginUsername = !(anno.username().isEmpty());
                            var hasUserUsername = !(anno.value().username().isEmpty());

                            String username;
                            String password;
                            Optional<UserDTO> user = Optional.empty();

                            if (hasApiLoginCredentials || hasUserCredentials) {
                                username = hasUserCredentials ? anno.value().username() : anno.username();
                                password = hasUserCredentials ? anno.value().password() : anno.password();
                                user = searchUserInStore(username);
                            } else if (hasApiLoginUsername || hasUserUsername) {
                                username = hasUserUsername ? anno.value().username() : anno.username();
                                user = searchUserInStore(username);
                                if (user.isEmpty()) {
                                    throw new IllegalStateException("User with username exists but not found in UserExtension and UsersExtensions store");
                                }
                                password = user.get().getTestData().getPassword();
                            } else {
                                var newUser = DataGenerator.generateUser();
                                username = newUser.getUsername();
                                password = newUser.getTestData().getPassword();
                                userService.create(newUser);
                            }

                            var token = authClient.signIn(username, password);
                            var idToken = token.replaceAll("Bearer ", "");
                            var jSessionId = ThreadSafeCookieStore.INSTANCE.cookieValue(JSESSIONID.name());

                            user.ifPresent(u -> u.getTestData().setToken(token)
                                    .setIdToken(idToken)
                                    .setJSessionId(jSessionId)
                                    .setPassword(password));

                            Selenide.open(MainPage.URL);

                            Selenide.localStorage().setItem("id_token", idToken);
                            WebDriverRunner.getWebDriver().manage().addCookie(new Cookie(CookieType.JSESSIONID.name(), jSessionId));

                            Selenide.open(MainPage.URL, MainPage.class)
                                    .shouldVisiblePage();

                        }
                );

    }

    private static Optional<UserDTO> searchUserInStore(String username) {
        var userFromUserExtension = UserExtension.findUser(username);
        var userFromUsersExtension = UsersExtension.findUser(username);

        return userFromUserExtension.isPresent()
                ? userFromUserExtension
                : userFromUsersExtension;
    }

}