package guru.qa.niffler.service;

import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.userdata.UserJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsersClient {
    UserJson createUser(String username, String password);

    AuthUserJson update(AuthUserJson authUserJson);

    Optional<AuthUserJson> getAuthUserById(UUID id);

    Optional<AuthUserJson> getAuthUserByName(String username);

    List<AuthUserJson> findAll();

    UserJson update(UserJson userJson);

    Optional<UserJson> getUserById(UUID id);

    Optional<UserJson> getUserByName(String username);

    List<UserJson> addIncomeInvitation(UserJson targetUser, int count);

    List<UserJson> addOutcomeInvitation(UserJson targetUser, int count);

    void removeUser(AuthUserJson authUserJson);

    List<UserJson> addFriend(UserJson targetUser, int count);

    void addFriend(UserJson requester, UserJson addressee);
}
