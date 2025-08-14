package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class PeoplePage extends BasePage<PeoplePage> {

    private final SelenideElement friendsTableShowButton = $x("//h2[text()='Friends']");
    private final SelenideElement allPeopleTableShowButton = $x("//h2[text()='All people']");
    private final SelenideElement allPeopleTable = $("#all");

    private final SearchField searchField = new SearchField();

    @Override
    @Step("Проверка, что страница 'All people' загружена")
    public PeoplePage checkThatPageLoaded() {
        friendsTableShowButton.shouldBe(visible);
        allPeopleTableShowButton.shouldBe(visible);
        allPeopleTable.shouldBe(visible);
        return this;
    }

    @Step("Проверка исходящей заявки в друзья для: {name}")
    public PeoplePage checkOutcomeInvitation(String name) {
        getRowInTable(allPeopleTable, name).shouldBe(visible)
                .$x(".//span[text()='Waiting...']").shouldBe(visible);
        return this;
    }

    @Step("Проверка, что в таблице 'All people' есть запись с именем: {rowName}")
    public PeoplePage checkRowInAllPeopleTable(String rowName) {
        getRowInTable(allPeopleTable, rowName).shouldBe(visible)
                .$x(".//button[text()='Add friend']").shouldBe(visible);
        return this;
    }

    @Step("Поиск пользователя по имени: {name}")
    public PeoplePage searchPeople(String name) {
        searchField.searchField(name);
        return this;
    }

    private SelenideElement getRowInTable(SelenideElement table, String rowName) {
        return table.$x(String.format(".//p[text()='%s']//ancestor::tr", rowName));
    }
}
