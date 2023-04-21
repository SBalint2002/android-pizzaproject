package hu.pizzavalto.pizzaproject.components;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Patterns;
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
import hu.pizzavalto.pizzaproject.retrofit.ApiService;
import hu.pizzavalto.pizzaproject.retrofit.NetworkService;
import hu.pizzavalto.pizzaproject.sharedpreferences.TokenUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A RegisterActivity osztály az alkalmazás regisztrációs felületét valósítja meg.
 * Az osztály megjeleníti a megfelelő UI elemeket, mint pl. a regisztrációs gomb,
 * az e-mail cím, a jelszó és a név beviteli mezőket.
 * A felhasználók regisztrálhatnak úgy, hogy megadják az e-mail címüket, a jelszavukat és a nevüket.
 * A regisztráció gomb megnyomásakor az adatok validációs ellenőrzésre kerülnek.
 * A felhasználói adatokat egy User típusú objektumba rakjuk és küldjük el a szervernek a retrofit használatával.
 * A válaszként kapott JWT tokeneket SharedPreferences-ben tároljuk és átirányítjuk a felhasználót a főoldalra, ha a regisztráció sikeres volt.
 */
public class RegisterActivity extends AppCompatActivity {

    /**
     * Regisztrálás gomb létrehozása.
     */
    private Button registerButton;

    /**
     * A bejelentkazés oldalra való átirányításhoz létrehozott szövegmező.
     */
    private TextView login;

    /**
     * Regisztráláshoz szükséges beviteli mezők létrehozás.
     */
    private TextInputEditText lastnameText, firstnameText, emailText, passwordText;

    /**
     * Az onCreate metódus az aktivitás életciklusának része, amelyet az Android rendszer hív meg,
     * amikor az aktivitást létrehozzák. Az első dolog, amit a metódus csinál, hogy meghívja
     * az ősosztály onCreate metódusát, majd inicializálja az UI elemeket, beállítja a képernyő
     * orientációját. Ezután regisztrálja a gomb eseménykezelőit és
     * elvégzi a validációt az input mezők kitöltöttségének és helyességének ellenőrzésére.
     *
     * @param savedInstanceState Ha az Activity újra inicializálódik
     *                           az előzőleg elmentett állapot visszaállításához szükséges adatokat tartalmazza.
     *                           <b><i>Megjegyzés: Ellenkező esetben null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_register);

        // Meghívja a UI elemek inicializálásért felelős metódust
        init();

        // A bejelentkezés gombra kattintva a LoginActivity-re irányítja a felhasználót
        login.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });

        // A regisztráció gombra kattintva ellenőrzi az input mezők kitöltöttségét.
        // Ha a felhasználó nem tölti ki mendegyik mezőt, a metódus egy toast üzenetet jelenít meg az erre vonatkozó figyelmeztetéssel.
        registerButton.setOnClickListener(view -> {
            String lastname = String.valueOf(lastnameText.getText());
            String firstname = String.valueOf(firstnameText.getText());
            String email = String.valueOf(emailText.getText());
            String password = String.valueOf(passwordText.getText());

            // Ellenőrzi hogy üresek -e a mezők
            if (lastname.isEmpty() || firstname.isEmpty() || email.isEmpty() || password.isEmpty()) {

                // Egyedi Toast
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

            // Vezetéknév validáció
            if (lastname.length() < 2 || lastname.length() > 50) {

                // Egyedi Toast
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast_layout_root));
                TextView text = layout.findViewById(R.id.text);
                text.setText("A vezetéknévnek 2 és 50 karakter között kell lennie!");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

                return;
            }

            // Keresztnév validáció
            if (firstname.length() < 2 || firstname.length() > 50) {

                // Egyedi Toast
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast_layout_root));
                TextView text = layout.findViewById(R.id.text);
                text.setText("A keresztnévnek 2 és 50 karakter között kell lennie!");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

                return;
            }

            // Email cím validáció
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                // Egyedi Toast
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast_layout_root));
                TextView text = layout.findViewById(R.id.text);
                text.setText("Nem megfelelő email formátum!");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

                return;
            }

            // Jelszó validáció (Minimum 6 karakter)
            if (password.length() < 6) {

                // Egyedi Toast
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast_layout_root));
                TextView text = layout.findViewById(R.id.text);
                text.setText("A jelszónak legalább 6 karakterből kell állnia!");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

                return;
            }

            // Jelszó validáció (Maximum 255 karakter)
            if (password.length() > 255) {

                // Egyedi Toast
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast_layout_root));
                TextView text = layout.findViewById(R.id.text);
                text.setText("A jelszó nem lehet hosszabb 255 karakternél!");
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();

                return;
            }

            //User objektum létrehozása és feltöltése.
            User user = new User();
            user.setLast_name(lastname);
            user.setFirst_name(firstname);
            user.setEmail(email);
            user.setPassword(password);

            // Regisztrációért felelős API request előhívása
            ApiService apiService = new NetworkService().getRetrofit().create(ApiService.class);
            apiService.registerUser(user).enqueue(new Callback<JwtResponse>() {
                /**
                 * Sikeres Api hívás esetén ez a metódus fut le.
                 *
                 * @param call Válaszként várt Jwt response fájl amely tartalmazza a felhasználóhoz tartozó access token-t és refresh token-t.
                 * @param response A hívás válasza ami tartalmazza a hívás adatait mint például státusz kód, válasz teste..stb
                 */
                @Override
                public void onResponse(@NonNull Call<JwtResponse> call, @NonNull Response<JwtResponse> response) {
                    //Ha 409-es hibakódot küld vissza akkor ha az email cím foglalt.
                    if (response.code() == 409) {

                        // Egyedi Toast
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast_layout_root));
                        TextView text = layout.findViewById(R.id.text);
                        text.setText("Az email cím foglalt!");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();

                    }
                    // Sikeres regisztrálás esetén 200-as státuszkódot (OK) küld válaszként.
                    else if (response.isSuccessful()) {
                        JwtResponse jwtResponse = response.body();
                        String jwtToken = null;
                        if (jwtResponse != null) {
                            jwtToken = jwtResponse.getAccessToken();
                        }
                        String refreshToken = null;
                        if (jwtResponse != null) {
                            refreshToken = jwtResponse.getRefreshToken();
                        }

                        // Tokenek mentése SharedPreferencies-be
                        TokenUtils tokenUtils = new TokenUtils(RegisterActivity.this);
                        tokenUtils.saveAccessToken(jwtToken);
                        tokenUtils.setRefreshToken(refreshToken);

                        // Egyedi Toast
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast, findViewById(R.id.toast_layout_root));
                        TextView text = layout.findViewById(R.id.text);
                        text.setText("Sikeres regisztrálás");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();


                        // Animációval egybefűzött átirányítás a MainPage osztályba.
                        startActivity(new Intent(RegisterActivity.this, MainPage.class));
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
                 * @param t visszadobott hibaüzenet.
                 */
                @Override
                public void onFailure(@NonNull Call<JwtResponse> call, @NonNull Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Ismeretlen hiba", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Input mezők, bejelentkezés szöveg és a regisztráció gomb inicializálása.
     */
    private void init() {
        lastnameText = findViewById(R.id.lastName_input);
        firstnameText = findViewById(R.id.firstName_input);
        emailText = findViewById(R.id.email_input);
        passwordText = findViewById(R.id.password_input);
        login = findViewById(R.id.LogInTextView);
        registerButton = findViewById(R.id.registerButton);
    }
}