package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class RegisterPage extends BasePage<RegisterPage> {

    public static final String URL = CFG.authUrl() + "register";

    private final SelenideElement alreadyHaveAnAccountLoginLink= $("a[href='/login']");
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement passwordButton = $("#passwordBtn");
    private final SelenideElement confirmPasswordInput = $("input[name='passwordSubmit']");
    private final SelenideElement confirmPasswordButton = $("#passwordSubmitBtn");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement successRegistrationText = $(byText("Congratulations! You've registered!"));
    private final SelenideElement loginButton = $x("//a[text()='Sign in']");
    private final SelenideElement errorText = $("span[class='form__error']");

    @Step("Переход по ссылке 'Already have an account? Log in!'")
    public LoginPage clickOnAlreadyHaveAnAccountLoginLink() {
        alreadyHaveAnAccountLoginLink.click();
        return new LoginPage();
    }

    @Override
    @Step("Проверка, что страница регистрации загружена")
    public RegisterPage checkThatPageLoaded() {
        alreadyHaveAnAccountLoginLink.shouldBe(visible);
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        passwordButton.shouldBe(visible);
        confirmPasswordInput.shouldBe(visible);
        confirmPasswordButton.shouldBe(visible);
        submitButton.shouldBe(visible);
        return this;
    }

    @Step("Заполнение формы регистрации: username = {username}, password и подтверждение пароля")
    public RegisterPage fillRegisterPage(String username, String password, String confirmPassword) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        confirmPasswordInput.setValue(confirmPassword);
        return this;
    }

    @Step("Раскрыть пароль")
    public RegisterPage revealPassword() {
        passwordButton.click();
        return this;
    }

    @Step("Раскрыть подтверждение пароля")
    public RegisterPage revealConfirmPassword() {
        confirmPasswordButton.click();
        return this;
    }

    @Step("Проверка, что пароль скрыт")
    public RegisterPage checkThatPasswordIsHidden() {
        passwordInput.shouldHave(attribute("type", "password"));
        return this;
    }

    @Step("Проверка, что пароль раскрыт")
    public RegisterPage checkThatPasswordIsRevealed() {
        passwordInput.shouldHave(attribute("type", "text"));
        return this;
    }

    @Step("Проверка, что подтверждение пароля скрыто")
    public RegisterPage checkThatConfirmPasswordIsHidden() {
        confirmPasswordInput.shouldHave(attribute("type", "password"));
        return this;
    }

    @Step("Проверка, что подтверждение пароля раскрыт")
    public RegisterPage checkThatConfirmPasswordIsRevealed() {
        confirmPasswordInput.shouldHave(attribute("type", "text"));
        return this;
    }

    @Step("Отправка формы регистрации")
    public RegisterPage submitRegistration() {
        submitButton.click();
        return this;
    }

    @Step("Проверка, что регистрация прошла успешно")
    public RegisterPage checkThatRegistrationIsSuccessful() {
        successRegistrationText.shouldBe(visible);
        return this;
    }

    @Step("Переход на страницу логина после регистрации")
    public LoginPage goToLoginPageAfterRegistration() {
        loginButton.click();
        return new LoginPage();
    }

    @Step("Проверка текста ошибки: {errorText}")
    public RegisterPage checkErrorText(String errorText) {
        this.errorText.shouldBe(text(errorText));
        return this;
    }
}
