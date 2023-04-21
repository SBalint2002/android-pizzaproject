package hu.pizzavalto.pizzaproject.sharedpreferences;

import android.content.Context;
import android.content.SharedPreferences;

import hu.pizzavalto.pizzaproject.auth.JwtResponse;
import hu.pizzavalto.pizzaproject.retrofit.ApiService;
import retrofit2.Callback;

/**
 * Tokenekkel kapcsolatos műveleteket megvalósító osztály
 */
public class TokenUtils {
    /**
     * A shared preferences neve
     */
    private static final String SHARED_PREFS_NAME = "gqgoWOpCHiiN79DAVdIlRCW2HjcOr6iL9X33EGYR";
    /**
     * Az access token kulcsa a shared preferences-ben
     */
    private static final String ACCESS_TOKEN_KEY = "Al5xlvgxlFtiezTS3dnTedrGxDAX9v8UvJw5j8lT";
    /**
     * A refresh token kulcsa a shared preferences-ben
     */
    private static final String REFRESH_TOKEN_KEY = "RcA9tu0SORJfqQkFjZvITMzFhPph3V5DVSLXaIn9";

    /**
     * A shared preferences
     */
    private final SharedPreferences sharedPreferences;

    /**
     * Konstruktor
     *
     * @param context Az alkalmazás context-je
     */
    public TokenUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Access token mentése
     *
     * @param token A mentendő token
     */
    public void saveAccessToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACCESS_TOKEN_KEY, token);
        editor.apply();
    }

    /**
     * Access token lekérése
     *
     * @return Az access token, vagy null, ha nincs elmentve
     */
    public String getAccessToken() {
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null);
    }

    /**
     * Refresh token mentése
     *
     * @param token A mentendő token
     */
    public void setRefreshToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(REFRESH_TOKEN_KEY, token);
        editor.apply();
    }

    /**
     * Refresh token lekérése
     *
     * @return A refresh token, vagy null, ha nincs elmentve
     */
    public String getRefreshToken() {
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null);
    }

    /**
     * Tokenek törlése
     */
    public void clearTokens() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ACCESS_TOKEN_KEY);
        editor.remove(REFRESH_TOKEN_KEY);
        editor.apply();
    }

    /**
     * Felhasználói token frissítése
     *
     * @param tokenUtils A TokenUtils osztály egy példánya
     * @param apiService Az ApiService egy példánya
     * @param callback   A callback függvény
     */
    public static void refreshUserToken(TokenUtils tokenUtils, ApiService apiService, Callback<JwtResponse> callback) {
        String refreshToken = tokenUtils.getRefreshToken();
        if (refreshToken != null) {
            apiService.refreshToken(refreshToken).enqueue(callback);
        }
    }
}

