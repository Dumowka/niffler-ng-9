package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class SearchField {
    private final SelenideElement self = $x("//form[contains(@class, 'MuiBox-root')]");

    private final By searchField = By.cssSelector("input[aria-label='search']");

    @Step("Выполнение поиска по запросу: {query}")
    public SearchField searchField(@Nonnull String query) {
        clearIfNotEmpty();
        self.$(searchField).setValue(query).pressEnter();
        return this;
    }

    @Step("Очистка поля поиска, если оно не пустое")
    public SearchField clearIfNotEmpty() {
        if (self.$(searchField).is(not(empty))) {
            self.$("#input-submit").shouldBe(visible).click();
        }
        return this;
    }
}
