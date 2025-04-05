package org.rococo.tests.page;

import com.codeborne.selenide.SelenideElement;
import org.rococo.tests.config.Config;
import org.rococo.tests.page.component.NotificationComponent;
import org.rococo.tests.page.component.NotificationComponent.NotificationStatus;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
abstract class BasePage<T> {

    protected static final Config CFG = Config.getInstance();
    protected static final String BASE_URL = CFG.frontUrl();
    protected static final String AUTH_URL = CFG.authUrl();

    private final NotificationComponent notification = new NotificationComponent();

    protected final SelenideElement root = $("main");

    public abstract T shouldVisiblePage();

    @SuppressWarnings("unchecked")
    public T shouldVisibleNotification(NotificationStatus status, String message) {
        notification.shouldHaveStatusAndMessage(status, message);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T shouldVisibleNotification(NotificationStatus status) {
        notification.shouldHaveStatus(status);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T shouldVisibleNotification(String message) {
        notification.shouldHaveMessage(message);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T notificationClose() {
        notification.close();
        return (T) this;
    }

}
