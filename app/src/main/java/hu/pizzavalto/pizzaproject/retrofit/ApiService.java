package hu.pizzavalto.pizzaproject.retrofit;

import java.util.List;

import hu.pizzavalto.pizzaproject.auth.JwtResponse;
import hu.pizzavalto.pizzaproject.model.Order;
import hu.pizzavalto.pizzaproject.model.OrderDto;
import hu.pizzavalto.pizzaproject.model.Pizza;
import hu.pizzavalto.pizzaproject.model.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * ApiService interface, mely a backend által biztosított API végpontokat definiálja.
 */
public interface ApiService {

    /**
     * Regisztrál egy felhasználót az adatbázisban.
     *
     * @param user a regisztrálni kívánt felhasználó objektuma.
     * @return JwtResponse válasz amely tartalmazza a felhasználóhoz tartozó acceess token-t és refresh token-t
     */
    @POST("/auth/register")
    Call<JwtResponse> registerUser(@Body User user);

    /**
     * Bejelentkezteti a felhasználót egy létező fiókkal az alkalmazásba.
     *
     * @param user a bejelentkezni kívánt felhasználó objektuma
     * @return JwtResponse válasz amely tartalmazza a felhasználóhoz tartozó acceess token-t és refresh token-t
     */
    @POST("/auth/login")
    Call<JwtResponse> loginUser(@Body User user);

    /**
     * Lejárt access token esetén kérést küld a szervernek a megújítás érdekében.
     *
     * @param refreshToken a felhasználóhoz tartozó refresh token.
     * @return JwtResponse válasz amely tartalmazza a felhasználóhoz tartozó acceess token-t és refresh token-t
     */
    @POST("/auth/refresh")
    Call<JwtResponse> refreshToken(@Body String refreshToken);

    /**
     * Lekéri a felhasználóhoz tartozó adatokat.
     *
     * @param accessToken a felhasználóhoz tartozó access token.
     * @return felhasználó objektum.
     */
    @GET("/user/data")
    Call<User> getUserInformation(@Header("Authorization") String accessToken);

    /**
     * Felhasználó saját adatainak módosítása. Más id-t küld akkor a backend nem engedélyezi a változtatást.
     *
     * @param accessToken a felhasználóhoz tartozó access token.
     * @param userId felhasználóhoz tartozó azonosító szám (index).
     * @param user felhasználó objektum.
     * @return válasz üzenet a kérés eredményéről.
     */
    @PUT("/user/{userId}")
    Call<ResponseBody> saveUser(@Header("Authorization") String accessToken, @Path("userId") Long userId, @Body User user);

    /**
     * Adatbázisban található pizza objektumok listázása.
     *
     * @return pizza típusú lista.
     */
    @GET("/pizza/get-all")
    Call<List<Pizza>> getAllPizzas();

    /**
     * Rendelés leadása rendelési adatok átadásával.
     *
     * @param accessToken a felhasználóhoz tartozó access token.
     * @param orderDto rendelés objektum.
     * @return válasz üzenet a kérés eredményéről.
     */
    @POST("/order/add-order")
    Call<ResponseBody> addOrder(@Header("Authorization") String accessToken, @Body OrderDto orderDto);

    /**
     * Felhasználóhoz tartozó rendelések listázása. (id-t a tokenből szedi ki a  backend)
     *
     * @param accessToken a felhasználóhoz tartozó access token.
     * @return rendelés típusú lista.
     */
    @GET("/order/get-orders")
    Call<List<Order>> getOrders(@Header("Authorization") String accessToken);
}
