package guru.qa.niffler.test;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.auth.Authority;
import guru.qa.niffler.model.auth.AuthorityJson;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserdataUserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.Date;

import static guru.qa.niffler.data.Databases.xaTransaction;

@Disabled
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
                ),
                Connection.TRANSACTION_REPEATABLE_READ
        );

        System.out.println(spend);
    }

    @Test
    void txTest2() {
        final Config CFG = Config.getInstance();

        AuthUserJson authUserJson = new AuthUserJson();
        authUserJson.setUsername(RandomDataUtils.randomUsername());
        authUserJson.setPassword(RandomStringUtils.randomAlphanumeric(10));
        authUserJson.setEnabled(true);
        authUserJson.setAccountNonExpired(true);
        authUserJson.setAccountNonLocked(true);
        authUserJson.setCredentialsNonExpired(true);

        var userdataUserJson = UserdataUserJson.fromAuthUserJson(authUserJson);

        System.out.println(authUserJson);

        xaTransaction(
                Connection.TRANSACTION_SERIALIZABLE,
                new Databases.XaConsumer(
                        connection -> {
                            AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authUserJson);
                            AuthUserJson createdUser = AuthUserJson.fromEntity(new AuthUserDaoJdbc(connection).createUser(authUserEntity));

                            AuthorityEntity authorityEntity1 = new AuthorityEntity();
                            authorityEntity1.setAuthority(Authority.read);
                            authorityEntity1.setUser(authUserEntity);

                            AuthorityEntity authorityEntity2 = new AuthorityEntity();
                            authorityEntity2.setAuthority(Authority.write);
                            authorityEntity2.setUser(authUserEntity);

                            createdUser.getAuthorities().add(AuthorityJson.fromEntity(authorityEntity1));
                            createdUser.getAuthorities().add(AuthorityJson.fromEntity(authorityEntity2));
                        },
                        CFG.authJdbcUrl()
                ),
                new Databases.XaConsumer(
                        connection -> {
                            var userdataUserEntity = UserdataUserEntity.fromJson(userdataUserJson);
                            new UserdataUserDaoJdbc(connection).createUser(userdataUserEntity);
                        },
                        CFG.userdataJdbcUrl()
                )
        );
    }
}

