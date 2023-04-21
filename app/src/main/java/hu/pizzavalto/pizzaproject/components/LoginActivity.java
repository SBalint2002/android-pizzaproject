package hu.pizzavalto.pizzaproject.components;

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
import hu.pizzavalto.pizzaproject.retrofit.ApiService;
import hu.pizzavalto.pizzaproject.sharedpreferences.TokenUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Az LoginActivity osztály felelős az alkalmazás belépőfelületéért és a felhasználó bejelentkezéséért.
 * Az osztály megjeleníti a megfelelő UI elemeket, mint pl. a belejentkezés gomb, az e-mail cím, a jelszó beviteli mezőket.
 * A felhasználó megadja az email címét és a hozzátartozó jelszavát, majd a bejelentkezés gombra kattintva az osztály
 * elküldi az adatokat az API-nak és megkapja a választ.
 * Ha a felhasználó nem tölti ki mindkét mezőt, a metódus egy toast üzenetet jelenít meg az erre vonatkozó figyelmeztetéssel.
 * Az API válasza alapján az osztály vagy sikeres bejelentkezés esetén átirányítja a felhasználót a főoldalra,
 * vagy a sikertelen bejelentkezést figyelmeztető toast üzenetet jelenít meg.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Regisztrációra irányító textView
     */
    private TextView register;

    /**
     * Email cím és Jelszó beviteli mezők.
     */
    private TextInputEditText emailText, passwordText;

    /**
     * Bejelentkezés gomb
     */
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_login);

        // Meghívja a UI elemek inicializálásért felelős metódust
        init();

        // A regisztráció gombra kattintva a RegisterActivity-re irányítja a felhasználót
        register.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        });

        // A bejelentkezés gombra kattintva ellenőrzi az input mezők kitöltöttségét.
        // Ha a felhasználó nem tölti ki mindkét mezőt, a metódus egy toast üzenetet jelenít meg az erre vonatkozó figyelmeztetéssel.
        loginButton.setOnClickListener(view -> {
            String email = String.valueOf(emailText.getText());
            String password = String.valueOf(passwordText.getText());

            //Ellenőrzi hogy üresek -e a mezők
            if (email.isEmpty() || password.isEmpty()) {

                //Egyedi Toast
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast_layout_root));
                TextView text = layout.findViewById(R.id.text);
                text.setText("Minden mezőt ki kell tölteni!");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

                return;
            }

            //User objektum létrehozása és feltöltése.
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);

            // Bejelentkezésért felelős API request előhívása.
            ApiService apiService = new NetworkService().getRetrofit().create(ApiService.class);
            apiService.loginUser(user).enqueue(new Callback<JwtResponse>() {
                /**
                 * Sikeres Api hívás esetén ez a metódus fut le.
                 *
                 * @param call Válaszként várt Jwt response fájl amely tartalmazza a felhasználóhoz tartozó access token-t és refresh token-t.
                 * @param response A hívás válasza ami tartalmazza a hívás adatait mint például státusz kód, válasz teste..stb
                 */
                @Override
                public void onResponse(@NonNull Call<JwtResponse> call, @NonNull Response<JwtResponse> response) {
                    // Ha 403-as hibakódot (FORBIDDEN) küld válaszként akkor ha nem sikerül belépni.
                    if (response.code() == 403) {

                        // Egyedi Toast
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast_layout_root));
                        TextView text = layout.findViewById(R.id.text);
                        text.setText("Nem megfelelő email és jelszó páros!");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();


                    }
                    // Sikeres belépés esetén 200-as státuszkódot (OK) küld válaszként.
                    else if (response.isSuccessful()) {
                        JwtResponse jwtResponse = response.body();
                        String accessToken = null;
                        if (jwtResponse != null) {
                            accessToken = jwtResponse.getAccessToken();
                        }
                        String refreshToken = null;
                        if (jwtResponse != null) {
                            refreshToken = jwtResponse.getRefreshToken();
                        }

                        // Tokenek mentése SharedPreferencies-be
                        TokenUtils tokenUtils = new TokenUtils(LoginActivity.this);
                        tokenUtils.saveAccessToken(accessToken);
                        tokenUtils.setRefreshToken(refreshToken);

                        // Egyedi Toast
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast_layout_root));
                        TextView text = layout.findViewById(R.id.text);
                        text.setText("Sikeres bejelentkezés!");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();

                        // Animációval egybefűzött átirányítás a MainPage osztályba.
                        startActivity(new Intent(LoginActivity.this, MainPage.class));
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    } else {
                        // Egyedi Toast
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast_layout_root));
                        TextView text = layout.findViewById(R.id.text);
                        text.setText("Ismeretlen hiba!");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();
                    }
                }

                /**
                 * Sikertelen API hívás esetén ez a metódus fut le.
                 *
                 * @param call Válaszként várt Jwt response fájl amely tartalmazza a felhasználóhoz tartozó access token-t és refresh token-t ez esetben null értékekkel.
                 * @param t visszadobott hiba.
                 */
                @Override
                public void onFailure(@NonNull Call<JwtResponse> call, @NonNull Throwable t) {
                    Toast.makeText(LoginActivity.this, "Ismeretlen hiba", Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    /**
     * Input mezők, regisztráció szöveg és a bejelntkezés gomb inicializálása.
     */
    private void init() {
        register = findViewById(R.id.RegisterTextView);
        emailText = findViewById(R.id.email_input);
        passwordText = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.loginButton);
    }
}