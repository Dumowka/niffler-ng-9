package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.core.CodeInterceptor;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.AuthClient;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.utils.OauthUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.StopWatch;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@ParametersAreNonnullByDefault
public class AuthApiClient extends RestClient implements AuthClient {

    private final AuthApi authApi;
    private final UserdataApiClient userdataApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
        authApi = create(AuthApi.class);
        userdataApi = new UserdataApiClient();
    }

    @Override
    public @Nonnull UserJson register(String username, String password, String passwordSubmit) {
        try {
            authApi.requestRegisterForm().execute();
            authApi.register(
                    username,
                    password,
                    passwordSubmit,
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StopWatch sw = StopWatch.createStarted();
        while (sw.getTime(TimeUnit.SECONDS) < 30) {
            UserJson userJson;
            userJson = userdataApi.currentUser(username);
            if (userJson != null && userJson.id() != null) {
                return userJson;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        throw new AssertionError("Timed out waiting for register");
    }

    @SneakyThrows
    public String login(String username, String password) {
        final String codeVerifier = OauthUtils.generateCodeVerifier();
        final String codeChallenge = OauthUtils.generateCodeChallenge(codeVerifier);
        final String clientId = "client";
        final String redirectUri = CFG.frontUrl() + "authorized";

        authApi.authorize(
                "code",
                clientId,
                "openid",
                redirectUri,
                codeChallenge,
                "S256"
        ).execute();

        authApi.login(
                username,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();

        Response<JsonNode> tokenResponse = authApi.token(
                ApiLoginExtension.getCode(),
                redirectUri,
                codeVerifier,
                "authorization_code",
                clientId
        ).execute();

        return tokenResponse.body().get("id_token").asText();
    }
}