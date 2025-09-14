package guru.qa.niffler.model.ui;

import guru.qa.niffler.model.spend.SpendJson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public record SpendUi(
        String categoryName,
        String amount,
        String description,
        String date
) {
    public static SpendUi fromSpendJson(SpendJson spendJson) {
        String amount = spendJson.amount() % 10 == 0
                ? String.valueOf((int) spendJson.amount().doubleValue())
                : spendJson.amount().toString();

        LocalDate date = LocalDate.parse(spendJson.spendDate().toString());
        DateTimeFormatter out = DateTimeFormatter.ofPattern("MMM dd, uuuu", Locale.ENGLISH);
        String formatted = date.format(out);

        return new SpendUi(
                spendJson.category().name(),
                amount + " " + spendJson.currency().getUiValue(),
                spendJson.description(),
                formatted
        );
    }
}
