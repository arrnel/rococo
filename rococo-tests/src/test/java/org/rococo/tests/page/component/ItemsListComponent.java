package org.rococo.tests.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import com.codeborne.selenide.impl.Alias;
import lombok.Getter;
import org.rococo.tests.enums.EntityType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class ItemsListComponent extends BaseComponent<ItemsListComponent> {

    private static final long UPDATE_LIST_TIMEOUT = 200L;
    private final ElementsCollection listItems = self.$$("li");
    private final SearchField searchComponent;
    private final String itemType;
    private final String itemsType;

    public ItemsListComponent(EntityType entityType, SelenideElement self, SelenideElement searchElement) {
        super(self);
        this.itemType = entityType.name().toLowerCase();
        this.itemsType = itemType + "s";
        this.searchComponent = new SearchField(searchElement);
    }

    public void shouldBeEmpty() {
        $(byAttribute("data-testid", "empty-list-filtered-title")).shouldBe(visible);
        $(byAttribute("data-testid", "empty-list-filtered-message")).shouldBe(visible);
    }

    public SelenideElement getByName(String itemName) {
        return findItemByName(itemName).orElseThrow(() ->
                new ElementNotFound(
                        WebDriverRunner.driver(),
                        new Alias("item"),
                        "%s: %s".formatted(itemType, itemName),
                        visible
                ));
    }

    public void shouldContainsItem(String itemName) {
        findItemByName(itemName)
                .orElseThrow(() -> new ElementNotFound(
                        WebDriverRunner.driver(),
                        new Alias(itemType),
                        "%s: %s".formatted(itemType, itemName),
                        visible
                ));
    }

    public void shouldNotContainItem(String itemName) {
        findItemByName(itemName).ifPresent(element -> {
            throw new ElementShouldNot(
                    WebDriverRunner.driver(),
                    new Alias(itemType),
                    "%s: %s".formatted(itemType, itemName),
                    "",
                    exist,
                    null,
                    self,
                    new AssertionError("Found item: %s".formatted(itemName))
            );
        });
    }

    public void shouldContainItems(List<String> itemsName) {
        var result = findItemsByNames(itemsName);
        if (result.isAllFounded()) return;
        throw new ElementNotFound(
                WebDriverRunner.driver(),
                new Alias(itemsType),
                "%s: %s".formatted(itemsType, String.join(", ", itemsName)),
                visible,
                new AssertionError("Not found %s: %s".formatted(itemsType, String.join(", ", result.notFoundedItemsName)))
        );
    }

    public void shouldNotContainItems(List<String> itemsName) {
        var result = findItemsByNames(itemsName);
        if (result.isAllAbsent()) return;
        throw new ElementNotFound(
                WebDriverRunner.driver(),
                new Alias(itemsType),
                "%s: %s".formatted(itemsType, itemsName),
                visible,
                new AssertionError("Found %s: %s".formatted(itemsType, String.join(", ", result.foundedItemsName)))
        );
    }

    public void shouldContainsItemsByQuery(String query, List<String> itemNames) {
        var result = findItemsByQuery(query, itemNames);
        if (result.isAllFounded()) return;
        throw new ElementShould(
                WebDriverRunner.driver(),
                new Alias(itemsType),
                "contains %s with text: %s".formatted(itemsType, itemNames),
                "contains with text: " + itemNames,
                exist,
                null,
                self,
                new AssertionError("Not found %s: %s".formatted(itemsType, String.join(", ", result.notFoundedItemsName)))
        );
    }

    public void shouldNotContainsItemsByQuery(String query, List<String> itemNames) {
        var result = findItemsByQuery(query, itemNames);
        if (result.isAllFounded()) return;
        throw new ElementShouldNot(
                WebDriverRunner.driver(),
                new Alias(itemsType),
                "contains %s with text: %s".formatted(itemsType, itemNames),
                "",
                exist,
                null,
                self,
                new AssertionError("Found %s: %s".formatted(itemsType, String.join(", ", result.foundedItemsName)))
        );
    }

    private FindElementsResult findItemsByNames(List<String> itemsName) {
        FindElementsResult result = new FindElementsResult(itemsName);
        itemsName.forEach(name ->
                findItemByName(name)
                        .ifPresent(el ->
                                result.foundItem(name)));
        return result;
    }

    private Optional<SelenideElement> findItemByName(String itemText) {

        if (itemText.isEmpty()) throw new IllegalArgumentException("itemText cannot be empty");

        var previousItemsCount = 0;
        searchComponent.search(itemText);
        Selenide.sleep(UPDATE_LIST_TIMEOUT);

        if (!self.is(visible, Duration.ofSeconds(5)))
            return Optional.empty();

        while (true) {
            var currentItemsCount = listItems.size();
            var foundedItem = findItemInRange(itemText, previousItemsCount, currentItemsCount);

            if (foundedItem.isPresent()) return foundedItem;

            scrollToLastAndWait();

            if (previousItemsCount == currentItemsCount) {
                return Optional.empty();
            }
            previousItemsCount = currentItemsCount;
        }

    }

    private FindElementsResult findItemsByQuery(String query, List<String> itemsName) {

        if (query.isEmpty())
            throw new IllegalArgumentException("query should not be empty");
        if (itemsName.isEmpty())
            throw new IllegalArgumentException("itemsName should not be empty");

        AtomicInteger previousItemsCount = new AtomicInteger();
        FindElementsResult result = new FindElementsResult(itemsName);

        searchComponent.search(query);
        Selenide.sleep(UPDATE_LIST_TIMEOUT);

        if (!self.is(visible, Duration.ofSeconds(5)))
            return result;

        while (true) {
            var currentItemsCount = listItems.size();
            itemsName.forEach(itemName ->
                    findItemInRange(itemName, previousItemsCount.get(), currentItemsCount)
                            .ifPresent(foundedItem -> result.foundItem(itemName)));

            scrollToLastAndWait();

            if (result.isAllFounded() || previousItemsCount.get() == currentItemsCount) {
                return result;
            }

            previousItemsCount.set(currentItemsCount);

        }

    }

    private Optional<SelenideElement> findItemInRange(String itemName, int start, int end) {
        return IntStream.range(start, end)
                .filter(i -> listItems.get(i).$(".item-title").has(text(itemName)))
                .mapToObj(listItems::get)
                .findFirst();
    }

    private void scrollToLastAndWait() {
        listItems.last().scrollIntoView(true);
        Selenide.sleep(UPDATE_LIST_TIMEOUT);
    }

    @Override
    public ItemsListComponent shouldVisibleComponent() {
        self.shouldBe(visible);
        return this;
    }

    @Override
    public void shouldNotVisibleComponent() {
        self.shouldNot(or("items list not visible or exists", visible, exist));
    }

    @Getter
    private static class FindElementsResult {

        private final List<String> notFoundedItemsName;
        private final List<String> foundedItemsName;

        private boolean allFounded = false;
        private boolean allAbsent = true;

        private FindElementsResult(List<String> itemsName) {

            if (itemsName.isEmpty())
                throw new IllegalArgumentException("Items name list cannot be empty");

            var validValues = itemsName.stream()
                    .filter(name -> name != null && !name.isEmpty())
                    .distinct()
                    .count();
            if (validValues != itemsName.size())
                throw new IllegalArgumentException("ItemsName can't contains null, empty and duplicate values");

            this.notFoundedItemsName = new ArrayList<>(itemsName);
            this.foundedItemsName = new ArrayList<>();

        }

        public void foundItem(String foundedItemName) {

            if (!this.notFoundedItemsName.contains(foundedItemName))
                throw new IllegalArgumentException("foundedItemName is already founded or was added to items list");

            this.notFoundedItemsName.remove(foundedItemName);
            this.foundedItemsName.add(foundedItemName);

            this.allFounded = notFoundedItemsName.isEmpty();
            this.allAbsent = false;

        }

    }

}
