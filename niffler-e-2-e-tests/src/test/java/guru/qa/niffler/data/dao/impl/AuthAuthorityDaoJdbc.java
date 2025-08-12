package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

@ParametersAreNonnullByDefault
public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public void createAuthority(AuthorityEntity... authorities) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO authority (user_id, authority) " +
                        "VALUES (?, ?)"
        )) {
            Arrays.stream(authorities).forEach(authority -> {
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
    public @Nonnull List<AuthorityEntity> findAll() {
        try (PreparedStatement preparedStatement = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM authority"
        )) {
            preparedStatement.execute();
            List<AuthorityEntity> authorityEntities = new ArrayList<>();
            try (ResultSet rs = preparedStatement.getResultSet()) {
                while (rs.next()) {
                    AuthorityEntity authorityEntity = new AuthorityEntity();
                    authorityEntity.setId(rs.getObject("id", UUID.class));
                    authorityEntity.setUser(rs.getObject("user_id", AuthUserEntity.class));
                    authorityEntity.setAuthority(Authority.valueOf(rs.getObject("authority", String.class)));
                    authorityEntities.add(authorityEntity);
                }
                return authorityEntities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
