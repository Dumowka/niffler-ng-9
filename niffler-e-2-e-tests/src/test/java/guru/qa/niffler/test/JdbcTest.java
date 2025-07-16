package guru.qa.niffler.test;

import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserdataUserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;


public class JdbcTest {

    @Test
    void txTest() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx-2",
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx",
                        null
                )
        );

        System.out.println(spend);
    }

    @Test
    void xaTxTest() {
        UsersDbClient usersDbClient = new UsersDbClient();

        UserdataUserJson user = new UserdataUserJson(
                null,
                "dzhirnov-6",
                CurrencyValues.RUB,
                null,
                null,
                null,
                null,
                null
        );

        UserdataUserJson createdUser = usersDbClient.createUserSpringJdbc(user);

        System.out.println(createdUser);
    }


    /*
        При установке в UserdataUserEntity username = null пользователь создается в Auth.user и Auth.Authority, но не создается в Userdata.user
        При установке в AuthorityEntity userId = null пользователь создается в Auth.user, но не создается в Auth.Authority и Userdata.user
     */
    @Test
    void chainedTxTest() {
        UsersDbClient usersDbClient = new UsersDbClient();

        UserdataUserJson user = new UserdataUserJson(
                null,
                "dzhirnov-7",
                CurrencyValues.RUB,
                null,
                null,
                null,
                null,
                null
        );

        UserdataUserJson createdUser = usersDbClient.createUserChained(user);

        System.out.println(createdUser);
    }

    /*
        При установке в UserdataUserEntity username = null пользователь не создается в Auth.user, Auth.Authority и Userdata.user
        При установке в AuthorityEntity userId = null пользователь не создается в Auth.user, Auth.Authority и Userdata.user
     */
    @Test
    void chainedTxSpringJdbcTest() {
        UsersDbClient usersDbClient = new UsersDbClient();

        UserdataUserJson user = new UserdataUserJson(
                null,
                "dzhirnov-8",
                CurrencyValues.RUB,
                null,
                null,
                null,
                null,
                null
        );

        UserdataUserJson createdUser = usersDbClient.createUserSpringJdbcChained(user);

        System.out.println(createdUser);
    }
}

