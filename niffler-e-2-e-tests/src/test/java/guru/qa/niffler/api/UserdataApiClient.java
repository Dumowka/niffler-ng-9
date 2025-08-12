package guru.qa.niffler.api;

import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.service.UserdataClient;
import retrofit2.Response;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class UserdataApiClient extends RestClient implements UserdataClient {

    private final UserdataApi userdataApi;

    public UserdataApiClient() {
        super(CFG.userdataUrl());
        userdataApi = create(UserdataApi.class);
    }
    
    @Override
    public @Nullable UserJson currentUser(String username) {
        final Response<UserJson> response;
        try {
            response = userdataApi.currentUser(username).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }
}
