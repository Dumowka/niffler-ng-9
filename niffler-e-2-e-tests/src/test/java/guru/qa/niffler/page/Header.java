package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class Header {
    private final SelenideElement menuButton = $("button[aria-label='Menu']");
    private final SelenideElement profileLink = $("a[href='/profile']");
    private final SelenideElement friendsLink = $x("//a[@href='/people/friends' and text()='Friends']");

    public Header openMenu() {
        menuButton.click();
        return this;
    }

    public UserProfilePage clickOnProfileButton() {
        profileLink.click();
        return new UserProfilePage();
    }

    public FriendsPage clickOnFriendsButton() {
        friendsLink.click();
        return new FriendsPage();
    }
}
