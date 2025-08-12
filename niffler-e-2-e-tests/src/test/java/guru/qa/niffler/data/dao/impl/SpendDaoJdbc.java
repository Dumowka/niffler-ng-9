package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

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
public class SpendDaoJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public @Nonnull SpendEntity create(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                        "VALUES ( ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            spend.setId(generatedKey);
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull SpendEntity update(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "UPDATE \"spend\" SET spend_date = ?, currency = ?, amount = ?, description = ? WHERE id = ?"
        )) {
            ps.setDate(1, new Date(spend.getSpendDate().getTime()));
            ps.setString(2, spend.getCurrency().name());
            ps.setDouble(3, spend.getAmount());
            ps.setString(4, spend.getDescription());
            ps.setObject(5, spend.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spend;
    }

    @Override
    public @Nonnull Optional<SpendEntity> findSpendById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                """
                        SELECT 
                            s.id as id, 
                            s.username as username, 
                            s.spend_date as spend_date, 
                            s.currency as currency, 
                            s.amount as amount, 
                            s.description as description, 
                            s.category_id, 
                            c.id as category_id, 
                            c.name, 
                            c.username, 
                            c.archived
                        FROM spend s
                        JOIN category c
                        ON s.category_id = category_id
                        WHERE s.id = ?
                    """
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet resultSet = ps.getResultSet()) {
                if (resultSet.next()) {
                    SpendEntity spendEntity = new SpendEntity();
                    spendEntity.setId(id);
                    spendEntity.setUsername(resultSet.getString("username"));
                    spendEntity.setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")));
                    spendEntity.setAmount(resultSet.getDouble("amount"));
                    spendEntity.setDescription(resultSet.getString("description"));
                    spendEntity.setSpendDate(resultSet.getDate("spend_date"));

                    CategoryEntity category = new CategoryEntity();
                    category.setId(resultSet.getObject("category_id", UUID.class));
                    category.setName(resultSet.getString("name"));
                    category.setUsername(resultSet.getString("username"));
                    category.setArchived(resultSet.getBoolean("archived"));
                    spendEntity.setCategory(category);
                    return Optional.of(spendEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull List<SpendEntity> findAllSpendsByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                """
                        SELECT 
                                s.id as id, 
                                s.username as username, 
                                s.spend_date as spend_date, 
                                s.currency as currency, 
                                s.amount as amount, 
                                s.description as description, 
                                s.category_id, 
                                c.id as category_id, 
                                c.name as category_name, 
                                c.username as category_username, 
                                c.archived as category_archived
                            FROM spend s
                            JOIN category c
                            ON s.category_id = category_id
                            WHERE s.username = ?
                        """
        )) {
            ps.setObject(1, username);
            ps.execute();

            List<SpendEntity> spends = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    SpendEntity spendEntity = new SpendEntity();
                    spendEntity.setId(rs.getObject("id", UUID.class));
                    spendEntity.setUsername(rs.getString("username"));
                    spendEntity.setSpendDate(rs.getDate("spend_date"));
                    spendEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    spendEntity.setAmount(rs.getDouble("amount"));
                    spendEntity.setDescription(rs.getString("description"));

                    CategoryEntity category = new CategoryEntity();
                    category.setId(rs.getObject("category_id", UUID.class));
                    category.setName(rs.getString("category_name"));
                    category.setUsername(rs.getString("category_username"));
                    category.setArchived(rs.getBoolean("category_archived"));
                    spendEntity.setCategory(category);

                    spends.add(spendEntity);
                }
                return spends;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                """
                        SELECT 
                            s.id as id, 
                            s.username as username, 
                            s.spend_date as spend_date, 
                            s.currency as currency, 
                            s.amount as amount, 
                            s.description as description, 
                            s.category_id, 
                            c.id as category_id, 
                            c.name, 
                            c.username, 
                            c.archived
                        FROM spend s
                        JOIN category c
                        ON s.category_id = category_id
                        WHERE s.username = ? AND s.description = ?
                    """
        )) {
            ps.setObject(1, username);
            ps.setObject(2, description);
            ps.execute();
            try (ResultSet resultSet = ps.getResultSet()) {
                if (resultSet.next()) {
                    SpendEntity spendEntity = new SpendEntity();
                    spendEntity.setId(resultSet.getObject("id", UUID.class));
                    spendEntity.setUsername(resultSet.getString("username"));
                    spendEntity.setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")));
                    spendEntity.setAmount(resultSet.getDouble("amount"));
                    spendEntity.setDescription(resultSet.getString("description"));
                    spendEntity.setSpendDate(resultSet.getDate("spend_date"));

                    CategoryEntity category = new CategoryEntity();
                    category.setId(resultSet.getObject("category_id", UUID.class));
                    category.setName(resultSet.getString("name"));
                    category.setUsername(resultSet.getString("username"));
                    category.setArchived(resultSet.getBoolean("archived"));
                    spendEntity.setCategory(category);
                    return Optional.of(spendEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Nonnull List<SpendEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                """
                        SELECT 
                                s.id as id, 
                                s.username as username, 
                                s.spend_date as spend_date, 
                                s.currency as currency, 
                                s.amount as amount, 
                                s.description as description, 
                                s.category_id, 
                                c.id as category_id, 
                                c.name as category_name, 
                                c.username as category_username, 
                                c.archived as category_archived
                            FROM spend s
                            JOIN category c
                            ON s.category_id = category_id                        
                        """
        )) {
            ps.execute();

            List<SpendEntity> spends = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    SpendEntity spendEntity = new SpendEntity();
                    spendEntity.setId(rs.getObject("id", UUID.class));
                    spendEntity.setUsername(rs.getString("username"));
                    spendEntity.setSpendDate(rs.getDate("spend_date"));
                    spendEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    spendEntity.setAmount(rs.getDouble("amount"));
                    spendEntity.setDescription(rs.getString("description"));

                    CategoryEntity category = new CategoryEntity();
                    category.setId(rs.getObject("category_id", UUID.class));
                    category.setName(rs.getString("category_name"));
                    category.setUsername(rs.getString("category_username"));
                    category.setArchived(rs.getBoolean("category_archived"));
                    spendEntity.setCategory(category);

                    spends.add(spendEntity);
                }
                return spends;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, spend.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}