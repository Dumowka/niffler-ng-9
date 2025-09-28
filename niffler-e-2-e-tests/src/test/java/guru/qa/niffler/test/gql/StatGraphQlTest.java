package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.type.FilterPeriod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

public class StatGraphQlTest extends BaseGraphQlTest {

    @User
    @Test
    @ApiLogin
    void statTest(@Token String bearerToken) {
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        System.out.println(bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        Assertions.assertEquals(
                0.0,
                result.total
        );
    }

    @User(
            categories = {
                    @Category(
                            name = "Обучение"
                    ),
                    @Category(
                            name = "Такси",
                            archived = true
                    )
            },
            spendings = {
                    @Spending(
                            amount = 79990.00,
                            description = "Advanced 9 поток!",
                            category = "Обучение"
                    ),
                    @Spending(
                            amount = 500,
                            description = "Test 2",
                            category = "Такси"
                    ),

            }
    )
    @Test
    @ApiLogin
    void checkStatWithArchivedCurrency(@Token String bearerToken, UserJson user) {
        Optional<Double> amount = user.testData().spendings().stream()
                .map(spending -> spending.amount()).reduce(Double::sum);
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        Assertions.assertEquals(
                amount.get(),
                result.total
        );
    }

    @User(
            categories = {
                    @Category(
                            name = "Обучение"
                    ),
                    @Category(
                            name = "Такси",
                            archived = true
                    ),
                    @Category(
                            name = "Рестораны"
                    )
            },
            spendings = {
                    @Spending(
                            amount = 79990.00,
                            description = "Advanced 9 поток!",
                            category = "Обучение"
                    ),
                    @Spending(
                            amount = 799.00,
                            currency = CurrencyValues.USD,
                            description = "Test",
                            category = "Рестораны"
                    ),
                    @Spending(
                            amount = 500,
                            description = "Test 2",
                            category = "Такси"
                    ),

            }
    )
    @Test
    @ApiLogin
    void checkStatByFilterCurrency(@Token String bearerToken, UserJson userJson) {
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(guru.qa.type.CurrencyValues.USD)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        Assertions.assertEquals(
                53266.67,
                result.total
        );
    }

    @User(
            categories = {
                    @Category(
                            name = "Обучение"
                    ),
                    @Category(
                            name = "Такси",
                            archived = true
                    ),
                    @Category(
                            name = "Рестораны"
                    )
            },
            spendings = {
                    @Spending(
                            amount = 79990.00,
                            description = "Advanced 9 поток!",
                            category = "Обучение"
                    ),
                    @Spending(
                            amount = 799.00,
                            currency = CurrencyValues.USD,
                            description = "Test",
                            category = "Рестораны"
                    ),
                    @Spending(
                            amount = 500,
                            description = "Test 2",
                            category = "Такси"
                    ),

            }
    )
    @Test
    @ApiLogin
    void checkStatByCurrency(@Token String bearerToken, UserJson userJson) {
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(guru.qa.type.CurrencyValues.USD)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        Assertions.assertEquals(
                2006.35,
                result.total
        );
    }

    @User(
            categories = {
                    @Category(
                            name = "Обучение"
                    ),
                    @Category(
                            name = "Такси",
                            archived = true
                    ),
                    @Category(
                            name = "Рестораны"
                    )
            },
            spendings = {
                    @Spending(
                            amount = 79990.00,
                            description = "Advanced 9 поток!",
                            category = "Обучение"
                    ),
                    @Spending(
                            amount = 799.00,
                            currency = CurrencyValues.USD,
                            description = "Test",
                            category = "Рестораны"
                    ),
                    @Spending(
                            amount = 500,
                            description = "Test 2",
                            category = "Такси"
                    ),

            }
    )
    @Test
    @ApiLogin
    void checkStatByFilterPeriod(@Token String bearerToken, UserJson userJson) {
        SpendJson spendJson = userJson.testData().spendings().get(0);

        Date date = new Date();
        date.setMonth(date.getMonth() - 5);
        SpendJson updatedSpend = new SpendJson(
                spendJson.id(),
                date,
                spendJson.category(),
                spendJson.currency(),
                spendJson.amount(),
                spendJson.description(),
                spendJson.username()
        );

        new SpendApiClient().update(updatedSpend);

        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(FilterPeriod.TODAY)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        Assertions.assertEquals(
                53766.67,
                result.total
        );
    }
}
