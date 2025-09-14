package guru.qa.niffler.test.api;

import guru.qa.niffler.api.AuthApiClient;
import guru.qa.niffler.service.AuthClient;
import guru.qa.niffler.utils.OauthUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OauthTests {
    @Test
    public void testOauth() throws Exception {
        String codeVerifier = OauthUtils.generateCodeVerifier();
        String codeChallenge = OauthUtils.generateCodeChallenge(codeVerifier);

        AuthClient client = new AuthApiClient();
        client.authorize(codeChallenge);
        String code = client.login("duck", "12345");
        String token = client.token(code, codeVerifier);
        assertNotNull(token);
    }
}
