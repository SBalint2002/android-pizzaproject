package hu.pizzavalto.pizzaproject.components;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import hu.pizzavalto.pizzaproject.R;
import hu.pizzavalto.pizzaproject.auth.JwtResponse;
import hu.pizzavalto.pizzaproject.model.OrderDto;
import hu.pizzavalto.pizzaproject.model.PizzaViewModel;
import hu.pizzavalto.pizzaproject.retrofit.ApiService;
import hu.pizzavalto.pizzaproject.retrofit.NetworkService;
import hu.pizzavalto.pizzaproject.sharedpreferences.TokenUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Az OrderActivity osztály a megrendelési folyamatot kezeli.
 * Inicializálja a vissza gombot, a megrendelés gombot, a szövegbeviteli mezőket, valamint
 * az ár megjelenítő szöveget. Ha a felhasználó nincs bejelentkezve, akkor átirányítja a LoginActivity-be.
 * Ha a megrendelés sikeres, akkor egy Dialog-ot jelenít meg a felhasználónak, amelyben megerősíti,
 * hogy a rendelés sikeres volt, majd a pizzák és a megrendelések üres listává válnak, majd vissza irányítja a felhasználót a MainPage-re.
 * Ha a válasz nem sikeres, akkor a válasz kódját ellenőrzi, és a hibakódnak megfelelően cselekszik.
 */
public class OrderActivity extends AppCompatActivity {
    /**
     * Visszagomb és a rendelés gomb létrehozása.
     */
    private Button backButton, orderButton;

    /**
     * A PizzaViewModel osztályban tárolja a pizzákat hashmap-ben annak érdekében, hogy mennyiséget lehessen számítani azonosítóhoz.
     */
    private PizzaViewModel pizzaViewModel;

    /**
     * Rendelési adatok megadásához szükséges beviteli mezők. Lakcím illetve telefonszám.
     */
    private TextInputEditText address_input, phone_input;

    /**
     * Sikeres megrendelést visszaigazoló Dialog.
     */
    private Dialog orderAddedDialog;

    /**
     * Rendelés összesített ára.
     */
    private TextView fullPrice;

    /**
     * Felfüggesztéskor bezárja a Dialog-ot.
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (orderAddedDialog != null && orderAddedDialog.isShowing()) {
            orderAddedDialog.dismiss();
        }
    }

    /**
     * Az Activity, ahol a felhasználó leadhatja a rendelését.
     * A rendelést az orderButton megnyomásával lehet leadni, a vissza gombbal pedig vissza lehet térni az előző Activity-re.
     * Az átirányításkor kapott ár értékét megjeleníti a fizetendő összeg helyén.
     * Az access token lekérése után a megadott adatok alapján elküld egy rendelést a szervernek az order() metódusban.
     *
     * @param savedInstanceState Ha az Activity újra inicializálódik
     *                           az előzőleg elmentett állapot visszaállításához szükséges adatokat tartalmazza.
     *                           <b><i>Megjegyzés: Ellenkező esetben null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Meghívja a UI elemek inicializálásért felelős metódust
        init();

        // Vissza gombra kattintva bezárja az Activity-t
        backButton.setOnClickListener(view -> finish());

        // Order gombra kattintva lefuttatja a megrendelést
        orderButton.setOnClickListener(view -> order());

        // Atirányításkor kapott ár változó értékét állítja a fizetendő összeg helyére
        fullPrice.setText("Fizetendő összeg: " + getIntent().getIntExtra("price", 0) + " Ft");
    }

    /**
     * Rendelés feladása.
     */
    private void order() {
        // Input mezők adatainak lekérdezése
        String address = Objects.requireNonNull(address_input.getText()).toString().trim();
        String phone = Objects.requireNonNull(phone_input.getText()).toString().trim();

        // Üres mezők ellenőrzése
        if (address.isEmpty() || phone.isEmpty()) {
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

        // access token lekérése. null esetén vissza irányítás a bejelentkező oldalra
        TokenUtils tokenUtils = new TokenUtils(OrderActivity.this);
        String accessToken = tokenUtils.getAccessToken();
        if (accessToken == null) {
            navigateToLoginActivity();
        }
        // Pizza Id listába helyezi a HashMap-ből a pizzákat hogy aztán a backend felé küldje ilyen formában [1, 1, 1, 3, 3]
        HashMap<Long, Integer> pizzaIds = (HashMap<Long, Integer>) getIntent().getSerializableExtra("pizzaIds");
        List<Long> pizzaIdList = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : pizzaIds.entrySet()) {
            Long pizzaId = entry.getKey();
            Integer quantity = entry.getValue();
            for (int i = 0; i < quantity; i++) {
                pizzaIdList.add(pizzaId);
            }
        }

        // Rendelés adatainak összeállítása
        String location = Objects.requireNonNull(address_input.getText()).toString();
        String phoneNumber = Objects.requireNonNull(phone_input.getText()).toString();
        OrderDto orderDto = new OrderDto(location, phoneNumber, pizzaIdList);

        // API request előhívása
        ApiService apiService = new NetworkService().getRetrofit().create(ApiService.class);
        apiService.addOrder("Bearer " + accessToken, orderDto).enqueue(new Callback<ResponseBody>() {
            /**
             * Sikeres Api hívás esetén ez a metódus fut le.
             *
             * @param call válasz üzenet a kérés eredményéről.
             * @param response A hívás válasza ami tartalmazza a hívás adatait mint például státusz kód, válasz teste..stb
             */
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                // Sikeres válasz esetén 200-as státuszkódot (OK) küld.
                if (response.isSuccessful()) {

                    // Sikeres rendelésről szóló Dialog
                    orderAddedDialog = new Dialog(OrderActivity.this);
                    View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.successful_order, findViewById(android.R.id.content), false);
                    orderAddedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    orderAddedDialog.setContentView(dialogView);

                    Window window = orderAddedDialog.getWindow();
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
                    window.setAttributes(params);

                    // Ok button alias elfogadás és bezárás
                    Button okButton = dialogView.findViewById(R.id.btn_ok);
                    okButton.setOnClickListener(add -> {
                        pizzaViewModel.clear();
                        pizzaViewModel.setPizzaIds(new HashMap<>());

                        orderAddedDialog.dismiss();

                        // Vissza irányítás a MainPage oldalra úgy, hogy azt újraindítja mivel nem volt bezárva.
                        Intent intent = new Intent(OrderActivity.this, MainPage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });

                    orderAddedDialog.show();
                }
                // Hibakódnak megfelelő cselekedet
                else {
                    handleResponseCode(response.code(), tokenUtils, apiService);
                }
            }

            /**
             * Sikertelen API hívás esetén ez a metódus fut le. Ez esetben bezárja az Activity-t.
             *
             * @param call válasz üzenet a hibáról a body-ban.
             * @param t visszadobott hiba.
             */
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                finish();
            }
        });
    }

    /**
     * Ha nem 200-as kódot kap a rendelés feladáskor a lekérés válaszként akkor ez a metódus hívódik meg
     * amely ha 451-es kódot kap (Lejárt access token) tovább küldi a kérést a handleTokenRefresh
     * metódusnak, különben vissza irányít a bejelntkező oldalra.
     *
     * @param code       válasz státusz kódja.
     * @param tokenUtils SharedPreferences meghívása amelyben találhatóak a tokenek
     * @param apiService A kérés újra elküldéséhez, hogy ne kelljen megint inicializálni.
     */
    private void handleResponseCode(int code, TokenUtils tokenUtils, ApiService apiService) {
        if (code == 451) {
            handleTokenRefresh(tokenUtils, apiService);
        } else {
            navigateToLoginActivity();
        }
    }

    /**
     * Lejárt access tokent esetén fut le. A refresh token segítségével kér egy újat.
     *
     * @param tokenUtils SharedPreferences meghívása amelyben találhatóak a tokenek
     * @param apiService A kérés újra elküldéséhez, hogy ne kelljen megint inicializálni.
     */
    private void handleTokenRefresh(TokenUtils tokenUtils, ApiService apiService) {
        // Ha nincs refresh token átírányít a bejelentkezés oldalára
        String refreshToken = tokenUtils.getRefreshToken();
        if (refreshToken == null) {
            System.out.println("Hiányzó refreshtoken");
            navigateToLoginActivity();
            return;
        }

        // Access token frissítéséért felelős API request előhívása.
        TokenUtils.refreshUserToken(tokenUtils, apiService, new Callback<JwtResponse>() {
            /**
             * Sikeres Api hívás esetén ez a metódus fut le.
             *
             * @param call Válaszként várt Jwt response fájl amely tartalmazza a felhasználóhoz tartozó új access token-t és a refresh token-t.
             * @param response A hívás válasza ami tartalmazza a hívás adatait mint például státusz kód, válasz teste..stb
             */
            @Override
            public void onResponse(@NonNull Call<JwtResponse> call, @NonNull Response<JwtResponse> response) {
                // Sikeres frissítés esetén 200-as státuszkódot (OK) küld válaszként, majd elmenti a tokeneket.
                if (response.isSuccessful()) {
                    JwtResponse jwtResponse = response.body();
                    if (jwtResponse != null && jwtResponse.getAccessToken() != null) {
                        tokenUtils.saveAccessToken(jwtResponse.getAccessToken());
                        tokenUtils.setRefreshToken(jwtResponse.getRefreshToken());
                        order();
                    } else {
                        navigateToLoginActivity();
                    }
                } else {
                    navigateToLoginActivity();
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
                navigateToLoginActivity();
            }
        });
    }

    /**
     * Meghívásakor animációval egybe fűzve vissza irányít a bejelentkező oldalra.
     */
    private void navigateToLoginActivity() {
        startActivity(new Intent(OrderActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    /**
     * Input mezők, szövegek és a gombok inicializálása.
     */
    private void init() {
        backButton = findViewById(R.id.backButton);
        orderButton = findViewById(R.id.orderButton);

        address_input = findViewById(R.id.address_input);
        phone_input = findViewById(R.id.phone_input);

        pizzaViewModel = new ViewModelProvider(this).get(PizzaViewModel.class);

        fullPrice = findViewById(R.id.fullPrice);
    }
}