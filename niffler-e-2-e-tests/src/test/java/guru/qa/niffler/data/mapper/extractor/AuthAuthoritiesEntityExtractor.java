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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
public class AuthAuthoritiesEntityExtractor implements ResultSetExtractor<List<AuthorityEntity>> {

    public static final AuthAuthoritiesEntityExtractor instance = new AuthAuthoritiesEntityExtractor();

    private AuthAuthoritiesEntityExtractor() {
    }

    @Override
    public @Nonnull List<AuthorityEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, AuthorityEntity> authorityMap = new ConcurrentHashMap<>();
        UUID authorityId;
        while (rs.next()) {
            authorityId = rs.getObject("authority_id", UUID.class);
            AuthorityEntity authority = authorityMap.computeIfAbsent(
                    authorityId, key -> {
                        try {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setId(rs.getObject("authority_id", UUID.class));
                            ae.setAuthority(Authority.valueOf(rs.getString("authority")));
                            return ae;
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });
            AuthUserEntity user = new AuthUserEntity();
            user.setId(rs.getObject("user_id", UUID.class));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setEnabled(rs.getBoolean("enabled"));
            user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
            user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
            user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
            authority.setUser(user);
        }
        return new ArrayList<>(authorityMap.values());
    }
}
