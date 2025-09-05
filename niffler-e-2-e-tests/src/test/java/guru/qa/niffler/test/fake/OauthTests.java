package guru.qa.niffler.test.fake;

import guru.qa.niffler.api.AuthApiClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.userdata.UserJson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OauthTests {

    private static final Config CFG = Config.getInstance();
    private final AuthApiClient authApiClient = new AuthApiClient();

    @Test
    @User(friends = 1)
    @ApiLogin
    public void testOauth(@Token String token, UserJson user) {
        System.out.println(user);
        assertNotNull(token);
    }
}
