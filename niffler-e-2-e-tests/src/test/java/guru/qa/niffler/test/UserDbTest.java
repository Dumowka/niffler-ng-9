package guru.qa.niffler.test;

import guru.qa.niffler.jupiter.extension.ClientResolver;
import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(ClientResolver.class)
public class UserDbTest {

    private UsersClient usersClient;

    private static String name = "a24";

    @Test
    void checkCreatingUser() {
        UserJson user = usersClient.createUser(name, "12345");
        System.out.println(user);
    }

    @Test
    void checkAuthUserFindByNameAndUpdateUser() {
        AuthUserJson authUserJson = usersClient.getAuthUserByName(name).get();
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
        AuthUserJson receivedUser = usersClient.update(updatedUserJson);
        assertEquals(updatedUserJson, receivedUser);
    }

    @Test
    void checkFindAuthUserById() {
        AuthUserJson userByName = usersClient.getAuthUserByName(name).get();
        AuthUserJson userById = usersClient.getAuthUserById(userByName.getId()).get();
        assertEquals(userByName, userById);
    }

    @Test
    void checkFindAllAuthUser() {
        assertFalse(usersClient.findAll().isEmpty());
    }

    @Test
    void checkUserdataFindByNameAndUpdate() {
        String newFirstName = RandomDataUtils.randomName();
        UserJson user = usersClient.getUserByName(name).get();
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
        UserJson receivedUser = usersClient.update(updatedUser);
        System.out.println(receivedUser);
        assertEquals(updatedUser, receivedUser);
    }

    @Test
    void checkUserDataFindById() {
        UserJson userByName = usersClient.getUserByName(name).get();
        UserJson userById = usersClient.getUserById(userByName.id()).get();
        assertEquals(userByName, userById);
    }

    @Test
    void checkIncomeInvitation() {
        UserJson requester = usersClient.createUser(RandomDataUtils.randomUsername(), "12345");
        usersClient.addIncomeInvitation(requester, 1);
    }

    @Test
    void checkOutcomeInvitation() {
        UserJson requester = usersClient.createUser(RandomDataUtils.randomUsername(), "12345");
        usersClient.addOutcomeInvitation(requester, 1);
    }

    @Test
    void checkAddFriend() {
        UserJson requester = usersClient.createUser(RandomDataUtils.randomUsername(), "12345");
        UserJson addressee = usersClient.createUser(RandomDataUtils.randomUsername(), "12345");
        usersClient.addFriend(requester, addressee);
    }

    @Test
    void removeUser() {
        AuthUserJson userJson = usersClient.getAuthUserByName(name).get();
        usersClient.removeUser(userJson);
    }
}
