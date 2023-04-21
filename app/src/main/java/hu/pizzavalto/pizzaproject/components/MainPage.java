package hu.pizzavalto.pizzaproject.components;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

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
 * A MainPage osztály egy Activity, amely a főoldal felületét jeleníti meg.
 * A felhasználó nevét és szerepkörét a NavigationView headerében jeleníti meg.
 * Az oldalsó menüből navigálhat a különböző fragmentek között.
 * A felhasználó a kilépés menüpontra kattintva tud kijelentkezni, amelyhez megerősítő dialogot jelenít meg.
 * Ha a felhasználó access token-je lejárt, akkor a refresh token segítségével kér újabb access token-t
 * majd megpróbálja a metódus futtatását ismét.
 */
public class MainPage extends AppCompatActivity {
    /**
     * Navigáció fejlécében megjeletídentő felhasználó keresztnevének textfield-je.
     */
    private TextView profileNameTextView;

    /**
     * Navigáció fejlécében megjeletídentő felhasználó szerepkörének textfield-je.
     */
    private TextView profileRoleTextView;

    /**
     * AppBar címének textfield-je.
     */
    private TextView textTitle;

    /**
     * Felhasználó objektum.
     */
    private User user;

    /**
     * MainPage layout objektum.
     */
    private DrawerLayout mainPageLayout;

    /**
     * Kilépés menuitem gomb.
     */
    private MenuItem logoutMenuItem;

    /**
     * Navigáció létrejöttét biztosító konroller.
     */
    private NavController navController;

    /**
     * Az onCreate függvény felelős az Activity inicializálásáért.
     * Beállítja a képernyő orientációját, betölti a megfelelő layoutot,
     * inicializálja a UI elemeket, lekéri a felhasználó adatait,
     * és beállítja az App Bar címét a navigáció helyszínétől függően.
     *
     * @param savedInstanceState Ha az Activity újra inicializálódik
     *                           az előzőleg elmentett állapot visszaállításához szükséges adatokat tartalmazza.
     *                           <b><i>Megjegyzés: Ellenkező esetben null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // Meghívja a UI elemek inicializálásért felelős metódust
        init();

        // Felhasználó adatait kéri le a metódus segítségével
        getUserInformation();

        // Kijelentkezés gombra kattintva meghívja a dialog-ot megjelenítő metódust majd kilépteti az alkalmazásból.
        logoutMenuItem.setOnMenuItemClickListener(menuItem -> {
            showLogoutConfirmationDialog();
            return true;
        });

        // A navigation helyszínétől függően állítja be az App Bar címét.
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> textTitle.setText(destination.getLabel()));
    }

    /**
     * Felhasználó objektum publikus Getter metódusa
     *
     * @return visszaadja a felhasználó objektumot
     */
    public User getUser() {
        return user;
    }

    /**
     * Kilépésért felelős metódus, mely meghívásakor megjelenít egy Dialog-ot.
     * Igen gombra kattintáskor törli az elmentett tokeneket.
     * Nem gombra kattintáskor bezárja a Dialog-ot és nem történik semmi.
     */
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this).setTitle("Biztosan ki akarsz lépni?").setPositiveButton("Igen", (dialogInterface, i) -> {
            TokenUtils tokenUtils = new TokenUtils(MainPage.this);
            tokenUtils.clearTokens();
            navigateToLoginActivity();
        }).setNegativeButton("Nem", (dialogInterface, i) -> dialogInterface.dismiss()).show();
    }

    /**
     * Felhasználói adatok lekérdezése szerverről.
     */
    private void getUserInformation() {
        // access token lekérése. null esetén vissza irányítás a bejelentkező oldalra
        TokenUtils tokenUtils = new TokenUtils(MainPage.this);
        String accessToken = tokenUtils.getAccessToken();
        if (accessToken == null) {
            navigateToLoginActivity();
            return;
        }

        // API request előhívása
        ApiService apiService = new NetworkService().getRetrofit().create(ApiService.class);
        apiService.getUserInformation("Bearer " + accessToken).enqueue(new Callback<User>() {
            /**
             * Sikeres API hívás esetén ez a metódus fut le.
             *
             * @param call Válaszként várt felhasználó objektum.
             * @param response A hívás válasza ami tartalmazza a hívás adatait mint például státusz kód, válasz teste..stb
             */
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                // Sikeres válasz esetén elmenti a felhasználó adatait egy felhasználó objektumban.
                if (response.isSuccessful()) {

                    User user = response.body();
                    if (user != null) {
                        profileNameTextView.setText(user.getFirst_name());
                        profileRoleTextView.setText(user.getRole().equals("ADMIN") ? "Adminisztrátor" : "Felhasználó");
                        profileRoleTextView.setTextColor(Color.parseColor(user.getRole().equals("ADMIN") ? "#FF0000" : "#00FF00"));
                        MainPage.this.user = user;
                    } else {
                        navigateToLoginActivity();
                    }

                }
                // Sikertelen híváskor lekezeli a hibát.
                else {
                    handleResponseCode(response.code(), tokenUtils, apiService);
                }
            }

            /**
             * Sikertelen API hívás esetén ez a metódus fut le.
             *
             * @param call Válaszként várt felhasználó objektum.
             * @param t visszadobott hibaüzenet.
             */
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                navigateToLoginActivity();
            }
        });
    }

    /**
     * Ha nem 200-as kódot kap az információ lekérés válaszként akkor ez a metódus hívódik meg
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
                        getUserInformation();
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
        startActivity(new Intent(MainPage.this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    /**
     * Az osztályban található objektumok inicializálása, amelyek szerepet játszanak a program megjelenítésében és működésében.
     * Beállításra kerül a NavigationView, a főoldal menüjének megjelenítése, valamint a navigációs kontroller.
     */
    private void init() {
        // NavigationView beállítása
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);

        // NavigationView fejléc objektumai
        View headerView = navigationView.getHeaderView(0);
        profileNameTextView = headerView.findViewById(R.id.ProfileName);
        profileRoleTextView = headerView.findViewById(R.id.ProfileRole);

        // Menü elem
        Menu menu = navigationView.getMenu();
        logoutMenuItem = menu.findItem(R.id.menuLogout);

        textTitle = findViewById(R.id.textTitle);

        // Oldalsó panel
        mainPageLayout = findViewById(R.id.mainPageLayout);
        findViewById(R.id.imageMenu).setOnClickListener(view -> mainPageLayout.openDrawer(GravityCompat.START));

        // Navigáció inicializálása
        navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);
    }
}