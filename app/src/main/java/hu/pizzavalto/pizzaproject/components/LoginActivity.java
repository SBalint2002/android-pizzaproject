package hu.pizzavalto.pizzaproject.components;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.textfield.TextInputEditText;

import hu.pizzavalto.pizzaproject.R;
import hu.pizzavalto.pizzaproject.auth.JwtResponse;
import hu.pizzavalto.pizzaproject.model.User;
import hu.pizzavalto.pizzaproject.retrofit.NetworkService;
import hu.pizzavalto.pizzaproject.retrofit.UserApi;
import hu.pizzavalto.pizzaproject.sharedpreferences.TokenUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView register;
    private TextInputEditText emailText, passwordText;
    private Button loginButton;

    @SuppressLint({"SourceLockedOrientationActivity", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        register.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        loginButton.setOnClickListener(view -> {
            String email = String.valueOf(emailText.getText());
            String password = String.valueOf(passwordText.getText());

            //Mindegyik ki van e töltve
            if (email.isEmpty() || password.isEmpty()) {

                //Custom Toast
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast,
                        findViewById(R.id.toast_layout_root));
                TextView text = layout.findViewById(R.id.text);
                text.setText("Minden mezőt ki kell tölteni!");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

                return;
            }

            //User típusba rakás
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);

            //Api request-ek kezelésére
            UserApi userApi = new NetworkService().getRetrofit().create(UserApi.class);
            userApi.loginUser(user)
                    .enqueue(new Callback<JwtResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<JwtResponse> call, @NonNull Response<JwtResponse> response) {
                            //Ha 403-as hibakódot küld vissza akkor ha nem sikerül belépni
                            System.out.println("onResponse: " + response.code());
                            if (response.code() == 403) {

                                //Custom Toast
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.toast,
                                        findViewById(R.id.toast_layout_root));
                                TextView text = layout.findViewById(R.id.text);
                                text.setText("Nem megfelelő email és jelszó páros!");
                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();



                            } else if (response.code() == 200) {
                                JwtResponse jwtResponse = response.body();
                                String accessToken = null;
                                if (jwtResponse != null) {
                                    accessToken = jwtResponse.getAccessToken();
                                }
                                String refreshToken = null;
                                if (jwtResponse != null) {
                                    refreshToken = jwtResponse.getRefreshToken();
                                }

                                //SharedPreferencies
                                TokenUtils tokenUtils = new TokenUtils(LoginActivity.this);
                                tokenUtils.saveAccessToken(accessToken);
                                tokenUtils.setRefreshToken(refreshToken);

                                //Custom Toast
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.toast,
                                        findViewById(R.id.toast_layout_root));
                                TextView text = layout.findViewById(R.id.text);
                                text.setText("Sikeres bejelentkezés!");
                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();

                                startActivity(new Intent(LoginActivity.this, MainPage.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            } else {
                                System.out.println(response.code());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JwtResponse> call, @NonNull Throwable t) {
                            Toast.makeText(LoginActivity.this, "Ismeretlen hiba", Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }

    private void init(){
        register = findViewById(R.id.RegisterTextView);
        emailText = findViewById(R.id.email_input);
        passwordText = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.loginButton);
    }
}