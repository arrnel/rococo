package org.rococo.tests.page.component;

import com.codeborne.selenide.*;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.Alias;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.containExactTextsCaseSensitive;
import static com.codeborne.selenide.CollectionCondition.itemWithText;
import static com.codeborne.selenide.Condition.*;

@ParametersAreNonnullByDefault
public class SelectField extends BaseComponent<SelectField> {

    private static final long UPDATE_LIST_TIMEOUT = 100L;

    public SelectField(SelenideElement self) {
        super(self);
    }

    public void selectByExactName(String name) {

        int previousOptionsCount = 0;

        while (true) {

            var options = self.getOptions();
            var currentOptionsCount = options.size();
            if (options.size() == previousOptionsCount) break;

            for (int item = previousOptionsCount; item < options.size(); item++) {
                if (name.equals(options.get(item).text())) {
                    options.get(item).scrollIntoView(true).click();
                    return;
                }
            }

            previousOptionsCount = currentOptionsCount;
            scrollToLastAndWait();

        }

        throw new ElementNotFound(
                WebDriverRunner.driver(),
                new Alias("Option with name = [%s] not found".formatted(name)),
                "text",
                Condition.text(name)
        );

    }

    public void shouldContainItem(String itemName) {
        if (itemName.isEmpty())
            throw new IllegalArgumentException("itemName cannot be empty");
        getAllOptions().shouldHave(itemWithText(itemName));
    }

    public void shouldContainItems(List<String> itemNames) {
        var isContainsInvalidItemNames = itemNames.stream()
                .anyMatch(name -> name == null || name.isEmpty());
        if (isContainsInvalidItemNames)
            throw new IllegalArgumentException("itemNames cannot contains null or empty items");
        getAllOptions().shouldHave(containExactTextsCaseSensitive(itemNames));
    }

    @Override
    public SelectField shouldVisibleComponent() {
        self.shouldBe(visible);
        return this;
    }

    @Override
    public void shouldNotVisibleComponent() {
        self.shouldNot(or("select not visible or exist", visible, exist));
    }

    private ElementsCollection getAllOptions() {
        int previousOptionsCount = 0;
        while (true) {
            var options = self.getOptions();
            var currentOptionsCount = options.size();
            if (options.size() == previousOptionsCount) return options;
            previousOptionsCount = currentOptionsCount;
            scrollToLastAndWait();
        }
    }

    private void scrollToLastAndWait() {
        self.getOptions().last().scrollIntoView(true);
        Selenide.sleep(UPDATE_LIST_TIMEOUT);
    }

}
