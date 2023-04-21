package hu.pizzavalto.pizzaproject.components;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Applikáció indulásakor a töltés alatt jeleníti meg a töltő képernyőt (SplashScreen)
 * Majd átirányít a MainPage oldalra.
 */
public class SplashScreen extends AppCompatActivity {

    /**
     * Létrehozza az alkalmazást, beállítja a kijelző orientációját és megjeleníti a Splash Screen-t,
     * amíg a program betöltődik. Ezután megjeleníti a Főoldalt és bezárja a Splash Screen-t.
     *
     * @param savedInstanceState Ha az Activity újra inicializálódik
     *                           az előzőleg elmentett állapot visszaállításához szükséges adatokat tartalmazza.
     *                           <b><i>Megjegyzés: Ellenkező esetben null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);

        // Amíg tölt a program addig a SplashScreen látszik.
        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this, MainPage.class));
            finish();
        }, 0);
    }
}