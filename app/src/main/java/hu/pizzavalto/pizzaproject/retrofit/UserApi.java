package hu.pizzavalto.pizzaproject.retrofit;

import java.util.List;

import hu.pizzavalto.pizzaproject.auth.JwtResponse;
import hu.pizzavalto.pizzaproject.model.Pizza;
import hu.pizzavalto.pizzaproject.auth.RefreshRequest;
import hu.pizzavalto.pizzaproject.model.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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

    @PUT("/user/{userId}")
    Call<ResponseBody> saveUser(@Header("Authorization") String token, @Path("userId") Long userId, @Body User user);

}
