package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthAuthorityEntityRowMapper implements RowMapper<AuthorityEntity> {

    public static final AuthAuthorityEntityRowMapper instance = new AuthAuthorityEntityRowMapper();

    private AuthAuthorityEntityRowMapper() {
    }

    @Override
    public @Nonnull AuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthorityEntity result = new AuthorityEntity();
        result.setId(rs.getObject("id", UUID.class));
        result.setUser(rs.getObject("user_id", AuthUserEntity.class));
        result.setAuthority(Authority.valueOf(rs.getObject("authority", String.class)));
        return result;
    }
}
