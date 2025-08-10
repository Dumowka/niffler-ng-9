package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendRepositoryJdbc implements SpendRepository {

    private static final SpendDao SPEND_DAO = new SpendDaoJdbc();
    private static final CategoryDao CATEGORY_DAO = new CategoryDaoJdbc();

    @Override
    public @Nonnull SpendEntity create(SpendEntity spend) {
        if (spend.getCategory().getId() == null || CATEGORY_DAO.findCategoryById(spend.getCategory().getId()).isEmpty()) {
            CategoryEntity categoryEntity = CATEGORY_DAO.create(spend.getCategory());
            spend.setCategory(categoryEntity);
        }
        return SPEND_DAO.create(spend);
    }

    @Override
    public @Nonnull SpendEntity update(SpendEntity spend) {
        SPEND_DAO.update(spend);
        CATEGORY_DAO.update(spend.getCategory());
        return spend;
    }

    @Override
    public @Nonnull CategoryEntity createCategory(CategoryEntity category) {
        return CATEGORY_DAO.create(category);
    }

    @Override
    public @Nonnull CategoryEntity updateCategory(CategoryEntity category) {
        CATEGORY_DAO.update(category);
        return category;
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryById(UUID id) {
        try {
            return CATEGORY_DAO.findCategoryById(id);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String name) {
        try {
            return CATEGORY_DAO.findCategoryByUsernameAndCategoryName(username, name);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public @Nonnull Optional<SpendEntity> findById(UUID id) {
        Optional<SpendEntity> spendEntity = SPEND_DAO.findSpendById(id);
        if (spendEntity.isPresent()) {
            UUID categoryId = spendEntity
                    .map(spend -> spend.getCategory().getId())
                    .orElseThrow(() -> new NoSuchElementException("Не найдена категория"));
            Optional<CategoryEntity> categoryEntity = CATEGORY_DAO.findCategoryById(categoryId);
            categoryEntity.ifPresent(category -> spendEntity.get().setCategory(category));
        }
        return spendEntity;
    }

    @Override
    public @Nonnull Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        Optional<SpendEntity> spendEntity = SPEND_DAO.findByUsernameAndSpendDescription(username, description);
        if (spendEntity.isPresent()) {
            UUID categoryId = spendEntity
                    .map(spend -> spend.getCategory().getId())
                    .orElseThrow(() -> new NoSuchElementException("Не найдена категория"));
            Optional<CategoryEntity> categoryEntity = CATEGORY_DAO.findCategoryById(categoryId);
            categoryEntity.ifPresent(category -> spendEntity.get().setCategory(category));
        }
        return spendEntity;
    }

    @Override
    public void remove(SpendEntity spend) {
        SPEND_DAO.deleteSpend(spend);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        List<SpendEntity> spends = SPEND_DAO.findAllSpendsByUsername(category.getUsername());
        spends.stream()
                .filter(spendEntity -> spendEntity.getCategory().getId().equals(category.getId()))
                .forEach(SPEND_DAO::deleteSpend);
        CATEGORY_DAO.deleteCategory(category);
    }
}
