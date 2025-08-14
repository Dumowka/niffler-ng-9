package guru.qa.niffler.api;

import guru.qa.niffler.model.userdata.UserJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface UserdataApi {

    @GET("internal/users/current")
    Call<UserJson> currentUser(@Query("username") String username);
}
