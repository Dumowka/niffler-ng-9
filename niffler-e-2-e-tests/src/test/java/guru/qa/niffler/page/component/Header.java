package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.UserProfilePage;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class Header {
    private final SelenideElement self = $("#root header");
    private final SelenideElement menuButton = $("button[aria-label='Menu']");
    private final SelenideElement profileLink = $("a[href='/profile']");
    private final SelenideElement friendsLink = $x("//a[@href='/people/friends' and text()='Friends']");

    public Header openMenu() {
        self.$("button[aria-controls='account-menu']").click();
        return this;
    }

    public UserProfilePage clickOnProfileButton() {
        self.$("a[href='/profile']").click();
        return new UserProfilePage();
    }

    public FriendsPage clickOnFriendsButton() {
        self.$("a[href='/people/friends']").click();
        return new FriendsPage();
    }
}
