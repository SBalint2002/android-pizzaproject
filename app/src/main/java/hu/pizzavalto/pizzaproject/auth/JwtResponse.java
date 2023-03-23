package hu.pizzavalto.pizzaproject.auth;

import com.google.gson.annotations.SerializedName;

public class JwtResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("accessToken")
    private String accessToken;

    @SerializedName("refreshToken")
    private String refreshToken;

    public JwtResponse(String status, String accessToken, String refreshToken) {
        this.status = status;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
