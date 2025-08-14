package guru.qa.niffler.page.component;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class DeclineFriendshipDialogWindow extends BaseComponent<DeclineFriendshipDialogWindow> {
    
    private final By declineButton = By.xpath(".//button[text()='Decline']");
    private final By closeButton = By.xpath(".//button[text()='Close']");

    public DeclineFriendshipDialogWindow() {
        super($("div[role='dialog']"));
    }

    @Step("Проверка, что окно подтверждения отклонения заявки в друзья появилось")
    public DeclineFriendshipDialogWindow checkThatWindowIsAppear() {
        getSelf().$("h2").shouldBe(visible).shouldHave(text("Decline friendship"));
        getSelf().$("#alert-dialog-slide-description")
                .shouldBe(visible)
                .shouldHave(text("Do you really want to decline friendship?"));
        getSelf().$(declineButton).shouldBe(visible);
        getSelf().$(closeButton).shouldBe(visible);
        return this;
    }

    @Step("Проверка, что окно подтверждения отклонения заявки в друзья исчезло")
    public DeclineFriendshipDialogWindow checkThatWindowIsDissappear() {
        getSelf().$("h2").shouldNotBe(visible);
        return this;
    }

    @Step("Нажатие на кнопку 'Decline'")
    public void clickOnDeclineButton() {
        getSelf().$(declineButton).click();
        checkThatWindowIsDissappear();
    }

    @Step("Нажатие на кнопку 'Close'")
    public void clickOnCloseButton() {
        getSelf().$(closeButton).click();
        checkThatWindowIsDissappear();
    }
}
