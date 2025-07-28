package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.extractor.AuthAuthoritiesEntityExtractor;
import guru.qa.niffler.data.repository.AuthAuthorityRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AuthAuthorityRepositorySpring implements AuthAuthorityRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public void createAuthority(AuthorityEntity... authorityEntities) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (? , ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authorityEntities[i].getUser().getId());
                        ps.setString(2, authorityEntities[i].getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authorityEntities.length;
                    }
                }
        );
    }

    @Override
    public List<AuthorityEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        return jdbcTemplate.query(
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
                        """,
                AuthAuthoritiesEntityExtractor.instance
        );
    }
}
