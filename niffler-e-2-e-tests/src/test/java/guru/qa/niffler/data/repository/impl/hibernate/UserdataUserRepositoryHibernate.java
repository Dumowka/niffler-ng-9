package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class UserdataUserRepositoryHibernate implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.userdataJdbcUrl());

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        entityManager.joinTransaction();
        entityManager.persist(userEntity);
        return userEntity;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(UserEntity.class, id)
        );
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try {
            return Optional.of(
                    entityManager.createQuery("SELECT u FROM UserEntity u WHERE u.username =: username", UserEntity.class)
                    .setParameter("username", username)
                    .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<UserEntity> findAll() {
        // TODO Реализовать в дз 6.2
        return List.of();
    }

    @Override
    public void deleteUser(UserEntity user) {
        // TODO Реализовать в дз 6.2
    }

    @Override
    public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        addressee.addFriends(FriendshipStatus.PENDING, requester);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        entityManager.joinTransaction();
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
    }
}
