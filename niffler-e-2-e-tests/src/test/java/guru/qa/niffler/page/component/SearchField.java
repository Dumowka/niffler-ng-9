package guru.qa.niffler.page.component;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class SearchField extends BaseComponent<SearchField> {

    private final By searchField = By.cssSelector("input[aria-label='search']");

    public SearchField() {
        super($x("//form[contains(@class, 'MuiBox-root')]"));
    }

    @Step("Выполнение поиска по запросу: {query}")
    public SearchField searchField(@Nonnull String query) {
        clearIfNotEmpty();
        getSelf().$(searchField).setValue(query).pressEnter();
        return this;
    }

    @Step("Очистка поля поиска, если оно не пустое")
    public SearchField clearIfNotEmpty() {
        if (getSelf().$(searchField).is(not(empty))) {
            getSelf().$("#input-submit").shouldBe(visible).click();
        }
        return this;
    }
}
