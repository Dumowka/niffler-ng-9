package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.EntityManager;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class SpendRepositoryHibernate implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.spendJdbcUrl());

    @Override
    public @Nonnull SpendEntity create(SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.persist(spend);
        return spend;
    }

    @Override
    public @Nonnull SpendEntity update(SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.merge(spend);
        return spend;
    }

    @Override
    public @Nonnull CategoryEntity createCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.persist(category);
        return category;
    }

    @Override
    public @Nonnull CategoryEntity updateCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.merge(category);
        return category;
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryById(UUID id) {
        CategoryEntity entity = entityManager.find(CategoryEntity.class, id);
        return entity == null ? Optional.empty() : Optional.of(entity);
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String name) {
        CategoryEntity entity = entityManager.createQuery("SELECT c FROM CategoryEntity c WHERE c.username =: username AND c.name =: name", CategoryEntity.class)
                .setParameter("username", username)
                .setParameter("name", name)
                .getSingleResult();
        return entity == null ? Optional.empty() : Optional.of(entity);
    }

    @Override
    public @Nonnull Optional<SpendEntity> findById(UUID id) {
        SpendEntity entity = entityManager.find(SpendEntity.class, id);
        return entity == null ? Optional.empty() : Optional.of(entity);
    }

    @Override
    public @Nonnull Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        SpendEntity entity = entityManager.createQuery("SELECT s FROM SpendEntity s WHERE s.username =: username AND s.description =: description", SpendEntity.class)
                .setParameter("username", username)
                .setParameter("description", description)
                .getSingleResult();
        return entity == null ? Optional.empty() : Optional.of(entity);
    }

    @Override
    public void remove(SpendEntity spend) {
        entityManager.joinTransaction();
        if (!entityManager.contains(spend)) {
            spend = entityManager.merge(spend);
        }
        entityManager.remove(spend);
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        if (!entityManager.contains(category)) {
            category = entityManager.merge(category);
        }
        entityManager.remove(category);
    }
}
