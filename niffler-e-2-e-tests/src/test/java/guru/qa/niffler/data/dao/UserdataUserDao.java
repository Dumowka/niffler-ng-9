package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserdataUserDao {
    UserEntity createUser(UserEntity user);

    Optional<UserEntity> findUserById(UUID id);

    Optional<UserEntity> findUserByUsername(String username);

    List<UserEntity> findAll();

    void deleteUser(UserEntity user);
}
