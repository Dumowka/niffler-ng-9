package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class LoginTest {

  private static final Config CFG = Config.getInstance();

  @Test
  @User
  void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit()
        .checkThatPageLoaded();
  }

  @Test
  @User
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .fillLoginPage(user.username(), user.username())
            .checkErrorAfterSubmitWithBadCredentials();
  }
}
