package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
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

    @User
    @Test
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
    @ApiLogin
    @Test
    void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
        final CategoryJson category = user.testData().categories().getFirst();

        Selenide.open(UserProfilePage.URL, UserProfilePage.class)
                .checkThatCategoryNonExist(category.name())
                .clickOnShowArchivedCheckbox(true)
                .checkThatCategoryExist(category.name());
    }

    @User(
            categories = @Category()
    )
    @ApiLogin
    @Test
    void activeCategoryShouldPresentInCategoriesList(UserJson user) {
        final CategoryJson category = user.testData().categories().getFirst();
        Selenide.open(UserProfilePage.URL, UserProfilePage.class)
                .checkThatCategoryExist(category.name());
    }

    @User
    @ApiLogin
    @Test
    void editProfile() {
        String newName = RandomDataUtils.randomName();

        Selenide.open(UserProfilePage.URL, UserProfilePage.class)
                .setName(newName)
                .clickOnSubmitButton()
                .checkName(newName)
                .getHeader().toMainPage()
                .getHeader().toProfilePage()
                .checkName(newName);
    }

    @User
    @ApiLogin
    @ScreenShotTest(value = "img/expected-avatar.png")
    void shouldUpdateProfilePhoto(UserJson user, BufferedImage expectedAvatar) throws IOException {
        UserProfilePage profilePage = Selenide.open(UserProfilePage.URL, UserProfilePage.class)
                .uploadPhotoFromClasspath("img/cat.jpeg")
                .clickOnSubmitButton();

        Selenide.refresh();

        profilePage.checkPhoto(expectedAvatar);
    }
}
