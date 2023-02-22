package hu.pizzavalto.pizzaproject.components;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import hu.pizzavalto.pizzaproject.R;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //TODO: ANIMÁCIÓ
        try {
            Thread.sleep(2000); //2 másodpercig várjon
            navigateToMainPage();
        } catch (InterruptedException e) {
            System.out.println("szar");
        }
    }

    private void navigateToMainPage() {
        Intent intent = new Intent(this, MainPage.class);
        startActivity(intent);
        finish();
    }
}