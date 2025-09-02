package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.ui.Browser;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

public class LoginTest {

    private static final Config CFG = Config.getInstance();

    @RegisterExtension
    private static final BrowserExtension browserExtension = new BrowserExtension();
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

    @User
    @ParameterizedTest
    @EnumSource(Browser.class)
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials(@ConvertWith(Browser.BrowserConverter.class) SelenideDriver driver, UserJson user) {
        browserExtension.drivers().add(driver);
        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .fillLoginPage(user.username(), user.username())
                .checkErrorAfterSubmitWithBadCredentials();
    }
}
