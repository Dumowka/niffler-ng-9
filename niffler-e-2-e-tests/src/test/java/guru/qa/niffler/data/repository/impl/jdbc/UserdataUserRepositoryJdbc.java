package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

@ParametersAreNonnullByDefault
public class UserdataUserRepositoryJdbc implements UserdataUserRepository {

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

    private static final UserdataUserDao USER_DATA_USER_DAO = new UserdataUserDaoJdbc();

    @Override
    public @Nonnull UserEntity create(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull Optional<UserEntity> findById(UUID id) {
        return USER_DATA_USER_DAO.findUserById(id);
    }

    @Override
    public @Nonnull Optional<UserEntity> findByUsername(String username) {
        return USER_DATA_USER_DAO.findUserByUsername(username);
    }

    @Override
    public @Nonnull List<UserEntity> findAll() {
        return USER_DATA_USER_DAO.findAll();
    }

    @Override
    public @Nonnull UserEntity update(UserEntity user) {
        try (PreparedStatement usersPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "UPDATE \"user\" SET currency = ?, firstname = ?, surname = ?, photo = ?, photo_small = ? " +
                        "WHERE id = ?");
             PreparedStatement friendsPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO friendship (requester_id, addressee_id, status) VALUES (?, ?, ?)"
             )) {
            usersPs.setString(1, user.getCurrency().name());
            usersPs.setString(2, user.getFirstname());
            usersPs.setString(3, user.getSurname());
            usersPs.setBytes(4, user.getPhoto());
            usersPs.setBytes(5, user.getPhotoSmall());
            usersPs.setObject(6, user.getId());
            usersPs.executeUpdate();

            for (FriendshipEntity fe : user.getFriendshipRequests()) {
                friendsPs.setObject(1, user.getId());
                friendsPs.setObject(2, fe.getAddressee().getId());
                friendsPs.setString(3, fe.getStatus().name());
                friendsPs.addBatch();
                friendsPs.clearParameters();
            }
            friendsPs.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public void addInvitation(UserEntity requester, UserEntity addressee) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
                        "VALUES (?, ?, ?, ?)"
        )) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, FriendshipStatus.PENDING.name());
            ps.setDate(4, new Date(System.currentTimeMillis()));
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
                        "VALUES (?, ?, ?, ?)"
        )) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, FriendshipStatus.ACCEPTED.name());
            ps.setDate(4, new Date(System.currentTimeMillis()));
            ps.execute();

            ps.setObject(1, addressee.getId());
            ps.setObject(2, requester.getId());
            ps.setString(3, FriendshipStatus.ACCEPTED.name());
            ps.setDate(4, new Date(System.currentTimeMillis()));

            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    private List<FriendshipEntity> extractRequesters(PreparedStatement ps, UserEntity user) throws SQLException {
        ps.setObject(1, user.getId());
        List<FriendshipEntity> feList = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserEntity requesters = UserdataUserEntityRowMapper.instance.mapRow(rs, rs.getRow());
                FriendshipEntity fe = new FriendshipEntity();
                fe.setRequester(requesters);
                fe.setAddressee(user);
                fe.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                fe.setCreatedDate(rs.getDate("created_date"));
                feList.add(fe);
            }
            return feList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<FriendshipEntity> extractAddressees(PreparedStatement ps, UserEntity user) throws SQLException {
        ps.setObject(1, user.getId());
        List<FriendshipEntity> feList = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                UserEntity addressees = UserdataUserEntityRowMapper.instance.mapRow(rs, rs.getRow());
                FriendshipEntity fe = new FriendshipEntity();
                fe.setRequester(user);
                fe.setAddressee(addressees);
                fe.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
                fe.setCreatedDate(rs.getDate("created_date"));
                feList.add(fe);
            }
            return feList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
