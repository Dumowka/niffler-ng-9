package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class FriendsPage {
    private final SelenideElement searchPeopleInput = $("input[aria-label='search']");
    private final SelenideElement friendsTableShowButton = $x("//h2[text()='Friends']");
    private final SelenideElement allPeopleTableShowButton = $x("//h2[text()='All people']");
    private final SelenideElement requestsToFriendTable = $("#requests");
    private final SelenideElement friendsTable = $("#friends");
    private final SelenideElement allPeopleTable = $("#all");
    private final SelenideElement noUserLabel = $x("//p[text()='There are no users yet']");

    public FriendsPage searchPeople(String name) {
        searchPeopleInput.shouldBe(visible).setValue(name).pressEnter();
        return this;
    }

    public FriendsPage clickOnFriendsTable() {
        friendsTableShowButton.click();
        return this;
    }

    public FriendsPage clickOnAllPeopleTable() {
        allPeopleTableShowButton.click();
        return this;
    }

    public FriendsPage checkRowInAllPeopleTable(String rowName) {
        getRowInTable(allPeopleTable, rowName).shouldBe(visible)
                .$x(".//button[text()='Add friend']").shouldBe(visible);
        return this;
    }

    public FriendsPage checkOutcomeInvitation(String name) {
        getRowInTable(allPeopleTable, name).shouldBe(visible)
                .$x(".//span[text()='Waiting...']").shouldBe(visible);
        return this;
    }

    public FriendsPage checkFriends(String name) {
        getRowInTable(friendsTable, name).shouldBe(visible)
                .$x(".//button[text()='Unfriend']").shouldBe(visible);
        return this;
    }

    public FriendsPage checkIncomeInvitation(String name) {
        SelenideElement row = getRowInTable(requestsToFriendTable, name).shouldBe(visible);
        row.$x(".//button[text()='Accept']").shouldBe(visible);
        row.$x(".//button[text()='Decline']").shouldBe(visible);
        return this;
    }

    public FriendsPage checkNoFriend() {
        friendsTable.shouldNotBe(exist);
        noUserLabel.shouldBe(visible);
        return this;
    }

    private SelenideElement getRowInTable(SelenideElement table, String rowName) {
        return table.$x(String.format(".//p[text()='%s']//ancestor::tr", rowName));
    }
}
