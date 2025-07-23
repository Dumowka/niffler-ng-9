package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserdataUserRepository {
    UserEntity createUser(UserEntity user);
    Optional<UserEntity> findById(UUID id);
    Optional<UserEntity> findByUsername(String username);
    List<UserEntity> findAll();
    void deleteUser(UserEntity user);

    void addIncomeInvitation(UserEntity requester, UserEntity addressee);
    default void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
        addIncomeInvitation(addressee, requester);
    }
    void addFriend(UserEntity requester, UserEntity addressee);
}
