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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import hu.pizzavalto.pizzaproject.R;
import hu.pizzavalto.pizzaproject.fragments.ProfileFragment;
import hu.pizzavalto.pizzaproject.model.JwtResponse;
import hu.pizzavalto.pizzaproject.model.User;
import hu.pizzavalto.pizzaproject.retrofit.NetworkService;
import hu.pizzavalto.pizzaproject.retrofit.UserApi;
import hu.pizzavalto.pizzaproject.sharedpreferences.TokenUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPage extends AppCompatActivity {
    private TextView profileNameTextView;
    private TextView profileRoleTextView;
    private TextView textTitle;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        //Drawer Layout
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        findViewById(R.id.imageMenu).setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        //NavigationView
        NavigationView navigationView = findViewById(R.id.navigationView);

        //Maradjon az összes ikon fekete még ha ki is van jelölve
        navigationView.setItemIconTintList(null);

        //Navigation Header
        View headerView = navigationView.getHeaderView(0);
        profileNameTextView = headerView.findViewById(R.id.ProfileName);
        profileRoleTextView = headerView.findViewById(R.id.ProfileRole);

        Menu menu = navigationView.getMenu();

        //Kilépés
        MenuItem logoutMenuItem = menu.findItem(R.id.menuLogout);
        logoutMenuItem.setOnMenuItemClickListener(menuItem -> {
            showLogoutConfirmationDialog();
            return true;
        });

        getUserInformation();

        //Appbar cím változik az aktív fragment label-jére
        textTitle = findViewById(R.id.textTitle);
        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            textTitle.setText(destination.getLabel());
        });
    }

    public User getUser(){
        return user;
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Biztosan ki akarsz lépni?")
                .setPositiveButton("Igen", (dialogInterface, i) -> {
                    TokenUtils tokenUtils = new TokenUtils(MainPage.this);
                    tokenUtils.clearTokens();
                    navigateToLoginActivity();
                })
                .setNegativeButton("Nem", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }

    private void setBundle(User user) {
        Bundle bundle = new Bundle();
        bundle.putString("first_name", user.getFirst_name());
        bundle.putString("last_name", user.getLast_name());
        bundle.putString("email", user.getEmail());
        bundle.putString("admin", user.isAdmin() ? "true" : "false");

        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(bundle);
    }

    public void getUserInformation() {
        TokenUtils tokenUtils = new TokenUtils(MainPage.this);
        String accessToken = tokenUtils.getAccessToken();
        if (accessToken == null) {
            navigateToLoginActivity();
            return;
        }

        NetworkService networkService = new NetworkService();
        UserApi userApi = networkService.getRetrofit().create(UserApi.class);
        userApi.getUserInformation("Bearer " + accessToken).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 451) {
                        handleTokenRefresh(tokenUtils, userApi);
                    } else if (response.code() == 401) {
                        navigateToLoginActivity();
                    } else {
                        navigateToLoginActivity();
                    }
                    return;
                }
                User user = response.body();
                profileNameTextView.setText(user.getFirst_name());
                profileRoleTextView.setText(user.isAdmin() ? "Admin" : "Felhasználó");
                profileRoleTextView.setTextColor(Color.parseColor(user.isAdmin() ? "#FF0000" : "#00FF00"));
                setBundle(user);
                MainPage.this.user = user;
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                navigateToLoginActivity();
            }
        });
    }

    private void handleTokenRefresh(TokenUtils tokenUtils, UserApi userApi) {
        String refreshToken = tokenUtils.getRefreshToken();
        if (refreshToken == null) {
            System.out.println("Hiányzó refreshtoken");
            navigateToLoginActivity();
            return;
        }

        TokenUtils.refreshUserToken(tokenUtils, userApi, new Callback<JwtResponse>() {
            @Override
            public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                if (!response.isSuccessful()) {
                    navigateToLoginActivity();
                    return;
                }

                JwtResponse jwtResponse = response.body();
                tokenUtils.saveAccessToken(jwtResponse.getJwttoken());
                tokenUtils.setRefreshToken(jwtResponse.getRefreshToken());
                getUserInformation();
            }

            @Override
            public void onFailure(Call<JwtResponse> call, Throwable t) {
                navigateToLoginActivity();
            }
        });
    }

    private void navigateToLoginActivity() {
        //Intent
        startActivity(new Intent(MainPage.this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }
}