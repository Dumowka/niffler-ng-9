package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.UserdataUserEntity;
import guru.qa.niffler.model.spend.CurrencyValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoJdbc implements UserdataUserDao {

    private static final Config CFG = Config.getInstance();

    private final Connection connection;

    public UserdataUserDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public UserdataUserEntity createUser(UserdataUserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
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
    public Optional<UserdataUserEntity> findUserById(Long id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setObject(1, id);
            ps.executeQuery();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserdataUserEntity userdataUserEntity = new UserdataUserEntity();
                    userdataUserEntity.setId(rs.getObject("id", UUID.class));
                    userdataUserEntity.setUsername(rs.getString("username"));
                    userdataUserEntity.setCurrency(rs.getObject("currency", CurrencyValues.class));
                    userdataUserEntity.setFirstname(rs.getString("firstname"));
                    userdataUserEntity.setSurname(rs.getString("surname"));
                    userdataUserEntity.setPhoto(rs.getBytes("photo"));
                    userdataUserEntity.setPhotoSmall(rs.getBytes("photo_small"));
                    userdataUserEntity.setFullname(rs.getString("full_name"));
                    return Optional.of(userdataUserEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserdataUserEntity> findUserByUsername(String username) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, username);
            ps.executeQuery();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserdataUserEntity userdataUserEntity = new UserdataUserEntity();
                    userdataUserEntity.setId(rs.getObject("id", UUID.class));
                    userdataUserEntity.setUsername(rs.getString("username"));
                    userdataUserEntity.setCurrency(rs.getObject("currency", CurrencyValues.class));
                    userdataUserEntity.setFirstname(rs.getString("firstname"));
                    userdataUserEntity.setSurname(rs.getString("surname"));
                    userdataUserEntity.setPhoto(rs.getBytes("photo"));
                    userdataUserEntity.setPhotoSmall(rs.getBytes("photo_small"));
                    userdataUserEntity.setFullname(rs.getString("full_name"));
                    return Optional.of(userdataUserEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteUser(UserdataUserEntity user) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
