package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class AlertWindow {
    private final SelenideElement self = $("div[role='alert']");
    private final By message = By.cssSelector("div.MuiAlert-message");

    @Step("Проверка появления сообщения об успешном удалении затрат")
    public void spendingsSuccesfullyDeletedIsAppeared() {
        self.$(message).shouldBe(visible).shouldHave(text("Spendings succesfully deleted"));
    }

    @Step("Проверка появления сообщения об успешном отклонении заявки в друзья {name}")
    public void declineFriendshipDialogWindowIsAppeared(String name) {
        self.$(message).shouldBe(visible).shouldHave(text(String.format("Invitation of %s is declined", name)));
    }

    @Step("Проверка появления сообщения об успешном изменении траты")
    public void spendingSuccesfullyEditedIsAppeared() {
        self.$(message).shouldBe(visible).shouldHave(text("Spending is edited successfully"));
    }

    @Step("Проверка появления сообщения об успешном создании траты")
    public void spendingSuccesfullyCreatedIsAppeared() {
        self.$(message).shouldBe(visible).shouldHave(text("New spending is successfully created"));
    }

    @Step("Проверка появления сообщения об успешном изменении данных в профиле")
    public void profileSuccesfullyUpdatedIsAppeared() {
        self.$(message).shouldBe(visible).shouldHave(text("Profile successfully updated"));
    }

    @Step("Проверка, что окно с оповещением исчезло")
    public void alertWindowIsDisappear() {
        self.shouldBe(visible);
    }

    @Step("Закрытие окна с оповещением")
    public void closeAlertWindow() {
        self.$("button[aria-label='Close']").shouldBe(visible).click();
    }
}
