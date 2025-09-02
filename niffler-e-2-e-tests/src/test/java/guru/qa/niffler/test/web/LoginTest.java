package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static com.codeborne.selenide.Condition.text;

public class LoginTest {

    private static final Config CFG = Config.getInstance();

    @RegisterExtension
    private final BrowserExtension browserExtension = new BrowserExtension();
    private final SelenideDriver chrome = new SelenideDriver(SelenideUtils.chromeConfig);

    @Test
    @User
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
        browserExtension.drivers().add(chrome);

        chrome.open(CFG.frontUrl());
        new LoginPage(chrome)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded();
    }

    @Test
    @User
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials(UserJson user) {
        SelenideDriver firefox = new SelenideDriver(SelenideUtils.firefoxConfig);
        browserExtension.drivers().addAll(List.of(chrome, firefox));
        chrome.open(CFG.frontUrl());
        firefox.open(CFG.frontUrl());
        new LoginPage(chrome)
                .fillLoginPage(user.username(), user.username())
                .checkErrorAfterSubmitWithBadCredentials();

        firefox.$(".logo-section__text").shouldBe(text("Niffler!"));
    }
}
