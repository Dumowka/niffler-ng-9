package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class CategoryDaoJdbc implements CategoryDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public CategoryEntity create(CategoryEntity category) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO category (username, name, archived) " +
                        "VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            category.setId(generatedKey);
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM category WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(rs.getObject("id", UUID.class));
                    categoryEntity.setUsername(rs.getString("username"));
                    categoryEntity.setName(rs.getString("name"));
                    categoryEntity.setArchived(rs.getBoolean("archived"));
                    return Optional.of(categoryEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM category WHERE username = ? AND category = ?"
        )) {
            ps.setObject(1, username);
            ps.setObject(2, categoryName);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(rs.getObject("id", UUID.class));
                    categoryEntity.setUsername(rs.getString("username"));
                    categoryEntity.setName(rs.getString("name"));
                    categoryEntity.setArchived(rs.getBoolean("archived"));
                    return Optional.of(categoryEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryEntity> findAllCategoriesByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM category WHERE username = ?"
        )) {
            ps.setObject(1, username);
            ps.execute();

            List<CategoryEntity> categories = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(rs.getObject("id", UUID.class));
                    categoryEntity.setUsername(rs.getString("username"));
                    categoryEntity.setName(rs.getString("name"));
                    categoryEntity.setArchived(rs.getBoolean("archived"));
                    categories.add(categoryEntity);
                }
                return categories;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryEntity> findAll() {
        try (PreparedStatement preparedStatement = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM category"
        )) {
            preparedStatement.execute();
            List<CategoryEntity> categoryEntities = new ArrayList<>();
            try (ResultSet rs = preparedStatement.getResultSet()) {
                while (rs.next()) {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(rs.getObject("id", UUID.class));
                    categoryEntity.setUsername(rs.getString("username"));
                    categoryEntity.setName(rs.getString("name"));
                    categoryEntity.setArchived(rs.getBoolean("archived"));
                    categoryEntities.add(categoryEntity);
                }
                return categoryEntities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM category WHERE id = ?"
        )) {
            ps.setObject(1, category.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
