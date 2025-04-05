package org.rococo.tests.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;

@Slf4j
public class NotificationComponent extends BaseComponent<NotificationComponent> {

    public NotificationComponent() {
        super($(byAttribute("data-testid", "toast")));
    }

    public NotificationComponent(SelenideElement self) {
        super(self);
    }

    private final SelenideElement toast = self.as("Notification toast"),
            toastMessage = self.$(".text-base").as("Notification message"),
            closeButton = self.$("button").as("Notification close button");


    public void close() {
        closeButton.click();
    }

    @Step("Notification should have status = [{status}]")
    public void shouldHaveStatus(NotificationStatus status) {
        log.info("Check notification has status = [{}]", status);
        toast.shouldBe(visible);
        toast.shouldHave(cssClass("variant-filled-" + status.getText().toLowerCase()));
        close();
    }

    @Step("Notification should have message = [{message}]")
    public void shouldHaveMessage(String message) {
        log.info("Check notification has message = [{}]", message);
        toast.shouldBe(visible);
        toastMessage.shouldHave(text(message));
        close();
    }

    @Step("Notification should have status = [{status}] and message = [{message}]")
    public void shouldHaveStatusAndMessage(NotificationStatus status, String message) {
        log.info("Check notification has status = [{}] and message = [{}]", status, message);
        toast.shouldBe(visible);
        toast.shouldHave(cssClass("variant-filled-" + status.getText()));
        toastMessage.shouldHave(text(message));
        close();
    }

    @Override
    public NotificationComponent shouldVisibleComponent() {
        toast.shouldBe(visible);
        toastMessage.shouldBe(visible);
        closeButton.shouldBe(visible);
        return this;
    }

    @Override
    public void shouldNotVisibleComponent() {
        toast.shouldNot(or("notification not visible or exist", visible, exist));
    }

    @Getter
    @RequiredArgsConstructor
    public enum NotificationStatus {

        /**
         * Used in actions: create, update entities
         */
        SUCCESS("primary"),

        /**
         * Used in actions: logout
         */
        INFO("tertiary"),


        ERROR("error");
        private final String text;
    }

}
