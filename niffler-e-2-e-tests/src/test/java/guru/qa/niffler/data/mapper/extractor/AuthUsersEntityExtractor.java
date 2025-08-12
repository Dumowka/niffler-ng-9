package guru.qa.niffler.data.mapper.extractor;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
public class AuthUsersEntityExtractor implements ResultSetExtractor<List<AuthUserEntity>> {

    public static final AuthUsersEntityExtractor instance = new AuthUsersEntityExtractor();

    private AuthUsersEntityExtractor() {
    }

    @Override
    public @Nonnull List<AuthUserEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, AuthUserEntity> userMap = new ConcurrentHashMap<>();
        UUID userId;
        while (rs.next()) {
            userId = rs.getObject("user_id", UUID.class);
            AuthUserEntity authUserEntity = userMap.computeIfAbsent(
                    userId,
                    key -> {
                        try {
                            AuthUserEntity result = new AuthUserEntity();
                            result.setId(key);
                            result.setUsername(rs.getString("username"));
                            result.setPassword(rs.getString("password"));
                            result.setEnabled(rs.getBoolean("enabled"));
                            result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                            result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                            result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                            return result;
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );

            AuthorityEntity authority = new AuthorityEntity();
            authority.setId(rs.getObject("authority_id", UUID.class));
            authority.setAuthority(Authority.valueOf(rs.getString("authority")));
            authority.setUser(authUserEntity);

            authUserEntity.getAuthorities().add(authority);
        }
        return userMap.values().stream().toList();
    }
}
