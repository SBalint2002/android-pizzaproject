package hu.pizzavalto.pizzaproject.components;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.pizzavalto.pizzaproject.R;
import hu.pizzavalto.pizzaproject.auth.JwtResponse;
import hu.pizzavalto.pizzaproject.model.OrderDto;
import hu.pizzavalto.pizzaproject.model.PizzaViewModel;
import hu.pizzavalto.pizzaproject.retrofit.NetworkService;
import hu.pizzavalto.pizzaproject.retrofit.UserApi;
import hu.pizzavalto.pizzaproject.sharedpreferences.TokenUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {
    private Button backButton, orderButton;
    private PizzaViewModel pizzaViewModel;
    private TextInputEditText address_input, phone_input;
    private Dialog orderAddedDialog;

    @Override
    protected void onStop() {
        super.onStop();
        if (orderAddedDialog != null && orderAddedDialog.isShowing()) {
            orderAddedDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        init();

        backButton.setOnClickListener(view -> {
            finish();
        });

        orderButton.setOnClickListener(view -> {
            order();
        });

    }

    private void order(){
        TokenUtils tokenUtils = new TokenUtils(OrderActivity.this);
        String accessToken = tokenUtils.getAccessToken();
        if (accessToken == null) {
            finish();
        }
        HashMap<Long, Integer> pizzaIds = (HashMap<Long, Integer>) getIntent().getSerializableExtra("pizzaIds");
        List<Long> pizzaIdList = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : pizzaIds.entrySet()) {
            Long pizzaId = entry.getKey();
            Integer quantity = entry.getValue();
            for (int i = 0; i < quantity; i++) {
                pizzaIdList.add(pizzaId);
            }
        }

        String location = address_input.getText().toString();
        String phoneNumber = phone_input.getText().toString();
        OrderDto orderDto = new OrderDto(location, phoneNumber, pizzaIdList);
        UserApi userApi = new NetworkService().getRetrofit().create(UserApi.class);
        System.out.println("OrderDto{location='" + orderDto.getLocation() + "', phoneNumber='" + orderDto.getPhoneNumber() + "', pizzaIds=" + orderDto.getPizzaIds().toString() + "}");
        userApi.addOrder("Bearer " + accessToken, orderDto).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    orderAddedDialog = new Dialog(OrderActivity.this);
                    View dialogView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.successful_order, null);
                    orderAddedDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    orderAddedDialog.setContentView(dialogView);

                    Window window = orderAddedDialog.getWindow();
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
                    window.setAttributes(params);

                    Button okButton = dialogView.findViewById(R.id.btn_ok);

                    okButton.setOnClickListener(add -> {
                        pizzaViewModel.clear();
                        orderAddedDialog.dismiss();
                        if(MainPage.instance != null) {
                            MainPage.instance.finish();
                        }
                        Intent intent = new Intent(OrderActivity.this, MainPage.class);
                        startActivity(intent);
                        finish();
                    });

                    orderAddedDialog.show();
                }else{
                    handleResponseCode(response.code(), tokenUtils, userApi);
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                System.out.println(t);
                finish();
            }
        });
    }

    private void handleResponseCode(int code, TokenUtils tokenUtils, UserApi userApi) {
        if (code == 451) {
            handleTokenRefresh(tokenUtils, userApi);
        } else {
            navigateToLoginActivity();
        }
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
            public void onResponse(@NonNull Call<JwtResponse> call, @NonNull Response<JwtResponse> response) {
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

            @Override
            public void onFailure(@NonNull Call<JwtResponse> call, @NonNull Throwable t) {
                navigateToLoginActivity();
            }
        });
    }

    private void navigateToLoginActivity() {
        startActivity(new Intent(OrderActivity.this, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    private void init(){
        backButton = findViewById(R.id.backButton);
        orderButton = findViewById(R.id.orderButton);

        address_input = findViewById(R.id.address_input);
        phone_input = findViewById(R.id.phone_input);

        pizzaViewModel = new ViewModelProvider(this).get(PizzaViewModel.class);

    }
}