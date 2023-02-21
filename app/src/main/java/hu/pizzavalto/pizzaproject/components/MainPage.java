package hu.pizzavalto.pizzaproject.components;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import hu.pizzavalto.pizzaproject.R;
import hu.pizzavalto.pizzaproject.model.JwtResponse;
import hu.pizzavalto.pizzaproject.model.User;
import hu.pizzavalto.pizzaproject.retrofit.NetworkService;
import hu.pizzavalto.pizzaproject.retrofit.UserApi;
import hu.pizzavalto.pizzaproject.sharedpreferences.TokenUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPage extends AppCompatActivity {
    private TextView id, first_name, last_name, email, admin;
    private Button logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        init();
        getUserInformation();

        logOutButton.setOnClickListener(view -> {
            TokenUtils tokenUtils = new TokenUtils(MainPage.this);
            tokenUtils.clearTokens();
            navigateToLoginActivity();
        });
    }

    private void init() {
        id = findViewById(R.id.id);
        first_name = findViewById(R.id.firstname);
        last_name = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        admin = findViewById(R.id.admin);

        logOutButton = findViewById(R.id.LogOutButton);
    }



    public void getUserInformation() {
        TokenUtils tokenUtils = new TokenUtils(MainPage.this);
        String accessToken = tokenUtils.getAccessToken();

        //Api request-ek kezelésére
        NetworkService networkService = new NetworkService();
        UserApi userApi = networkService.getRetrofit().create(UserApi.class);
        if (accessToken != null) {
            userApi.getUserInformation("Bearer " + accessToken)
                    .enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            if (response.isSuccessful()) {
                                //Ha sikeres a kérés és kapok egy user típusú json-t

                                System.out.println("Sikeres lekérés");

                                User user = response.body();

                                System.out.println(response.body());

                                id.setText(String.valueOf(user.getId()));
                                first_name.setText(user.getFirst_name());
                                last_name.setText(user.getLast_name());
                                email.setText(user.getEmail());
                                admin.setText(String.valueOf(user.isAdmin()));
                            } else if (response.code() == 451) {
                                //Ha visszakapom azt hogy lejárt a token akkor kérek egy újat
                                String refreshToken = tokenUtils.getRefreshToken();
                                if (refreshToken != null) {
                                    TokenUtils.refreshUserToken(tokenUtils, userApi, new Callback<JwtResponse>() {
                                        @Override
                                        public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                                            if (response.isSuccessful()) {
                                                JwtResponse jwtResponse = response.body();
                                                tokenUtils.saveAccessToken(jwtResponse.getJwttoken());
                                                tokenUtils.setRefreshToken(jwtResponse.getRefreshToken());
                                                getUserInformation();
                                            }else {
                                                navigateToLoginActivity();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<JwtResponse> call, Throwable t) {
                                            navigateToLoginActivity();
                                        }
                                    });
                                }else{
                                    //hiányzó refresh token
                                    System.out.println("Hiányzó refreshtoken");
                                    navigateToLoginActivity();
                                }
                            } else if (response.code() == 401) {
                                //unauthorized
                                System.out.println("Lejárt refresh");
                                navigateToLoginActivity();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            //network error
                            System.out.println(t);
                            navigateToLoginActivity();
                        }
                    });
        } else {
            //Refreshtoken Null
            navigateToLoginActivity();
        }
    }

    private void navigateToLoginActivity() {
        //Intent
        startActivity(new Intent(MainPage.this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}