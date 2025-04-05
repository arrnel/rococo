package org.rococo.tests.page.component;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SearchField extends BaseComponent<SearchField> {

    private final SelenideElement input = self.$("input").as("Search input");
    private final SelenideElement searchIcon = self.$("button").as("Search submit icon");

    public SearchField(SelenideElement self) {
        super(self);
    }

    public SearchField() {
        super($(byAttribute("data-testid", "search")));
    }

    public void search(String text) {
        input.click();
        input.setValue(text);
        searchIcon.click();
    }

    @Override
    public SearchField shouldVisibleComponent() {
        input.shouldBe(visible);
        searchIcon.shouldBe(visible);
        return this;
    }

    @Override
    public void shouldNotVisibleComponent() {
        input.shouldNot(or("search input not visible or exist", visible, exist));
    }

}
