package guru.qa.niffler.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface AuthApi {

    @GET("/register")
    Call<Void> requestRegisterForm();

    @POST("/register")
    Call<Void> register(
            @Query("username") String username,
            @Query("password") String password,
            @Query("passwordSubmit") String passwordSubmit,
            @Query("_csrf") String token
    );
}
