package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class UserdataUserRepositoryHibernate implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.userdataJdbcUrl());

    @Override
    public UserEntity create(UserEntity userEntity) {
        entityManager.joinTransaction();
        entityManager.persist(userEntity);
        return userEntity;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        UserEntity entity = entityManager.find(UserEntity.class, id);
        return entity == null ? Optional.empty() : Optional.of(entity);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        UserEntity entity = entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.username =: username", UserEntity.class)
                .setParameter("username", username)
                .getSingleResult();
        return entity == null ? Optional.empty() : Optional.of(entity);
    }

    @Override
    public List<UserEntity> findAll() {
        return entityManager.createQuery("SELECT u FROM UserEntity u", UserEntity.class).getResultList();
    }

    @Override
    public UserEntity update(UserEntity user) {
        entityManager.joinTransaction();
        entityManager.merge(user);
        return user;
    }

    @Override
    public void addInvitation(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        addressee.addFriends(FriendshipStatus.PENDING, requester);
        entityManager.merge(requester);
        entityManager.merge(addressee);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
        entityManager.merge(requester);
        entityManager.merge(addressee);
    }

    @Override
    public void remove(UserEntity user) {
        entityManager.joinTransaction();
        if (!entityManager.contains(user)) {
            user = entityManager.merge(user);
        }
        entityManager.remove(user);
    }
}
