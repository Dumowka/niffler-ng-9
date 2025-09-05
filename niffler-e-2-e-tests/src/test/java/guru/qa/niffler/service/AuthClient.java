package guru.qa.niffler.service;

import guru.qa.niffler.model.userdata.UserJson;

import javax.annotation.Nonnull;

public interface AuthClient {
    @Nonnull
    UserJson register(String username, String password, String passwordSubmit);

    String login(String username, String password);
}
