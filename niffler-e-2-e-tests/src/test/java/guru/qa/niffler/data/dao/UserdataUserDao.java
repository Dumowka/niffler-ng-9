package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.userdata.UserdataUserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserDao {
    UserdataUserEntity createUser(UserdataUserEntity user);
    Optional<UserdataUserEntity> findUserById(UUID id);
    Optional<UserdataUserEntity> findUserByUsername(String username);
    void deleteUser(UserdataUserEntity user);
}
