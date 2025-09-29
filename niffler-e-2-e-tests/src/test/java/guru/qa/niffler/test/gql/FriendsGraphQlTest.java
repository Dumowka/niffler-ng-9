package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.Friends2SubQueriesQuery;
import guru.qa.FriendsWithCategoriesQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FriendsGraphQlTest extends BaseGraphQlTest {

    @User(
            friends = 2,
            spendings = @Spending(
                    amount = 79990.00,
                    description = "Advanced 9 поток!",
                    category = "Обучение"
            )
    )
    @Test
    @ApiLogin
    void friendsWithCategoryTest(@Token String bearerToken) {
        final ApolloCall<FriendsWithCategoriesQuery.Data> currenciesCall = apolloClient.query(FriendsWithCategoriesQuery.builder()
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<FriendsWithCategoriesQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final FriendsWithCategoriesQuery.Data data = response.dataOrThrow();

        List<String> messages = response.errors.stream().map(error -> error.getMessage()).toList();

        Assertions.assertNotNull(data);
        Assertions.assertTrue(messages.stream().allMatch(message -> message.contains("Can`t query categories for another user")));
    }

    @User(friends = 1)
    @Test
    @ApiLogin
    void checkDepthInRequestTest(@Token String bearerToken) {
        final ApolloCall<Friends2SubQueriesQuery.Data> currenciesCall = apolloClient.query(Friends2SubQueriesQuery.builder()
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<Friends2SubQueriesQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        Assertions.assertTrue(response.errors.get(0).getMessage().contains("Can`t fetch over 2 friends sub-queries"));
    }
}

