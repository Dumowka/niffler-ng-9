package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.model.ErrorMessages.BAD_CREDENTIALS;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {

  public static final String URL = CFG.authUrl() + "login";

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitButton = $("button[type='submit']");
  private final SelenideElement createAccountButton = $("#register-button");
  private final SelenideElement errorText = $("p[class='form__error']");

  @Step("Заполнение формы логина: username = {username}, password = ******")
  public LoginPage fillLoginPage(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    return this;
  }

  @Step("Отправка формы логина")
  public MainPage submit() {
    submitButton.click();
    return new MainPage();
  }

  @Step("Переход на страницу регистрации")
  public RegisterPage goToRegisterPage() {
    createAccountButton.click();
    return new RegisterPage();
  }

  @Override
  @Step("Проверка, что страница логина загружена")
  public LoginPage checkThatPageLoaded() {
    usernameInput.shouldBe(visible);
    passwordInput.shouldBe(visible);
    submitButton.shouldBe(visible);
    createAccountButton.shouldBe(visible);
    return this;
  }

  @Step("Проверка ошибки при входе с некорректными данными")
  public LoginPage checkErrorAfterSubmitWithBadCredentials() {
    submitButton.click();
    errorText.shouldHave(text(BAD_CREDENTIALS.getText()));
    return this;
  }
}

