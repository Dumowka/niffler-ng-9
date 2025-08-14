package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.page.component.Calendar;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {
  private final SelenideElement amountInput = $("#amount");
  private final SelenideElement currencyInput = $("#currency");
  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement categoryInput = $("#category");
  private final SelenideElement submitButton = $("#save");
  private final SelenideElement openCalendarButton = $x("//button[contains(@aria-label, 'Choose date')]");

  private final String currencyItemCss = "li[data-value='%s']";

  private final Calendar calendar = new Calendar();

  @Override
  @Step("Проверка, что страница 'Edit spending' загружена")
  public EditSpendingPage checkThatPageLoaded() {
    amountInput.shouldBe(visible);
    currencyInput.shouldBe(visible);
    openCalendarButton.shouldBe(visible);
    descriptionInput.shouldBe(visible);
    categoryInput.shouldBe(visible);
    submitButton.shouldBe(visible);
    return this;
  }

  @Step("Изменение описания траты на: {description}")
  public EditSpendingPage setDescription(String description) {
    descriptionInput.setValue(description);
    return this;
  }

  @Step("Изменение суммы затрат на: {amount}")
  public EditSpendingPage setAmount(int amount) {
    amountInput.setValue(String.valueOf(amount));
    return this;
  }

  @Step("Изменение валюты затрат на: {currency}")
  public EditSpendingPage setCurrency(CurrencyValues currency) {
    currencyInput.shouldBe(visible).click();
    $(String.format(currencyItemCss, currency.name())).shouldBe(visible).click();
    return this;
  }

  @Step("Изменение категории трат на: {categoryName}")
  public EditSpendingPage setCategory(String categoryName) {
    categoryInput.shouldBe(visible).setValue(categoryName);
    return this;
  }

  @Step("Открытие календаря выбора даты")
  public EditSpendingPage openCalendar() {
    openCalendarButton.shouldBe(visible).click();
    return this;
  }

  @Step("Выбор даты через календарь: {date}")
  public EditSpendingPage selectDateInCalendar(@Nonnull Date date) {
    openCalendar();
    calendar.selectDateInCalendar(date);
    return this;
  }

  @Step("Сохранение изменений по трате")
  public MainPage saveEditedSpending() {
    submitButton.click();
    checkAlert("Spending is edited successfully");
    return new MainPage();
  }

  @Step("Сохранение созданной траты")
  public MainPage saveCreatedSpending() {
    submitButton.click();
    checkAlert("New spending is successfully created");
    return new MainPage();
  }
}
