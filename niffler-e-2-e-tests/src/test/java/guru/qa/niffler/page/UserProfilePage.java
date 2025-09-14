package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static guru.qa.niffler.jupiter.extension.ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE;
import static guru.qa.niffler.utils.PageUtils.getElementScreenshot;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class UserProfilePage extends BasePage<UserProfilePage> {

    public static final String URL = CFG.frontUrl() + "profile";

    private final SelenideElement photo = $("#image__input").parent().$("img");
    private final SelenideElement uploadNewPictureButton = $("label[for='image__input']");
    private final SelenideElement photoInput = $("input[type='file']");
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement nameInput = $("input[name='name']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement showArchivedCheckbox = $("input[type='checkbox']");

    @Getter
    private final Header header = new Header();

    @Override
    @Step("Проверка, что страница 'Profile' загружена")
    public UserProfilePage checkThatPageLoaded() {
        uploadNewPictureButton.shouldBe(Condition.visible);
        usernameInput.shouldBe(Condition.visible);
        nameInput.shouldBe(Condition.visible);
        submitButton.shouldBe(Condition.visible);
        showArchivedCheckbox.parent().shouldBe(Condition.visible);
        getCategoryInput("").shouldBe(Condition.visible);
        return this;
    }

    @Step("Нажатие кнопки загрузки нового изображения профиля")
    public UserProfilePage clickUploadNewPictureButton() {
        uploadNewPictureButton.click();
        return this;
    }

    @Step("Загрузка фото из classpath")
    public UserProfilePage uploadPhotoFromClasspath(String path) {
        photoInput.uploadFromClasspath(path);
        return this;
    }

    @Step("Проверка имени пользователя: {username}")
    public UserProfilePage checkUsername(String username) {
        usernameInput.shouldHave(value(username));
        return this;
    }

    @Step("Установка имени: {name}")
    public UserProfilePage setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Step("Проверка имени: {name}")
    public UserProfilePage checkName(String name) {
        nameInput.shouldHave(value(name));
        return this;
    }

    @Step("Нажатие на кнопку 'Save changes'")
    public UserProfilePage clickOnSubmitButton() {
        submitButton.click();
        checkAlert("Profile successfully updated");
        return this;
    }

    @Step("Изменение состояния чекбокса 'Show archived' на: {shouldBeShowed}")
    public UserProfilePage clickOnShowArchivedCheckbox(boolean shouldBeShowed) {
        if (showArchivedCheckbox.isSelected() != shouldBeShowed) {
            showArchivedCheckbox.click();
        }
        return this;
    }

    @Step("Добавление новой категории")
    public UserProfilePage addNewCategory() {
        getCategoryInput("").setValue(RandomStringUtils.randomAlphanumeric(10)).pressEnter();
        return this;
    }

    @Step("Нажатие кнопки редактирования категории: {categoryName}")
    public UserProfilePage clickOnEditCategoryNameButton(String categoryName) {
        getCategoryButton(categoryName).parent()
                .find(By.xpath("//button[@aria-label='Edit category']")).click();
        return this;
    }

    @Step("Нажатие кнопки архивации категории: {categoryName}")
    public UserProfilePage clickOnArchiveCategoryNameButton(String categoryName) {
        getCategoryButton(categoryName).parent()
                .find(By.xpath("//button[@aria-label='Archive category']")).click();
        return this;
    }

    @Step("Редактирование категории '{categoryName}' на новое имя '{newCategoryName}'")
    public UserProfilePage editCategoryName(String categoryName, String newCategoryName) {
        getCategoryInput(categoryName).setValue(newCategoryName).pressEnter();
        return this;
    }

    @Step("Закрытие режима редактирования категории: {categoryName}")
    public UserProfilePage closeEditCategoryName(String categoryName) {
        getCategoryInput(categoryName).parent()
                .find(By.xpath("//button[@aria-label='close']")).click();
        return this;
    }

    @Step("Проверка, что категория не существует: {categoryName}")
    public UserProfilePage checkThatCategoryNonExist(String categoryName) {
        getCategoryButton(categoryName).shouldNotBe(Condition.visible);
        return this;
    }

    @Step("Проверка, что категория существует: {categoryName}")
    public UserProfilePage checkThatCategoryExist(String categoryName) {
        getCategoryButton(categoryName).shouldBe(Condition.visible);
        return this;
    }

    @Step("Проверка, что фото соответствует ожидаемому")
    public UserProfilePage checkPhoto(BufferedImage expected) throws IOException {
        Selenide.sleep(1000);
        BufferedImage actual = getElementScreenshot(photo);
        assertFalse(
                new ScreenDiffResult(
                        expected,
                        actual
                ),
                ASSERT_SCREEN_MESSAGE
        );
        return this;
    }

    private SelenideElement getCategoryInput(String categoryName) {
        return $x(String.format("//input[@name='category' and @value='%s']", categoryName));
    }

    private SelenideElement getCategoryButton(String categoryName) {
        return $x(String.format("//span[text() ='%s']", categoryName));
    }
}

