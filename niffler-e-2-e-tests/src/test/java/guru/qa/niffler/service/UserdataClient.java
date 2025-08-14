package guru.qa.niffler.service;

import guru.qa.niffler.model.userdata.UserJson;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UserdataClient {

    @Nullable
    UserJson currentUser(String username);
}
