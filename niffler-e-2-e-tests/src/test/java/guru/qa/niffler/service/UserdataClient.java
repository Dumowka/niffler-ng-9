package guru.qa.niffler.service;

import guru.qa.niffler.api.UserdataApiClient;
import guru.qa.niffler.model.userdata.UserJson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface UserdataClient {

    @Nonnull
    static UserdataClient getInstance() {
        return new UserdataApiClient();
    }

    @Nullable
    UserJson currentUser(String username);

    @Nonnull
    List<UserJson> allUsers(String username, @Nullable String searchQuery);
}
