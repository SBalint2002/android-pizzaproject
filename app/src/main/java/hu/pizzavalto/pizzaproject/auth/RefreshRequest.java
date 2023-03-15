package hu.pizzavalto.pizzaproject.auth;

public class RefreshRequest {
    private String refreshToken;

    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public RefreshRequest() {

    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            throw new NullPointerException();
        } else {
            this.refreshToken = refreshToken;
        }
    }
}