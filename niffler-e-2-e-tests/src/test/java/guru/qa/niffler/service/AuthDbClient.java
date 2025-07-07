package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.auth.Authority;
import guru.qa.niffler.model.auth.AuthorityJson;

import java.util.Arrays;

import static guru.qa.niffler.data.Databases.transaction;

public class AuthDbClient {
    private static final Config CFG = Config.getInstance();

    public AuthUserJson createUser(AuthUserJson authUserJson, int transactionLevel) {
        return transaction(connection -> {
                    AuthUserEntity authUserEntity = AuthUserEntity.fromJson(authUserJson);
                    AuthUserJson createdUser = AuthUserJson.fromEntity(new AuthUserDaoJdbc(connection).createUser(authUserEntity));

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(authority -> {
                        AuthorityEntity authorityEntity = new AuthorityEntity();
                        authorityEntity.setUser(authUserEntity);
                        authorityEntity.setAuthority(authority);
                        return authorityEntity;
                    }).toArray(AuthorityEntity[]::new);

                    createdUser.setAuthorities(
                            new AuthAuthorityDaoJdbc(connection)
                                    .createAuthority(authorityEntities)
                                    .stream()
                                    .map(AuthorityJson::fromEntity)
                                    .toList()
                    );
                    return createdUser;
                },
                CFG.authJdbcUrl(),
                transactionLevel
        );
    }
}
