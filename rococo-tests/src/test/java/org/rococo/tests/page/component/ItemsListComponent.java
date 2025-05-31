package org.rococo.tests.page.component;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.rococo.tests.enums.EntityType;
import org.rococo.tests.util.SearchResult;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selenide.$;

@Slf4j
@ParametersAreNonnullByDefault
public class ItemsListComponent extends BaseComponent<ItemsListComponent> {

    private final EntityType entityType;
    private static final long LOAD_WAIT_MS = 200;
    private final String tableName;

    public ItemsListComponent(EntityType entityType, SelenideElement self) {
        super(self);
        if (entityType == EntityType.USER)
            throw new IllegalArgumentException("You cannot use EntityType.USER in ItemsListComponent. Cause frontend not have users list");
        this.entityType = entityType;
        tableName = entityType.name().charAt(0) + entityType.name().substring(1).toLowerCase();
    }

    public Optional<SelenideElement> getItemByName(String itemName) {
        log.info("Getting {} item by name: {}", entityType, itemName);
        return findItemByName(itemName);
    }

    @Nonnull
    public List<String> getFoundItemsName(List<String> itemNames) {
        log.info("Get founded item names in {} table. Item names: {}", tableName, itemNames);
        return findItemsByName(itemNames)
                .getFoundItems();
    }

    @Nonnull
    public List<String> getMissedItemsName(List<String> itemNames) {
        log.info("Get missing item names in {} table. Item names: {}", tableName, itemNames);
        return findItemsByName(itemNames)
                .getMissingItems();
    }

    @Nonnull
    public ItemsListComponent shouldVisibleNoSearchResultMessage() {
        log.info("Should visible {} no search result message", tableName);
        $(byAttribute("data-testid", "empty-%ss-list".formatted(tableName.toLowerCase()))).shouldBe(visible);
        return this;
    }

    @Nonnull
    public ItemsListComponent shouldVisibleInitialEmptyListMessage() {
        log.info("Should visible {} initial empty list message", tableName);
        $(byAttribute("data-testid", "empty-list-filtered-title")).shouldBe(visible);
        $(byAttribute("data-testid", "empty-list-filtered-message")).shouldBe(visible);
        return this;
    }

    @Override
    @Step("Verify that the {entityType} search result list is visible")
    public ItemsListComponent shouldVisibleComponent() {
        log.info("Verifying visibility of {} list component", entityType);
        self.shouldBe(visible);
        return this;
    }

    @Override
    @Step("Verify that the {entityType} search result list is not visible")
    public void shouldNotVisibleComponent() {
        log.info("Verifying non-visibility of {} list component", entityType);
        self.shouldNotBe(visible);
    }

    /**
     * Finds element of item by exact name in search results
     *
     * @param itemName the name of the item to find
     * @return Optional containing the found SelenideElement or empty if not found
     */
    private Optional<SelenideElement> findItemByName(String itemName) {

        var itemsList = self.$$("li");

        AtomicInteger previousItemCount = new AtomicInteger();
        while (itemsList.size() > previousItemCount.get()) {
            var item = findItemInRange(
                    itemName,
                    previousItemCount.get(),
                    itemsList.size()
            );
            if (item.isPresent())
                return item;
            previousItemCount.set(itemsList.size());
            scrollToLastTableItem();
        }

        return Optional.empty();

    }

    /**
     * Searching items by name in search result.
     *
     * @param itemsName list of item names to search for
     * @return SearchResult object containing found and missing items
     */
    private SearchResult<String> findItemsByName(List<String> itemsName) {

        var itemsList = self.$$("li");
        var previousItemCount = new AtomicInteger();
        var searchResult = new SearchResult<>(itemsName);

        while (itemsList.size() > previousItemCount.get() || searchResult.isAllFounded()) {
            itemsName.forEach(itemName ->
                    findItemInRange(
                            itemName,
                            previousItemCount.get(),
                            itemsList.size()
                    ).ifPresent(item ->
                            searchResult.markAsFound(itemName)
                    )
            );
            if (searchResult.isAllFounded())
                return searchResult;
            previousItemCount.set(itemsList.size());
            scrollToLastTableItem();
        }

        return searchResult;

    }

    /**
     * Finds an item by name in new loaded items range.
     *
     * @param itemName the name of the item to find
     * @param start    the starting index of the range (inclusive)
     * @param end      the ending index of the range (exclusive)
     * @return Optional containing the found element or empty if not found
     */
    private Optional<SelenideElement> findItemInRange(String itemName, int start, int end) {
        var itemsList = self.$$("li");
        return IntStream.range(start, end)
                .filter(i -> itemsList.get(i).$(".item-title").has(text(itemName)))
                .mapToObj(itemsList::get)
                .findFirst();
    }

    /**
     * Scrolls to the last item in the list and wait load new items
     */
    private void scrollToLastTableItem() {
        self.scrollIntoView(true);
        Selenide.sleep(LOAD_WAIT_MS);
    }

}
