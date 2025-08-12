package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class DeleteSpendingsDialogWindow {
    private final SelenideElement self = $("div[role='dialog']");
    private final By deleteButton = By.xpath(".//button[text()='Delete']");
    private final By cancelButton = By.xpath(".//button[text()='Cancel']");

    @Step("Проверка, что окно подтверждения удаления затрат появилось")
    public DeleteSpendingsDialogWindow checkThatWindowIsAppear() {
        self.$("h2").shouldBe(visible).shouldHave(text("Delete spendings?"));
        self.$("#alert-dialog-slide-description")
                .shouldBe(visible)
                .shouldHave(text("If you are sure, submit your action."));
        self.$(deleteButton).shouldBe(visible);
        self.$(cancelButton).shouldBe(visible);
        return this;
    }

    @Step("Проверка, что окно подтверждения удаления затрат исчезло")
    public DeleteSpendingsDialogWindow checkThatWindowIsDissappear() {
        self.$("h2").shouldNotBe(visible);
        return this;
    }

    @Step("Нажатие на кнопку 'Delete'")
    public void clickOnDeleteButton() {
        self.$(deleteButton).click();
        checkThatWindowIsDissappear();
    }

    @Step("Нажатие на кнопку 'Cancel'")
    public void clickOnCancelButton() {
        self.$(cancelButton).click();
        checkThatWindowIsDissappear();
    }
}
