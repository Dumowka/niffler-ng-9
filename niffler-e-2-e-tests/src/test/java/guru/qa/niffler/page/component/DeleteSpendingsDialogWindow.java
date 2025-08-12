package guru.qa.niffler.page.component;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class DeleteSpendingsDialogWindow extends BaseComponent<DeleteSpendingsDialogWindow> {
    
    private final By deleteButton = By.xpath(".//button[text()='Delete']");
    private final By cancelButton = By.xpath(".//button[text()='Cancel']");

    public DeleteSpendingsDialogWindow() {
        super($("div[role='dialog']"));
    }

    @Step("Проверка, что окно подтверждения удаления затрат появилось")
    public DeleteSpendingsDialogWindow checkThatWindowIsAppear() {
        getSelf().$("h2").shouldBe(visible).shouldHave(text("Delete spendings?"));
        getSelf().$("#alert-dialog-slide-description")
                .shouldBe(visible)
                .shouldHave(text("If you are sure, submit your action."));
        getSelf().$(deleteButton).shouldBe(visible);
        getSelf().$(cancelButton).shouldBe(visible);
        return this;
    }

    @Step("Проверка, что окно подтверждения удаления затрат исчезло")
    public DeleteSpendingsDialogWindow checkThatWindowIsDissappear() {
        getSelf().$("h2").shouldNotBe(visible);
        return this;
    }

    @Step("Нажатие на кнопку 'Delete'")
    public void clickOnDeleteButton() {
        getSelf().$(deleteButton).click();
        checkThatWindowIsDissappear();
    }

    @Step("Нажатие на кнопку 'Cancel'")
    public void clickOnCancelButton() {
        getSelf().$(cancelButton).click();
        checkThatWindowIsDissappear();
    }
}
