package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static guru.qa.niffler.model.ErrorMessages.BAD_CREDENTIALS;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {
  private final SelenideElement usernameInput;
  private final SelenideElement passwordInput;
  private final SelenideElement submitButton;
  private final SelenideElement createAccountButton;
  private final SelenideElement errorText;

  public LoginPage(SelenideDriver driver) {
    super(driver);
    this.usernameInput = driver.$("input[name='username']");
    this.passwordInput = driver.$("input[name='password']");
    this.submitButton = driver.$("button[type='submit']");
    this.createAccountButton = driver.$("#register-button");
    this.errorText = driver.$("p[class='form__error']");
  }

  public LoginPage() {
    this.usernameInput = Selenide.$("input[name='username']");
    this.passwordInput = Selenide.$("input[name='password']");
    this.submitButton = Selenide.$("button[type='submit']");
    this.createAccountButton = Selenide.$("#register-button");
    this.errorText = Selenide.$("p[class='form__error']");
  }

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

