package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<AuthorityEntity> createAuthority(AuthorityEntity... authorities) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO authority (user_id, authority) " +
                        "VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
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

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                int id = 0;
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                    authorities[id++].setId(generatedKey);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            return Arrays.asList(authorities);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
