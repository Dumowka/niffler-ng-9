package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
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
                .setDescription(newDescription)
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
                .setAmount(1000)
                .setCurrency(CurrencyValues.KZT)
                .setCategory(RandomDataUtils.randomCategoryName())
                .selectDateInCalendar(new Date(2024, 11, 15))
                .setDescription(newDescription)
                .saveCreatedSpending()
                .checkThatTableContainsSpending(newDescription);
    }

    @User(
            spendings = @Spending(
                    amount = 79990.00,
                    description = "Advanced 9 поток!",
                    category = "Обучение"
            )
    )
    @ScreenShotTest(value = "img/expected-stat.png")
    void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .checkThatPageLoaded()
                .getStatComponent()
                .checkStatisticsImage(expected);
    }

    @User(
            categories = {
                    @Category(name = "Поездки"),
                    @Category(name = "Ремонт", archived = true),
                    @Category(name = "Страховка", archived = true)
            },
            spendings = {
                    @Spending(
                            category = "Поездки",
                            description = "В Москву",
                            amount = 9500
                    ),
                    @Spending(
                            category = "Ремонт",
                            description = "Цемент",
                            amount = 100
                    ),
                    @Spending(
                            category = "Страховка",
                            description = "ОСАГО",
                            amount = 3000
                    )
            }
    )
    @ScreenShotTest(value = "img/expected-stat-archived.png")
    void statComponentShouldDisplayArchivedCategories(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .fillLoginPage(user.username(), user.testData().password())
                .submit()
                .getStatComponent()
                .checkStatisticBubblesContains("Поездки 9500 ₽", "Archived 3100 ₽")
                .checkStatisticsImage(expected)
                .checkBubblesColor(Color.YELLOW);
    }
}
