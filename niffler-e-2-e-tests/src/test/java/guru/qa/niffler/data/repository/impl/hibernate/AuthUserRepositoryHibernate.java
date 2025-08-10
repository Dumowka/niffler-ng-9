package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryHibernate implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.authJdbcUrl());

    @Override
    public @Nonnull AuthUserEntity create(AuthUserEntity authUserEntity) {
        entityManager.joinTransaction();
        entityManager.persist(authUserEntity);
        return authUserEntity;
    }

    @Override
    public @Nonnull AuthUserEntity update(AuthUserEntity user) {
        entityManager.joinTransaction();
        entityManager.merge(user);
        return user;
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(AuthUserEntity.class, id)
        );
    }

    @Override
    public @Nonnull Optional<AuthUserEntity> findByUsername(String username) {
        try {
            return Optional.of(
                    entityManager.createQuery("SELECT u FROM AuthUserEntity u WHERE u.username =: username", AuthUserEntity.class)
                    .setParameter("username", username)
                    .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public @Nonnull List<AuthUserEntity> findAll() {
        return entityManager.createQuery("SELECT u FROM AuthUserEntity u", AuthUserEntity.class).getResultList();
    }

    @Override
    public void remove(AuthUserEntity user) {
        entityManager.joinTransaction();
        if (!entityManager.contains(user)) {
            user = entityManager.merge(user);
        }
        entityManager.remove(user);
    }
}
