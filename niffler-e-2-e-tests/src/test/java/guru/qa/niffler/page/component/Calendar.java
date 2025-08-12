package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Calendar {
    private final SelenideElement self = $("div.MuiDateCalendar-root");

    private final By yearAndMonthLabel = By.cssSelector("div.MuiPickersCalendarHeader-label");
    private final By previousMonthButton = By.cssSelector("[data-testid='ArrowLeftIcon']");
    private final By nextMonthButton = By.cssSelector("[data-testid='ArrowRightIcon']");
    private final By switchToYearViewButton = By.cssSelector("[data-testid='ArrowDropDownIcon']");

    private final DateTimeFormatter headerFmt =
            DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("en"));

    @Step("Выбор даты в календаре: {date}")
    public void selectDateInCalendar(
            @Nonnull
            Date date) {
        final YearMonth targetYM = YearMonth.of(date.getYear(), date.getMonth());
        selectYear(targetYM.getYear());
        selectMonth(targetYM.getMonthValue());
        selectDay(date.getDate());
    }

    @Step("Выбор года: {year}")
    private void selectYear(int year) {
        YearMonth currentYM = currentYM();
        if (year != currentYM.getYear()) {
            self.$(switchToYearViewButton).shouldBe(visible).click();
            self.$x(String.format(".//button[contains(@class, 'MuiPickersYear-yearButton') and text()='%s']", year))
                    .scrollIntoCenter()
                    .shouldBe(visible)
                    .click();
        }
    }

    @Step("Выбор месяца: {month}")
    private void selectMonth(int month) {
        YearMonth currentYM = currentYM();

        boolean isBefore = month < currentYM.getMonth().getValue();
        int steps = Math.abs(currentYM.getMonth().getValue() - month);

        if (isBefore) {
            changeMonth(steps, previousMonthButton);
        } else {
            changeMonth(steps, nextMonthButton);
        }

        YearMonth expectedYM = YearMonth.of(currentYM.getYear(), month);
        self.$(yearAndMonthLabel).shouldHave(text(expectedYM.format(headerFmt)));
    }

    @Step("Выбор дня: {day}")
    private void selectDay(int day) {
        self.$x(String.format(".//button[contains(@class, 'MuiPickersDay-dayWithMargin') and text()='%s']", day))
                .shouldBe(visible)
                .click();
    }

    private void changeMonth(int steps, By button) {
        for (int i = 0; i < steps; i++) {
            self.$(button).shouldBe(visible).click();
        }
    }

    private YearMonth currentYM() {
        return YearMonth.parse(self.$(yearAndMonthLabel).shouldBe(visible).getText(), headerFmt);
    }
}
