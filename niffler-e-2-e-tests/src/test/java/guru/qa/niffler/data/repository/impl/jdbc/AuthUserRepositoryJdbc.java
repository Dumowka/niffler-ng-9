package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private static final AuthAuthorityDao AUTHORITY_DAO = new AuthAuthorityDaoJdbc();
    private static final AuthUserDao AUTH_USER_DAO = new AuthUserDaoJdbc();

    @Override
    public @Nonnull AuthUserEntity create(AuthUserEntity user) {
        AuthUserEntity createdUser = AUTH_USER_DAO.createUser(user);
        AUTHORITY_DAO.createAuthority(user.getAuthorities().toArray(new AuthorityEntity[0]));
        return createdUser;
    }

    @Override
    public @Nonnull AuthUserEntity update(AuthUserEntity user) {
        try (PreparedStatement preparedStatement = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "UPDATE \"user\" SET username = ?, password = ?, enabled = ?, account_non_expired = ?, " +
                        "account_non_locked = ?, credentials_non_expired = ? WHERE id = ?"
        )) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setObject(2, user.getPassword());
            preparedStatement.setBoolean(3, user.getEnabled());
            preparedStatement.setBoolean(4, user.getAccountNonExpired());
            preparedStatement.setBoolean(5, user.getAccountNonLocked());
            preparedStatement.setBoolean(6, user.getCredentialsNonExpired());
            preparedStatement.setObject(7, user.getId());

            preparedStatement.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT u.id AS id, u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, " +
                        "u.credentials_non_expired, a.id AS authority_id, a.authority FROM \"user\" u " +
                        "JOIN authority a ON u.id = a.user_id WHERE u.id = ?")) {
            ps.setObject(1, id);

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                AuthUserEntity user = null;
                List<AuthorityEntity> authorityEntities = new ArrayList<>();
                while (rs.next()) {
                    if (user == null) {
                        user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }

                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(user);
                    ae.setId(rs.getObject("authority_id", UUID.class));
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    authorityEntities.add(ae);

                    AuthUserEntity result = new AuthUserEntity();
                    result.setId(rs.getObject("id", UUID.class));
                    result.setUsername(rs.getString("username"));
                    result.setPassword(rs.getString("password"));
                    result.setEnabled(rs.getBoolean("enabled"));
                    result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    user.setAuthorities(authorityEntities);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT u.id AS id, u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, " +
                        "u.credentials_non_expired, a.id AS authority_id, a.authority FROM \"user\" u " +
                        "JOIN authority a ON u.id = a.user_id WHERE u.username = ?")) {
            ps.setObject(1, username);

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                AuthUserEntity user = null;
                List<AuthorityEntity> authorityEntities = new ArrayList<>();
                while (rs.next()) {
                    if (user == null) {
                        user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }

                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(user);
                    ae.setId(rs.getObject("authority_id", UUID.class));
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                    authorityEntities.add(ae);

                    AuthUserEntity result = new AuthUserEntity();
                    result.setId(rs.getObject("id", UUID.class));
                    result.setUsername(rs.getString("username"));
                    result.setPassword(rs.getString("password"));
                    result.setEnabled(rs.getBoolean("enabled"));
                    result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    user.setAuthorities(authorityEntities);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull List<AuthUserEntity> findAll() {
        try (PreparedStatement preparedStatement = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\""
        )) {
            preparedStatement.execute();
            List<AuthUserEntity> userEntities = new ArrayList<>();
            try (ResultSet rs = preparedStatement.getResultSet()) {
                while (rs.next()) {
                    AuthUserEntity user = new AuthUserEntity();
                    user.setId(rs.getObject("id", UUID.class));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEnabled(rs.getBoolean("enabled"));
                    user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    userEntities.add(user);
                }
                return userEntities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(AuthUserEntity user) {
        try (PreparedStatement psAuthority = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"authority\" WHERE user_id = ?");
             PreparedStatement psUser = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                     "DELETE FROM \"user\" WHERE id = ?")
        ) {
            psAuthority.setObject(1, user.getId());
            int deleteRowsAuthority = psAuthority.executeUpdate();
            if (deleteRowsAuthority == 0) {
                throw new SQLException("Authorities не были удалены");
            }

            psUser.setObject(1, user.getId());
            int deleteRowsUser = psUser.executeUpdate();
            if (deleteRowsUser == 0) {
                throw new SQLException("User не был удален");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}