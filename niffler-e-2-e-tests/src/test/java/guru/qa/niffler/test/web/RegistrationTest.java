package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.SelenideUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.model.ErrorMessages.PASSWORDS_SHOULD_BE_EQUAL;

@WebTest
public class RegistrationTest {

    private static final Config CFG = Config.getInstance();

    private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);
    private String username;
    private String password;
    

    @BeforeEach
    public void setup() {
        username = RandomDataUtils.randomUsername();
        password = RandomStringUtils.randomAlphanumeric(10);
    }

    @Test
    void shouldOpenLoginPageAfterClickOn() {
        driver.open(CFG.frontUrl(), LoginPage.class)
                .goToRegisterPage()
                .clickOnAlreadyHaveAnAccountLoginLink()
                .checkThatPageLoaded();
    }

    @Test
    void shouldRegisterNewUser() {
        driver.open(CFG.frontUrl(), LoginPage.class)
                .goToRegisterPage()
                .checkThatPageLoaded()
                .fillRegisterPage(username, password, password)
                .submitRegistration()
                .checkThatRegistrationIsSuccessful()
                .goToLoginPageAfterRegistration()
                .checkThatPageLoaded()
                .fillLoginPage(username, password)
                .submit()
                .checkThatPageLoaded();
    }

    @Test
    @User
    void shouldNotRegisterUserWithExistingUsername(UserJson user) {
        driver.open(CFG.frontUrl(), LoginPage.class)
                .goToRegisterPage()
                .fillRegisterPage(user.username(), user.testData().password(), user.testData().password())
                .submitRegistration()
                .checkErrorText(String.format("Username `%s` already exists", user.username()));
    }

    @Test
    void passwordFieldShouldBeRevealedAfterRevealButtonsClicked() {
        driver.open(CFG.frontUrl(), LoginPage.class)
                .goToRegisterPage()
                .fillRegisterPage(username, password, password)
                .checkThatPasswordIsHidden()
                .revealPassword()
                .checkThatPasswordIsRevealed()
                .checkThatConfirmPasswordIsHidden()
                .revealConfirmPassword()
                .checkThatConfirmPasswordIsRevealed();
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        driver.open(CFG.frontUrl(), LoginPage.class)
                .goToRegisterPage()
                .fillRegisterPage(username, password, password + "_invalid")
                .submitRegistration()
                .checkErrorText(PASSWORDS_SHOULD_BE_EQUAL.getText());
    }
}
