package hu.pizzavalto.pizzaproject.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import hu.pizzavalto.pizzaproject.R;
import hu.pizzavalto.pizzaproject.auth.JwtResponse;
import hu.pizzavalto.pizzaproject.components.LoginActivity;
import hu.pizzavalto.pizzaproject.components.MainPage;
import hu.pizzavalto.pizzaproject.model.User;
import hu.pizzavalto.pizzaproject.retrofit.ApiService;
import hu.pizzavalto.pizzaproject.retrofit.NetworkService;
import hu.pizzavalto.pizzaproject.sharedpreferences.TokenUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Egy {@link Fragment} alosztály ami megjeleníti és lehetővé teszi a felhasználó számára a profiljának módosítását.
 * Tartalmazza az EditText mezőket a felhasználó adatainak szerkesztéséhez és a mentés gombot, ami elmenti az adatokat.
 * Az adatok mentése előtt a metódus ellenőrzi az adatok helyességét és ha hibát talál, megjeleníti az erről szóló figyelmeztetést.
 */
public class ProfileFragment extends Fragment {

    /**
     * Beviteli mezők a módosítás érdekében.
     */
    private EditText profileLastName, profileFirstName, profileEmail, profilePassword;

    /**
     * Módosítások mentésére szolgáló gomb.
     */
    private Button saveProfileButton;

    /**
     * Eredeti adatok változóban való eltárolása, hogy nyomon lehessen követni a változtatásokat.
     */
    private String originalLastName, originalFirstName, originalEmail;

    /**
     * Globális felhasználó adatok, hogy más fragmentből át lehessen venni.
     */
    private User user;

    /**
     * Felhasználó módosított adatait meni el egy változóban.
     */
    private User modifyUser;

    /**
     * Az onCreateView metódus felelős az XML fájlból való View létrehozásáért.
     * Inicializálja a view elemeket, majd beállítja a felhasználó profiljának
     * adatait, amelyeket a felhasználó objektumból kap. Hozzáad egy TextWatcher-t,
     * hogy érzékelje a EditText mezőkben bekövetkező változásokat.
     * Ha a felhasználó megpróbál menteni egy érvénytelen adatot, akkor AlertDialog-ot jelenít meg.
     * Ha az adatok megfelelőek, akkor a metódus létrehoz egy új felhasználó objektumot,
     * és meghívja a saveUserInformation metódust az adatok mentéséhez.
     *
     * @param inflater           Az inflater objektum a view létrehozásához.
     * @param container          A view konténer objektuma.
     * @param savedInstanceState A mentett állapot objektuma.
     * @return Az onCreateView metódus által létrehozott View objektum.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        init(view);

        User user = ((MainPage) requireActivity()).getUser();
        profileLastName.setText(user.getLast_name());
        profileFirstName.setText(user.getFirst_name());
        profileEmail.setText(user.getEmail());
        profilePassword.setText("");

        // Add a TextWatcher to detect changes in any of the EditText fields.
        profileLastName.addTextChangedListener(textWatcher);
        profileFirstName.addTextChangedListener(textWatcher);
        profileEmail.addTextChangedListener(textWatcher);
        profilePassword.addTextChangedListener(textWatcher);

        saveProfileButton.setOnClickListener(saveProfile -> {
            String password = null;
            if (!profilePassword.getText().toString().isEmpty()) {
                if (profilePassword.getText().length() < 6) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setMessage("A jelszónak minimum 6 karakterből kell állnia.").setTitle("Figyelem").setPositiveButton("OK", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return;
                }
                password = profilePassword.getText().toString();
            }
            if (profileFirstName.length() < 2 || profileFirstName.length() > 50) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setMessage("A keresztnévnek 2 és 50 karakter közöttinek kell lennie!").setTitle("Figyelem").setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }
            if (profileLastName.length() < 2 || profileLastName.length() > 50) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setMessage("A vezetéknévnek 2 és 50 karakter közöttinek kell lennie!").setTitle("Figyelem").setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }
            if (!isValidEmail(profileEmail.getText().toString())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setMessage("Nem megfelelő email formátum!").setTitle("Figyelem").setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
                return;
            }
            modifyUser = new User(user.getId(), profileFirstName.getText().toString(), profileLastName.getText().toString(), profileEmail.getText().toString(), password, user.getRole());
            saveUserInformation();
        });

        return view;
    }

    /**
     * Ellenőrzi, hogy a beírt email cím érvényes -e.
     *
     * @param email megadott email cím.
     * @return Igaz vagy hamis érték attól függően, hogy megfelel -e a mintának.
     */
    public static boolean isValidEmail(String email) {
        String pattern = "^[a-zA-Z/d._%+-]+@[a-zA-Z/d.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(pattern);
    }

    /**
     * Szöveg figyelő metódus, amely megvizsgálja, hogy a felhasználói adatok változtak-e.
     * Ha igen, akkor az "Mentés" gomb engedélyezése.
     */
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            saveProfileButton.setEnabled(hasChanges());
        }
    };

    /**
     * Ez a funkció ellenőrzi, hogy a történt e változtatás bármely beviteli mezőben. Minden módosításnál lefut.
     * Ha visszakerül az eredeti helyzetbe akkor hamis értéket ad eredményül.
     *
     * @return
     */
    private boolean hasChanges() {
        String currentLastName = profileLastName.getText().toString().trim();
        String currentFirstName = profileFirstName.getText().toString().trim();
        String currentEmail = profileEmail.getText().toString().trim();
        String currentPassword = profilePassword.getText().toString().trim();

        return !currentLastName.equals(originalLastName) || !currentFirstName.equals(originalFirstName) || !currentEmail.equals(originalEmail) || !currentPassword.isEmpty();
    }

    /**
     * Felhasználói adatok mentése.
     * Az access token lekérdezése, majd annak hiányában a login activityre navigálás.
     * Az ApiService inicializálása és a felhasználó adatainak mentése.
     */
    private void saveUserInformation() {
        TokenUtils tokenUtils = new TokenUtils(requireActivity());
        String accessToken = tokenUtils.getAccessToken();
        if (accessToken == null) {
            navigateToLoginActivity();
            return;
        }

        ApiService apiService = new NetworkService().getRetrofit().create(ApiService.class);
        apiService.saveUser("Bearer " + accessToken, user.getId(), modifyUser).enqueue(new Callback<ResponseBody>() {
            /**
             * Sikeres API hívás esetén ez a metódus fut le.
             *
             * @param call Válaszként várt üzenet.
             * @param response A hívás válasza ami tartalmazza a hívás adatait mint például státusz kód, válasz teste..stb
             */
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Dialog orderDialog = new Dialog(getActivity());
                    View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.logout_dialog, (ViewGroup) getView(), false);
                    orderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    orderDialog.setContentView(dialogView);

                    Window window = orderDialog.getWindow();
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
                    window.setAttributes(params);

                    Button okButton = dialogView.findViewById(R.id.btn_ok);

                    okButton.setOnClickListener(add -> {
                        navigateToLoginActivity();
                        orderDialog.dismiss();
                    });

                    orderDialog.show();
                } else {
                    handleResponseCode(response.code(), tokenUtils, apiService);
                }
            }

            /**
             * Sikertelen API hívás esetén ez a metódus fut le.
             *
             * @param call Válaszként várt üzenet.
             * @param t visszadobott hibaüzenet.
             */
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                navigateToLoginActivity();
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
        String refreshToken = tokenUtils.getRefreshToken();
        if (refreshToken == null) {
            System.out.println("Hiányzó refreshtoken");
            navigateToLoginActivity();
            return;
        }

        TokenUtils.refreshUserToken(tokenUtils, apiService, new Callback<JwtResponse>() {
            /**
             * Sikeres Api hívás esetén ez a metódus fut le.
             *
             * @param call Válaszként várt Jwt response fájl amely tartalmazza a felhasználóhoz tartozó új access token-t és a refresh token-t.
             * @param response A hívás válasza ami tartalmazza a hívás adatait mint például státusz kód, válasz teste..stb
             */
            @Override
            public void onResponse(@NonNull Call<JwtResponse> call, @NonNull Response<JwtResponse> response) {
                if (!response.isSuccessful()) {
                    navigateToLoginActivity();
                    return;
                }
                JwtResponse jwtResponse = response.body();
                if (jwtResponse != null) {
                    tokenUtils.saveAccessToken(jwtResponse.getAccessToken());
                }
                if (jwtResponse != null) {
                    tokenUtils.setRefreshToken(jwtResponse.getRefreshToken());
                }
                saveUserInformation();
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
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    /**
     * Inicializálja a profil adatokat, gombokat és elmenti a jelenlegi felhasználói adatokat egy objektummal a megadott {@code view}-val.
     * Ezek a megjelenítési elemek szolgálnak a felhasználó adatainak megjelenítésére illetve módosításaira.
     *
     * @param view a nézet, amely tartalmazza a felhasználó adatait és azoknak mezőit.
     */
    private void init(View view) {
        profileLastName = view.findViewById(R.id.profileLastName);
        profileFirstName = view.findViewById(R.id.profileFirstName);
        profileEmail = view.findViewById(R.id.profileEmail);
        profilePassword = view.findViewById(R.id.profilePassword);

        saveProfileButton = view.findViewById(R.id.saveProfileButton);
        saveProfileButton.setEnabled(!hasChanges());

        user = ((MainPage) requireActivity()).getUser();
        originalLastName = user.getLast_name();
        originalFirstName = user.getFirst_name();
        originalEmail = user.getEmail();
    }
}
