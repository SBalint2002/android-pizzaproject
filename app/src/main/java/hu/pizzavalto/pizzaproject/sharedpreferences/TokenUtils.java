package hu.pizzavalto.pizzaproject.sharedpreferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import hu.pizzavalto.pizzaproject.R;
import hu.pizzavalto.pizzaproject.auth.JwtResponse;
import hu.pizzavalto.pizzaproject.components.LoginActivity;
import hu.pizzavalto.pizzaproject.components.MainPage;
import hu.pizzavalto.pizzaproject.retrofit.ApiService;
import retrofit2.Callback;

public class TokenUtils {
    private static final String SHARED_PREFS_NAME = "gqgoWOpCHiiN79DAVdIlRCW2HjcOr6iL9X33EGYR";
    private static final String ACCESS_TOKEN_KEY = "Al5xlvgxlFtiezTS3dnTedrGxDAX9v8UvJw5j8lT";
    private static final String REFRESH_TOKEN_KEY = "RcA9tu0SORJfqQkFjZvITMzFhPph3V5DVSLXaIn9";

    private final SharedPreferences sharedPreferences;

    public TokenUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveAccessToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACCESS_TOKEN_KEY, token);
        editor.apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(ACCESS_TOKEN_KEY, null);
    }

    public void setRefreshToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(REFRESH_TOKEN_KEY, token);
        editor.apply();
    }

    public String getRefreshToken() {
        return sharedPreferences.getString(REFRESH_TOKEN_KEY, null);
    }

    public void clearTokens() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ACCESS_TOKEN_KEY);
        editor.remove(REFRESH_TOKEN_KEY);
        editor.apply();
    }

    public static void refreshUserToken(TokenUtils tokenUtils, ApiService apiService, Callback<JwtResponse> callback) {
        String refreshToken = tokenUtils.getRefreshToken();
        if (refreshToken != null) {
            apiService.refreshToken(refreshToken).enqueue(callback);
        }
    }
}

