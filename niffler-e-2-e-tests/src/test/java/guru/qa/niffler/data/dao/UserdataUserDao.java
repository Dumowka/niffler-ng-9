package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.Optional;

public interface UserdataUserDao {
    UserEntity createUser(UserEntity user);
    Optional<UserEntity> findUserById(Long id);
    Optional<UserEntity> findUserByUsername(String username);
    void deleteUser(UserEntity user);
}
