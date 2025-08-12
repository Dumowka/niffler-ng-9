package guru.qa.niffler.service;

import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.userdata.UserJson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UsersClient {
    @Nullable UserJson createUser(String username, String password);

    @Nullable AuthUserJson update(AuthUserJson authUserJson);

    @Nonnull Optional<AuthUserJson> getAuthUserById(UUID id);

    @Nonnull Optional<AuthUserJson> getAuthUserByName(String username);

    @Nonnull List<AuthUserJson> findAll();

    @Nullable UserJson update(UserJson userJson);

    @Nonnull Optional<UserJson> getUserById(UUID id);

    @Nonnull Optional<UserJson> getUserByName(String username);

    @Nonnull List<UserJson> addIncomeInvitation(UserJson targetUser, int count);

    @Nonnull List<UserJson> addOutcomeInvitation(UserJson targetUser, int count);

    void removeUser(AuthUserJson authUserJson);

    @Nonnull List<UserJson> addFriend(UserJson targetUser, int count);

    void addFriend(UserJson requester, UserJson addressee);
}
