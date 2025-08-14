package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.DeclineFriendshipDialogWindow;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class FriendsPage extends BasePage<FriendsPage> {
    private final SelenideElement friendsTableShowButton = $x("//h2[text()='Friends']");
    private final SelenideElement allPeopleTableShowButton = $x("//h2[text()='All people']");
    private final SelenideElement requestsToFriendTable = $("#requests");
    private final SelenideElement friendsTable = $("#friends");
    private final SelenideElement noUserLabel = $x("//p[text()='There are no users yet']");

    private final String acceptButtonXpath = ".//button[text()='Accept']";
    private final String declineButtonXpath = ".//button[text()='Decline']";

    private final SearchField searchField = new SearchField();
    private final DeclineFriendshipDialogWindow declineFriendshipDialogWindow = new DeclineFriendshipDialogWindow();

    @Override
    @Step("Проверка, что страница 'Friends' загружена")
    public FriendsPage checkThatPageLoaded() {
        friendsTableShowButton.shouldBe(visible);
        allPeopleTableShowButton.shouldBe(visible);
        friendsTable.shouldBe(visible);
        return this;
    }

    @Step("Поиск пользователя по имени: {name}")
    public FriendsPage searchPeople(String name) {
        searchField.searchField(name);
        return this;
    }

    @Step("Открытие вкладки 'Friends'")
    public FriendsPage clickOnFriendsTable() {
        friendsTableShowButton.click();
        return this;
    }

    @Step("Открытие вкладки 'All people'")
    public PeoplePage clickOnAllPeopleTable() {
        allPeopleTableShowButton.click();
        return new PeoplePage();
    }

    @Step("Проверка, что {name} находится в списке друзей")
    public FriendsPage checkFriends(String name) {
        getRowInTable(friendsTable, name).shouldBe(visible)
                .$x(".//button[text()='Unfriend']").shouldBe(visible);
        return this;
    }

    @Step("Проверка входящей заявки в друзья от: {name}")
    public FriendsPage checkIncomeInvitation(String name) {
        SelenideElement row = getRowInTable(requestsToFriendTable, name).shouldBe(visible);
        row.$x(acceptButtonXpath).shouldBe(visible);
        row.$x(declineButtonXpath).shouldBe(visible);
        return this;
    }

    @Step("Прием входящей заявки в друзья от: {name}")
    public FriendsPage acceptIncomeInvitation(String name) {
        SelenideElement row = getRowInTable(requestsToFriendTable, name).shouldBe(visible);
        row.$x(acceptButtonXpath).shouldBe(visible).click();
        return this;
    }

    @Step("Отклонение входящей заявки в друзья от: {name}")
    public FriendsPage declineIncomeInvitation(String name) {
        SelenideElement row = getRowInTable(requestsToFriendTable, name).shouldBe(visible);
        row.$x(declineButtonXpath).shouldBe(visible).click();
        declineFriendshipDialogWindow.checkThatWindowIsAppear().clickOnDeclineButton();
        checkAlert(String.format("Invitation of %s is declined", name));
        return this;
    }

    @Step("Проверка, что список друзей пуст")
    public FriendsPage checkNoFriend() {
        friendsTable.shouldNotBe(exist);
        noUserLabel.shouldBe(visible);
        return this;
    }

    private SelenideElement getRowInTable(SelenideElement table, String rowName) {
        return table.$x(String.format(".//p[text()='%s']//ancestor::tr", rowName));
    }
}
