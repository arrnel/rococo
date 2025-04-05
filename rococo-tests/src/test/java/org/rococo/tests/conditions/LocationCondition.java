package org.rococo.tests.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.NoSuchElementException;

@ParametersAreNonnullByDefault
public final class LocationCondition {

    @Nonnull
    public static WebElementCondition location(
            String countryName,
            String city
    ) {

        final var expectedText = "%s, %s".formatted(countryName, city);

        return new WebElementCondition("expected location: [%s]".formatted(expectedText)) {

            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {

                var isCurrentElementLocation = "museum-geo".equals(element.getAttribute("data-testid"));
                var locationElement = isCurrentElementLocation
                        ? element
                        : element.findElements(By.xpath("//*[data-testid='museum-geo']")).stream()
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("Element not found with data-testid = [museum-geo]"));

                var actualText = locationElement.getText().replaceAll("\n {16}", "");
                return new CheckResult(expectedText.equals(actualText), expectedText);

            }
        };

    }

}
