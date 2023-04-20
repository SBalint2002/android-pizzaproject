package hu.pizzavalto.pizzaproject.auth;

import com.google.gson.annotations.SerializedName;

public class JwtResponse {
    @SerializedName("accessToken")
    private final String accessToken;

    @SerializedName("refreshToken")
    private final String refreshToken;

    public JwtResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
