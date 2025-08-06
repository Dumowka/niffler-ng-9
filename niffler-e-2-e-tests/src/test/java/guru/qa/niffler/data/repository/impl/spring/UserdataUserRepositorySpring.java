package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserdataUserRepositorySpring implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    private static final String REQUESTERS_SQL = """
            SELECT
            f.status,
            f.created_date,
            u.*
            FROM friendship f
            JOIN "user" u ON u.id = f.requester_id
            WHERE f.addressee_id = ?
            """;

    private static final String ADDRESSEES_SQL = """
            SELECT
            f.status,
            f.created_date,
            u.*
            FROM friendship f
            JOIN "user" u ON u.id = f.addressee_id
            WHERE f.requester_id = ?
            """;

    private static final String ADD_INVITATION_SQL = "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
            "VALUES (?, ?, ?, ?)";

    @Override
    public UserEntity create(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                            "VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        UserEntity user = jdbcTemplate.queryForObject(
                "SELECT * FROM \"user\" WHERE id = ?",
                UserdataUserEntityRowMapper.instance,
                id
        );

        try {
            List<FriendshipEntity> requesters = jdbcTemplate.query(
                    REQUESTERS_SQL,
                    ps -> ps.setObject(1, user.getId()),
                    friendshipRequesterMapper(user)
            );
            user.setFriendshipRequests(requesters);

            List<FriendshipEntity> addressees = jdbcTemplate.query(
                    ADDRESSEES_SQL,
                    ps -> ps.setObject(1, user.getId()),
                    friendshipAddresseesMapper(user)
            );
            user.setFriendshipAddressees(addressees);

            return Optional.of(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        UserEntity user = jdbcTemplate.queryForObject(
                "SELECT * FROM \"user\" WHERE username = ?",
                UserdataUserEntityRowMapper.instance,
                username
        );

        try {
            List<FriendshipEntity> requesters = jdbcTemplate.query(
                    REQUESTERS_SQL,
                    ps -> ps.setObject(1, user.getId()),
                    friendshipRequesterMapper(user)
            );
            user.setFriendshipRequests(requesters);

            List<FriendshipEntity> addressees = jdbcTemplate.query(
                    ADDRESSEES_SQL,
                    ps -> ps.setObject(1, user.getId()),
                    friendshipAddresseesMapper(user)
            );
            user.setFriendshipAddressees(addressees);

            return Optional.of(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        List<UserEntity> users = jdbcTemplate.query(
                "SELECT * FROM \"user\" WHERE id = ?",
                UserdataUserEntityRowMapper.instance
        );

        users.forEach(user -> {
            try {
                List<FriendshipEntity> requesters = jdbcTemplate.query(
                        REQUESTERS_SQL,
                        ps -> ps.setObject(1, user.getId()),
                        friendshipRequesterMapper(user)
                );
                user.setFriendshipRequests(requesters);

                List<FriendshipEntity> addressees = jdbcTemplate.query(
                        ADDRESSEES_SQL,
                        ps -> ps.setObject(1, user.getId()),
                        friendshipAddresseesMapper(user)
                );
                user.setFriendshipAddressees(addressees);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return users;
    }

    @Override
    public UserEntity update(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update("UPDATE \"user\" SET currency = ?, firstname = ?, surname = ?, photo = ?, " +
                        "photo_small = ? WHERE id = ?",
                user.getCurrency().name(), user.getFirstname(), user.getSurname(), user.getPhoto(),
                user.getPhotoSmall(), user.getId());

        FriendshipEntity[] friendshipEntities = user.getFriendshipRequests().toArray(new FriendshipEntity[0]);
        jdbcTemplate.batchUpdate(
                "INSERT INTO friendship (requester_id, addressee_id, status) VALUES (?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, user.getId());
                        ps.setString(2, String.valueOf(friendshipEntities[i].getAddressee().getId()));
                        ps.setString(3, friendshipEntities[i].getStatus().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return friendshipEntities.length;
                    }
                }
        );
        return user;
    }

    @Override
    public void addInvitation(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(
                ADD_INVITATION_SQL,
                requester.getId(),
                addressee.getId(),
                FriendshipStatus.PENDING.name(),
                new Date(System.currentTimeMillis())
        );
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(
                ADD_INVITATION_SQL,
                requester.getId(),
                addressee.getId(),
                FriendshipStatus.ACCEPTED.name(),
                new Date(System.currentTimeMillis())
        );
        jdbcTemplate.update(
                ADD_INVITATION_SQL,
                addressee.getId(),
                requester.getId(),
                FriendshipStatus.ACCEPTED.name(),
                new Date(System.currentTimeMillis())
        );
    }

    @Override
    public void remove(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private RowMapper<FriendshipEntity> friendshipRequesterMapper(UserEntity user) throws SQLException {
        return (rs, rowNum) -> {
            UserEntity requester = UserdataUserEntityRowMapper.instance.mapRow(rs, rowNum);
            FriendshipEntity fe = new FriendshipEntity();
            fe.setRequester(requester);
            fe.setAddressee(user);
            fe.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
            fe.setCreatedDate(rs.getDate("created_date"));
            return fe;
        };
    }

    private RowMapper<FriendshipEntity> friendshipAddresseesMapper(UserEntity user) throws SQLException {
        return (rs, rowNum) -> {
            UserEntity addressee = UserdataUserEntityRowMapper.instance.mapRow(rs, rowNum);
            FriendshipEntity fe = new FriendshipEntity();
            fe.setRequester(user);
            fe.setAddressee(addressee);
            fe.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
            fe.setCreatedDate(rs.getDate("created_date"));
            return fe;
        };
    }
}
