package guru.qa.niffler.test;

import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.impl.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserDbTest {
    private final UsersDbClient usersDbClient = new UsersDbClient();

    private static String name = "a21";

    @Test
    void checkCreatingUser() {
        UserJson user = usersDbClient.createUser(name, "12345");
        System.out.println(user);
    }

    @Test
    void checkAuthUserFindByNameAndUpdateUser() {
        AuthUserJson authUserJson = usersDbClient.getAuthUserByName(name).get();
        AuthUserJson updatedUserJson = new AuthUserJson(
                authUserJson.getId(),
                authUserJson.getUsername(),
                authUserJson.getPassword(),
                authUserJson.getEnabled(),
                false,
                authUserJson.getAccountNonLocked(),
                authUserJson.getCredentialsNonExpired(),
                authUserJson.getAuthorities()
        );
        AuthUserJson receivedUser = usersDbClient.update(updatedUserJson);
        assertEquals(updatedUserJson, receivedUser);
    }

    @Test
    void checkFindAuthUserById() {
        AuthUserJson userByName = usersDbClient.getAuthUserByName(name).get();
        AuthUserJson userById = usersDbClient.getAuthUserById(userByName.getId()).get();
        assertEquals(userByName, userById);
    }

    @Test
    void checkFindAllAuthUser() {
        assertFalse(usersDbClient.findAll().isEmpty());
    }

    @Test
    void checkUserdataFindByNameAndUpdate() {
        String newFirstName = RandomDataUtils.randomName();
        UserJson user = usersDbClient.getUserByName(name).get();
        UserJson updatedUser = new UserJson(
                user.id(),
                user.username(),
                user.currency(),
                newFirstName,
                user.surname(),
                user.fullname(),
                user.photo(),
                user.photoSmall(),
                null,
                null
        );
        UserJson receivedUser = usersDbClient.update(updatedUser);
        System.out.println(receivedUser);
        assertEquals(updatedUser, receivedUser);
    }

    @Test
    void checkUserDataFindById() {
        UserJson userByName = usersDbClient.getUserByName(name).get();
        UserJson userById = usersDbClient.getUserById(userByName.id()).get();
        assertEquals(userByName, userById);
    }

    @Test
    void checkIncomeInvitation() {
        UserJson requester = usersDbClient.createUser(RandomDataUtils.randomUsername(), "12345");
        usersDbClient.addIncomeInvitation(requester, 1);
    }

    @Test
    void checkOutcomeInvitation() {
        UserJson requester = usersDbClient.createUser(RandomDataUtils.randomUsername(), "12345");
        usersDbClient.addOutcomeInvitation(requester, 1);
    }

    @Test
    void checkAddFriend() {
        UserJson requester = usersDbClient.createUser(RandomDataUtils.randomUsername(), "12345");
        UserJson addressee = usersDbClient.createUser(RandomDataUtils.randomUsername(), "12345");
        usersDbClient.addFriend(requester, addressee);
    }

    @Test
    void removeUser() {
        AuthUserJson userJson = usersDbClient.getAuthUserByName(name).get();
        usersDbClient.removeUser(userJson);
    }
}
