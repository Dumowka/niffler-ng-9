package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.UserProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
public class UserProfileTest {

    private static final Config CFG = Config.getInstance();

    @Test
    @User
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .getHeader().toProfilePage()
                .checkThatPageLoaded();
    }

    @User(
            categories = @Category(
                    archived = true
            )
    )
    @Test
    void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
        final CategoryJson category = user.testData().categories().getFirst();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .getHeader().toProfilePage()
                .checkThatCategoryNonExist(category.name())
                .clickOnShowArchivedCheckbox(true)
                .checkThatCategoryExist(category.name());
    }

    @User(
            categories = @Category()
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(UserJson user) {
        final CategoryJson category = user.testData().categories().getFirst();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .getHeader().toProfilePage()
                .checkThatCategoryExist(category.name());
    }

    @User
    @Test
    void editProfile(UserJson user) {
        String newName = RandomDataUtils.randomName();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .getHeader().toProfilePage()
                .setName(newName)
                .clickOnSubmitButton()
                .checkName(newName)
                .getHeader().toMainPage()
                .getHeader().toProfilePage()
                .checkName(newName);
    }

    @User
    @ScreenShotTest(value = "img/expected-avatar.png")
    void shouldUpdateProfilePhoto(UserJson user, BufferedImage expectedAvatar) throws IOException {
        UserProfilePage profilePage = Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded()
                .getHeader().toProfilePage()
                .uploadPhotoFromClasspath("img/cat.jpeg")
                .clickOnSubmitButton();

        Selenide.refresh();

        profilePage.checkPhoto(expectedAvatar);
    }
}
