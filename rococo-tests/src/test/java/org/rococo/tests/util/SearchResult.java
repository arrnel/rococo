package org.rococo.tests.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Getter
@ParametersAreNonnullByDefault
public class SearchResult<T> {

    private final List<T> missingItems;
    private final List<T> foundItems;
    private final Comparator<T> comparator;

    private boolean allFounded = false;
    private boolean allAbsent = true;

    public SearchResult(List<T> itemsToSearch) {
        this.missingItems = new ArrayList<>(itemsToSearch);
        this.foundItems = new ArrayList<>();
        this.comparator = null;
    }

    public SearchResult(List<T> itemsToSearch, Comparator<T> comparator) {
        this.missingItems = new ArrayList<>(itemsToSearch);
        this.foundItems = new ArrayList<>();
        this.comparator = comparator;
    }

    public void markAsFound(T item) {

        if (missingItems.isEmpty())
            throw new IllegalArgumentException("No missing items present");

        missingItems.stream()
                .filter(notFoundItem -> comparator != null
                        ? comparator.compare(notFoundItem, item) == 0
                        : notFoundItem.equals(item))
                .findFirst()
                .ifPresentOrElse(
                        i -> {
                            missingItems.remove(i);
                            foundItems.add(i);
                        },
                        () -> log.info("Marked object not found in items to search. Item : {} ", item)
                );

        allFounded = missingItems.isEmpty();
        allAbsent = false;

    }

}