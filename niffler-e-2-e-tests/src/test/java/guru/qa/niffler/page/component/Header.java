package guru.qa.niffler.page.component;

import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.PeoplePage;
import guru.qa.niffler.page.UserProfilePage;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Header extends BaseComponent<Header> {
    
    private final HeaderMenu headerMenu = new HeaderMenu();

    public Header() {
        super($("#root header"));
    }

    @Step("Проверка заголовка на наличие текста 'Niffler'")
    public Header checkHeaderText() {
        getSelf().$("h1.MuiTypography-root").shouldBe(visible).shouldHave(text("Niffler"));
        return this;
    }

    @Step("Раскрытие меню пользователя")
    public Header openMenu() {
        getSelf().shouldBe(visible).$("[data-testid='PersonIcon']").click();
        return this;
    }

    @Step("Переход на страницу 'Profile'")
    public UserProfilePage toProfilePage() {
        openMenu();
        return headerMenu.clickOnProfileButton();
    }

    @Step("Переход на страницу 'Friends'")
    public FriendsPage toFriendsPage() {
        openMenu();
        return headerMenu.clickOnFriendsButton();
    }

    @Step("Переход на страницу 'All people'")
    public PeoplePage toAllPeoplePage() {
        openMenu();
        return headerMenu.clickOnAllPeopleButton();
    }

    @Step("Выход из системы")
    public LoginPage signOut() {
        openMenu();
        return headerMenu.clickOnSignOutButton();
    }

    @Step("Переход на страницу 'Add new spending'")
    public EditSpendingPage addSpendingPage() {
        getSelf().$("a[href='/spending']").shouldBe(visible).click();
        return new EditSpendingPage();
    }

    @Step("Переход на главную страницу")
    public MainPage toMainPage() {
        getSelf().$("[alt='Niffler logo']").shouldBe(visible).click();
        return new MainPage();
    }
}
