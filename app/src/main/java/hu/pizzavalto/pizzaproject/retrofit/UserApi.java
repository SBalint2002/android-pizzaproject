package hu.pizzavalto.pizzaproject.retrofit;

import java.util.List;
import hu.pizzavalto.pizzaproject.auth.JwtResponse;
import hu.pizzavalto.pizzaproject.model.Pizza;
import hu.pizzavalto.pizzaproject.auth.RefreshRequest;
import hu.pizzavalto.pizzaproject.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface UserApi {

    @GET("/user/data")
    Call<User> getUserInformation(@Header("Authorization") String token);

    @POST("/user/register")
    Call<JwtResponse> registerUser(@Body User user);

    @POST("/user/login")
    Call<JwtResponse> loginUser(@Body User user);

    @POST("/user/refresh")
    Call<JwtResponse> refreshToken(@Body RefreshRequest refreshRequest);

    @GET("/pizza/get-all")
    Call<List<Pizza>> getAllPizzas();
}
