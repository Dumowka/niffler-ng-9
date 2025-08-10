package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserdataUserRepository {
    @Nonnull UserEntity create(UserEntity user);

    @Nonnull Optional<UserEntity> findById(UUID id);

    @Nonnull Optional<UserEntity> findByUsername(String username);

    @Nonnull List<UserEntity> findAll();

    @Nonnull UserEntity update(UserEntity user);

    void addInvitation(UserEntity requester, UserEntity addressee);

    void addFriend(UserEntity requester, UserEntity addressee);

    void remove(UserEntity user);
}
