package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.CalculateRequest;
import guru.qa.niffler.grpc.CalculateResponse;
import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.grpc.CurrencyResponse;
import guru.qa.niffler.grpc.CurrencyValues;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CurrencyGrpcTest extends BaseGrpcTest {

    Map<CurrencyValues, Double> currencies = Map.of(
            CurrencyValues.RUB, 	0.015,
            CurrencyValues.KZT,	0.0021,
            CurrencyValues.EUR,	1.08,
            CurrencyValues.USD, 	1.0
    );

    @Test
    void allCurrenciesShouldReturned() {
        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
        final List<Currency> allCurrencies = response.getAllCurrenciesList();

        assertTrue(allCurrencies.stream().map(Currency::getCurrency).toList().containsAll(
                currencies.keySet()
        ));
        assertTrue(allCurrencies.stream().map(Currency::getCurrencyRate).toList().containsAll(
                currencies.values()
        ));
        assertEquals(currencies.size(), allCurrencies.size());
    }

    @Test
    void calculateRateWithOnlyUsd() {
        final double amount = 5.0;

        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.USD)
                .setDesiredCurrency(CurrencyValues.USD)
                .setAmount(amount)
                .build();
        CalculateResponse response = blockingStub.calculateRate(request);
        assertEquals(amount, response.getCalculatedAmount());
    }

    @Test
    void calculateRateWithOnlyNotUsd() {
        final double amount = 5.0;

        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(CurrencyValues.RUB)
                .setAmount(amount)
                .build();
        CalculateResponse response = blockingStub.calculateRate(request);
        assertEquals(amount, response.getCalculatedAmount());
    }

    @Test
    void calculateRateFromRubToUsd() {
        final double amount = 5.0;
        final double expectedCurrencyRate = amount * currencies.get(CurrencyValues.RUB);

        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(CurrencyValues.USD)
                .setAmount(amount)
                .build();
        CalculateResponse response = blockingStub.calculateRate(request);
        assertEquals(expectedCurrencyRate, response.getCalculatedAmount(), 0.01);
    }

    @Test
    void calculateRateFromRubToEur() {
        final double amount = 5.0;
        final double expectedCurrencyRate = amount * currencies.get(CurrencyValues.RUB) / currencies.get(CurrencyValues.EUR);

        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(CurrencyValues.EUR)
                .setAmount(amount)
                .build();
        CalculateResponse response = blockingStub.calculateRate(request);
        assertEquals(expectedCurrencyRate, response.getCalculatedAmount(), 0.01);
    }

    @Test
    void calculateRateWithZeroAmount() {
        final double amount = 0.0;

        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(CurrencyValues.EUR)
                .setAmount(amount)
                .build();
        CalculateResponse response = blockingStub.calculateRate(request);
        assertEquals(amount, response.getCalculatedAmount(), 0.01);
    }

    @Test
    void calculateRateWithSpendCurrencyIsUnspecifiedCurrencyValue() {
        final double amount = 0.0;

        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.UNSPECIFIED)
                .setDesiredCurrency(CurrencyValues.EUR)
                .setAmount(amount)
                .build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.calculateRate(request));
    }

    @Test
    void calculateRateWithDesiredCurrencyIsUnspecifiedCurrencyValue() {
        final double amount = 0.0;

        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(CurrencyValues.UNSPECIFIED)
                .setAmount(amount)
                .build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.calculateRate(request));
    }

    @Test
    void calculateRateWithoutSpendCurrency() {
        final double amount = 0.0;

        CalculateRequest request = CalculateRequest.newBuilder()
                .setDesiredCurrency(CurrencyValues.EUR)
                .setAmount(amount)
                .build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.calculateRate(request));
    }

    @Test
    void calculateRateWithoutDesiredCurrency() {
        final double amount = 0.0;

        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.RUB)
                .setAmount(amount)
                .build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.calculateRate(request));
    }

    @Test
    void calculateRateWithoutAmount() {
        CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(CurrencyValues.RUB)
                .setDesiredCurrency(CurrencyValues.UNSPECIFIED)
                .build();

        assertThrows(StatusRuntimeException.class, () -> blockingStub.calculateRate(request));
    }
}
