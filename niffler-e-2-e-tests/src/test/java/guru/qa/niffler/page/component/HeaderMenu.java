package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.PeoplePage;
import guru.qa.niffler.page.UserProfilePage;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class HeaderMenu {
    private final SelenideElement self = $("ul[role='menu']");

    @Step("Нажатие на кнопку 'Profile'")
    public UserProfilePage clickOnProfileButton() {
        self.$("a[href='/profile']").shouldBe(visible).click();
        return new UserProfilePage();
    }

    @Step("Нажатие на кнопку 'Friends'")
    public FriendsPage clickOnFriendsButton() {
        self.$("a[href='/people/friends']").shouldBe(visible).click();
        return new FriendsPage();
    }

    @Step("Нажатие на кнопку 'All people'")
    public PeoplePage clickOnAllPeopleButton() {
        self.$("a[href='/people/all']").shouldBe(visible).click();
        return new PeoplePage();
    }

    @Step("Нажатие на кнопку 'Sign out'")
    public LoginPage clickOnSignOutButton() {
        self.$x("//li[text()='Sign out']").shouldBe(visible).click();
        return new LoginPage();
    }
}
