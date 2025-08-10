package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;

@WebTest
public class SpendingTest {

    private static final Config CFG = Config.getInstance();

    @User(
            spendings = @Spending(
                    amount = 89990.00,
                    description = "Advanced 9 поток!",
                    category = "Обучение"
            )
    )
    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
        SpendJson spend = user.testData().spendings().getFirst();
        final String newDescription = ":)";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded()
                .findSpending(spend.description())
                .editSpending(spend.description())
                .setNewSpendingDescription(newDescription)
                .saveEditedSpending()
                .checkThatTableContainsSpending(newDescription);
    }

    @User
    @Test
    void addNewSpending(UserJson user) {
        String newDescription = RandomDataUtils.randomSentence(2);

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded()
                .getHeader().addSpendingPage()
                .setNewSpendingAmount(1000)
                .setNewSpendingCurrency(CurrencyValues.KZT)
                .setNewSpendingCategory(RandomDataUtils.randomCategoryName())
                .selectDateInCalendar(new Date(2024, 11, 15))
                .setNewSpendingDescription(newDescription)
                .saveCreatedSpending()
                .checkThatTableContainsSpending(newDescription);
    }
}
