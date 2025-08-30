package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.StatConditions.spends;

@ParametersAreNonnullByDefault
public class SpendingTable extends BaseComponent<SpendingTable> {

    private final SearchField searchField = new SearchField();

    private final ElementsCollection tableRows = getSelf().$("tbody").$$("tr");

    public SpendingTable() {
        super($("#spendings"));
    }

    @Step("Проверка, что таблица затрат загружена")
    public SpendingTable checkIsLoaded() {
        getSelf().shouldBe(visible);
        return this;
    }

    @Step("Выбор периода: {period}")
    public SpendingTable selectPeriod(@Nonnull DataFilterValues period) {
        getSelf().$("#period").shouldBe(visible).click();
        $(String.format("li[data-value='%s']", period.name())).shouldBe(visible).click();
        return this;
    }

    @Step("Редактирование затраты с описанием: {description}")
    public EditSpendingPage editSpending(@Nonnull String description) {
        getRowInTable(description).$("button[aria-label='Edit spending']").shouldBe(visible).click();
        return new EditSpendingPage();
    }

    @Step("Удаление затраты с описанием: {description}")
    public SpendingTable deleteSpending(@Nonnull String description) {
        getRowInTable(description).shouldBe(visible).click();
        getSelf().$("#delete").shouldBe(visible).click();
        return this;
    }

    @Step("Поиск затраты по описанию: {description}")
    public SpendingTable searchSpendingByDescription(@Nonnull String description) {
        searchField.searchField(description);
        getRowInTable(description).shouldBe(visible);
        return this;
    }

    @Step("Проверка, что таблица содержит записи: {expectedSpends}")
    public SpendingTable checkTableContains(@Nonnull String... expectedSpends) {
        for (String spend : expectedSpends) {
            searchSpendingByDescription(spend);
        }
        return this;
    }

    @Step("Проверка размера таблицы: ожидается {expectedSize}")
    public SpendingTable checkTableSize(int expectedSize) {
        getRowsTable().shouldHave(size(expectedSize));
        return this;
    }

    @Step("Проверка содержимого табоицы")
    public SpendingTable checkSpends(SpendJson... expectedSpends) {
        tableRows.shouldHave(spends(expectedSpends));
        return this;
    }

    private SelenideElement getRowInTable(String rowName) {
        return getSelf().$x(String.format(".//span[text()='%s']//ancestor::tr", rowName));
    }

    private ElementsCollection getRowsTable() {
        return getSelf().$$("tbody tr");
    }
}
