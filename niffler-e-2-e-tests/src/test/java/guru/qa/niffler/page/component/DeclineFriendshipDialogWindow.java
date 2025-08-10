package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class DeclineFriendshipDialogWindow {
    private final SelenideElement self = $("div[role='dialog']");
    private final By declineButton = By.xpath(".//button[text()='Decline']");
    private final By closeButton = By.xpath(".//button[text()='Close']");

    @Step("Проверка, что окно подтверждения отклонения заявки в друзья появилось")
    public DeclineFriendshipDialogWindow checkThatWindowIsAppear() {
        self.$("h2").shouldBe(visible).shouldHave(text("Decline friendship"));
        self.$("#alert-dialog-slide-description")
                .shouldBe(visible)
                .shouldHave(text("Do you really want to decline friendship?"));
        self.$(declineButton).shouldBe(visible);
        self.$(closeButton).shouldBe(visible);
        return this;
    }

    @Step("Проверка, что окно подтверждения отклонения заявки в друзья исчезло")
    public DeclineFriendshipDialogWindow checkThatWindowIsDissappear() {
        self.$("h2").shouldNotBe(visible);
        return this;
    }

    @Step("Нажатие на кнопку 'Decline'")
    public void clickOnDeclineButton() {
        self.$(declineButton).click();
        checkThatWindowIsDissappear();
    }

    @Step("Нажатие на кнопку 'Close'")
    public void clickOnCloseButton() {
        self.$(closeButton).click();
        checkThatWindowIsDissappear();
    }
}
