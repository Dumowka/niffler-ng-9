package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.ui.Bubble;
import guru.qa.niffler.model.ui.SpendUi;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

public class StatConditions {

    public static WebElementCondition statBubbles(Color expectedColor) {
        return new WebElementCondition("color") {

            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final String rgba = element.getCssValue("background-color");
                return new CheckResult(
                        expectedColor.rgb.equals(rgba),
                        rgba
                );
            }
        };
    }

    public static WebElementsCondition statBubbles(Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final String expectedBubblesString = Arrays.stream(expectedBubbles).toList().toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedBubbles.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedBubbles.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                List<Bubble> actualBubbleList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final String colorToCheck = expectedBubbles[i].color().rgb;
                    final String textToCheck = expectedBubbles[i].text();
                    final String rgba = elementToCheck.getCssValue("background-color");
                    final String text = elementToCheck.getText();
                    actualBubbleList.add(new Bubble(Color.getByValue(rgba), text));

                    if (passed) {
                        passed = colorToCheck.equals(rgba) && textToCheck.equals(text);
                    }
                }

                if (!passed) {
                    final String actualRgba = actualBubbleList.toString();
                    final String message = String.format(
                            "List colors mismatch (expected: %s, actual: %s)", expectedBubblesString, actualRgba
                    );
                    return rejected(message, actualRgba);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedBubblesString;
            }
        };
    }

    public static WebElementsCondition statBubblesInAnyOrder(Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final String expectedBubblesString = Arrays.stream(expectedBubbles).toList().toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedBubbles.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedBubbles.length, elements.size());
                    return rejected(message, elements);
                }

                Set<Bubble> actualBubbleList = elements.stream()
                        .map(element -> new Bubble(
                                Color.getByValue(element.getCssValue("background-color")),
                                element.getText()
                        )).collect(Collectors.toSet());

                actualBubbleList.addAll(Arrays.asList(expectedBubbles));

                boolean passed = actualBubbleList.size() == expectedBubbles.length;

                if (!passed) {
                    final String actualBubbles = actualBubbleList.toString();
                    final String message = String.format(
                            "List colors mismatch (expected: %s, actual: %s)", expectedBubblesString, actualBubbles
                    );
                    return rejected(message, actualBubbles);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedBubblesString;
            }
        };
    }

    public static WebElementsCondition statBubblesContains(Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final String expectedBubblesString = Arrays.stream(expectedBubbles).toList().toString();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles)) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }

                List<Bubble> actualBubbleList = elements.stream()
                        .map(element -> new Bubble(
                                Color.getByValue(element.getCssValue("background-color")),
                                element.getText()
                        )).toList();

                boolean passed = actualBubbleList.containsAll(List.of(expectedBubbles));

                if (!passed) {
                    final String actualBubbles = actualBubbleList.toString();
                    final String message = String.format(
                            "List colors mismatch (expected: %s, actual: %s)", expectedBubblesString, actualBubbles
                    );
                    return rejected(message, actualBubbles);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedBubblesString;
            }
        };
    }

    public static WebElementsCondition spends(SpendJson... expectedSpends) {
        return new WebElementsCondition() {
            private final List<SpendUi> expectedSpendsList = Arrays.stream(expectedSpends).map(SpendUi::fromSpendJson).toList();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedSpends)) {
                    throw new IllegalArgumentException("No expected spends given");
                }

                boolean passed = true;
                List<SpendUi> actualSpendsList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final SpendUi expectedSpend = expectedSpendsList.get(i);
                    final WebElement elementToCheck = elements.get(i);

                    SpendUi spendToCheck = new SpendUi(
                            elementToCheck.findElement(By.cssSelector("td:nth-child(2)")).getText(),
                            elementToCheck.findElement(By.cssSelector("td:nth-child(3)")).getText(),
                            elementToCheck.findElement(By.cssSelector("td:nth-child(4)")).getText(),
                            elementToCheck.findElement(By.cssSelector("td:nth-child(5)")).getText()
                    );
                    actualSpendsList.add(spendToCheck);

                    if (passed) {
                        passed = expectedSpend.equals(spendToCheck);
                    }
                }

                if (!passed) {
                    final String actualSpends = actualSpendsList.toString();
                    final String message = String.format(
                            "List colors mismatch (expected: %s, actual: %s)", expectedSpendsList, actualSpends
                    );
                    return rejected(message, actualSpends);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedSpendsList.toString();
            }
        };
    }
}
