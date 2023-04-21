package hu.pizzavalto.pizzaproject.auth;

import com.google.gson.annotations.SerializedName;

/**
 * Kérésekből érkező jwt válasz amely tartalmazza a felhasználóhoz tartozó access token-t és refresh token-t.
 */
public class JwtResponse {
    /**
     * Felhasználóhoz tartozó access token.
     */
    @SerializedName("accessToken")
    private final String accessToken;

    /**
     * Felhasználóhoz tartozó refresh token.
     */
    @SerializedName("refreshToken")
    private final String refreshToken;

    /**
     * Jwt válasz konstruktora
     * @param accessToken access token.
     * @param refreshToken refresh token;
     */
    public JwtResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    /**
     * Getter metódus amely vissza adja a felhasználóhoz tartozó access token-t.
     *
     * @return felhasználóhoz tartozó access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Getter metódus amely vissza adja a felhasználóhoz tartozó refresh token-t.
     *
     * @return felhasználóhoz tartozó refresh token
     */
    public String getRefreshToken() {
        return refreshToken;
    }
}
