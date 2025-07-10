package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userdata.UserdataUserEntity;

import java.util.Optional;

public interface UserdataUserDao {
    UserdataUserEntity createUser(UserdataUserEntity user);
    Optional<UserdataUserEntity> findUserById(Long id);
    Optional<UserdataUserEntity> findUserByUsername(String username);
    void deleteUser(UserdataUserEntity user);
}
