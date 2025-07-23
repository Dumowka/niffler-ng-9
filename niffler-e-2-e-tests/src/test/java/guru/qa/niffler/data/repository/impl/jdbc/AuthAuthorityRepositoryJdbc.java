package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.repository.AuthAuthorityRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthAuthorityRepositoryJdbc implements AuthAuthorityRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public void createAuthority(AuthorityEntity... authorityEntities) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO authority (user_id, authority) " +
                        "VALUES (?, ?)"
        )) {
            Arrays.stream(authorityEntities).forEach(authority -> {
                        try {
                            ps.setObject(1, authority.getUser().getId());
                            ps.setString(2, authority.getAuthority().name());
                            ps.addBatch();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthorityEntity> findAll() {
        try (PreparedStatement preparedStatement = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                """
                            SELECT
                                u.id AS user_id,
                                u.username,
                                u.password,
                                u.enabled,
                                u.account_non_expired,
                                u.account_non_locked,
                                u.credentials_non_expired,
                                a.id AS authority_id,
                                a.authority
                            FROM authority a
                                     JOIN "user" u ON u.id = a.user_id
                        """
        )) {
            preparedStatement.execute();
            List<AuthorityEntity> authorityEntities = new ArrayList<>();
            try (ResultSet rs = preparedStatement.getResultSet()) {
                while (rs.next()) {
                    AuthUserEntity userEntity = new AuthUserEntity();
                    userEntity.setId(rs.getObject("user_id", UUID.class));
                    userEntity.setUsername(rs.getString("username"));
                    userEntity.setPassword(rs.getString("password"));
                    userEntity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    userEntity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    userEntity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));

                    AuthorityEntity authorityEntity = new AuthorityEntity();
                    authorityEntity.setId(rs.getObject("id", UUID.class));
                    authorityEntity.setAuthority(Authority.valueOf(rs.getObject("authority", String.class)));
                    authorityEntity.setUser(userEntity);

                    authorityEntities.add(authorityEntity);
                }
                return authorityEntities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
