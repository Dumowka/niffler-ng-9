package guru.qa.niffler.test.api;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UserdataClient;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserApiTests {

    private final UserdataClient userdataClient = UserdataClient.getInstance();

    @Tag("first")
    @Test
    @User
    void firstTest(UserJson user) {
        List<UserJson> users = userdataClient.allUsers(user.username(), null);
        assertTrue(users.isEmpty());
    }

    @Test
    void parallelTestInClass1() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void parallelTestInClass2() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void parallelTestInClass3() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Tag("last")
    @Test
    @User
    void lastTest(UserJson user) {
        List<UserJson> users = userdataClient.allUsers(user.username(), null);
        assertFalse(users.isEmpty());
    }
}
