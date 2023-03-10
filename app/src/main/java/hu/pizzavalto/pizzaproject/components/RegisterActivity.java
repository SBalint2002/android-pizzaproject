package hu.pizzavalto.pizzaproject.components;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import hu.pizzavalto.pizzaproject.R;
import hu.pizzavalto.pizzaproject.model.JwtResponse;
import hu.pizzavalto.pizzaproject.model.User;
import hu.pizzavalto.pizzaproject.retrofit.NetworkService;
import hu.pizzavalto.pizzaproject.retrofit.UserApi;
import hu.pizzavalto.pizzaproject.sharedpreferences.TokenUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private MaterialButton registerButton;
    private TextView login;
    TextInputEditText lastnameText, firstnameText, emailText, passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_register);

        init();

        login.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });


        registerButton.setOnClickListener(view -> {
            //Input mezők adatainak elmentése változókba
            String lastname = String.valueOf(lastnameText.getText());
            String firstname = String.valueOf(firstnameText.getText());
            String email = String.valueOf(emailText.getText());
            String password = String.valueOf(passwordText.getText());

            //Mindegyik ki van e töltve
            if (lastname.isEmpty() || firstname.isEmpty() || email.isEmpty() || password.isEmpty()) {

                //Custom Toast
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast,
                        (ViewGroup) findViewById(R.id.toast_layout_root));
                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText("Minden mezőt ki kell tölteni!");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

                return;
            }

            if (TextUtils.isEmpty(lastname)){
                System.out.println("rossz vezetéknév");
                return;
            }

            if (TextUtils.isEmpty(firstname)){
                System.out.println("rossz keresztnév");
                return;
            }

            if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                System.out.println("rossz email");
                return;
            }

            if (TextUtils.isEmpty(password)){
                System.out.println("Rossz jelszó");
                return;
            }

            //User típusba rakás
            User user = new User();
            user.setLast_name(lastname);
            user.setFirst_name(firstname);
            user.setEmail(email);
            user.setPassword(password);

            //Api request-ek kezelésére
            NetworkService networkService = new NetworkService();
            UserApi userApi = networkService.getRetrofit().create(UserApi.class);

            //enqueue arra a célra mialatt küldi ne legyen használhatatlan a program (async)
            //new Call

            userApi.registerUser(user)
                    .enqueue(new Callback<JwtResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<JwtResponse> call, @NonNull Response<JwtResponse> response) {
                            //Ha 409-es hibakódot küld vissza akkor az email cím foglalt
                            System.out.println("onResponse: " + response.code());
                            if (response.code() == 409) {


                                //Custom Toast
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.toast,
                                        (ViewGroup) findViewById(R.id.toast_layout_root));
                                TextView text = (TextView) layout.findViewById(R.id.text);
                                text.setText("Foglalt email cím");
                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();

                            } else {
                                JwtResponse jwtResponse = response.body();
                                String jwtToken = jwtResponse.getJwttoken();
                                String refreshToken = jwtResponse.getRefreshToken();

                                //SharedPreferencies
                                TokenUtils tokenUtils = new TokenUtils(RegisterActivity.this);
                                tokenUtils.saveAccessToken(jwtToken);
                                tokenUtils.setRefreshToken(refreshToken);

                                //Custom Toast
                                LayoutInflater inflater = getLayoutInflater();
                                View layout = inflater.inflate(R.layout.toast,
                                        (ViewGroup) findViewById(R.id.toast_layout_root));
                                TextView text = (TextView) layout.findViewById(R.id.text);
                                text.setText("Sikeres regisztrálás");
                                Toast toast = new Toast(getApplicationContext());
                                toast.setDuration(Toast.LENGTH_SHORT);
                                toast.setView(layout);
                                toast.show();


                                //Intent
                                startActivity(new Intent(RegisterActivity.this, MainPage.class));
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<JwtResponse> call, @NonNull Throwable t) {
                            System.out.println("onFailure: " + t);
                            System.out.println("onFailure: " + call.toString());
                            Toast.makeText(RegisterActivity.this, "Ismeretlen hiba", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void init() {
        lastnameText = findViewById(R.id.lastName_input);
        firstnameText = findViewById(R.id.firstName_input);
        emailText = findViewById(R.id.email_input);
        passwordText = findViewById(R.id.password_input);
        login = findViewById(R.id.LogInTextView);

        registerButton = findViewById(R.id.registerButton);
    }
}