package guru.qa.niffler.page;

import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.page.component.DataFilterValues;
import guru.qa.niffler.page.component.DeleteSpendingsDialogWindow;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {


    @Getter
    private final Header header = new Header();
    private final SpendingTable spendingTable = new SpendingTable();
    private final DeleteSpendingsDialogWindow deleteSpendingsDialogWindow = new DeleteSpendingsDialogWindow();
    @Getter
    private final StatComponent statComponent = new StatComponent();

    @Override
    @Step("Проверка, что главная страница загружена")
    public MainPage checkThatPageLoaded() {
        spendingTable.checkIsLoaded();
        statComponent.checkThatComponentLoaded();
        header.checkHeaderText();
        return this;
    }

    @Step("Поиск затрат по описанию: {spendingDescription}")
    public MainPage findSpending(@Nonnull String spendingDescription) {
        spendingTable.searchSpendingByDescription(spendingDescription);
        return this;
    }

    @Step("Выбор периода: {period}")
    public MainPage selectPeriod(@Nonnull DataFilterValues period) {
        spendingTable.selectPeriod(period);
        return this;
    }

    @Step("Редактирование затраты с описанием: {description}")
    public EditSpendingPage editSpending(@Nonnull String description) {
        return spendingTable.editSpending(description);
    }

    @Step("Удаление затраты с описанием: {description}")
    public MainPage removeSpending(@Nonnull String description) {
        spendingTable.deleteSpending(description);
        deleteSpendingsDialogWindow.checkThatWindowIsAppear().clickOnDeleteButton();
        checkAlert("Spendings succesfully deleted");
        return this;
    }

    @Step("Проверка, что таблица содержит затрату с описанием: {description}")
    public MainPage checkThatTableContainsSpending(@Nonnull String description) {
        spendingTable.searchSpendingByDescription(description);
        return this;
    }

    @Step("Проверка, что таблица содержит затраты с описанием: {descriptions}")
    public MainPage checkThatTableContainsSpendings(@Nonnull String... descriptions) {
        spendingTable.checkTableContains(descriptions);
        return this;
    }

    @Step("Проверка размера таблицы: ожидается {expectedSize}")
    public MainPage checkSpendingsTableSize(int expectedSize) {
        spendingTable.checkTableSize(expectedSize);
        return this;
    }

    @Step("Проверка наличия затрат {expectedSpends}")
    public MainPage checkSpendings(SpendJson... expectedSpends) {
        spendingTable.checkSpends(expectedSpends);
        return this;
    }
}
