package hu.pizzavalto.pizzaproject.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.pizzavalto.pizzaproject.R;
import hu.pizzavalto.pizzaproject.auth.JwtResponse;
import hu.pizzavalto.pizzaproject.components.LoginActivity;
import hu.pizzavalto.pizzaproject.model.Order;
import hu.pizzavalto.pizzaproject.model.PizzaDto;
import hu.pizzavalto.pizzaproject.retrofit.ApiService;
import hu.pizzavalto.pizzaproject.retrofit.NetworkService;
import hu.pizzavalto.pizzaproject.sharedpreferences.TokenUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderHistoryFragment extends Fragment {

    private LinearLayout orderContainer;
    private TextView emptyOrdersText;

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        init(view);

        getOrderHistory();

        return view;
    }

    private void getOrderHistory() {
        TokenUtils tokenUtils = new TokenUtils(requireActivity());
        String accessToken = tokenUtils.getAccessToken();
        if (accessToken == null) {
            navigateToLoginActivity();
            return;
        }

        ApiService apiService = new NetworkService().getRetrofit().create(ApiService.class);
        apiService.getOrders("Bearer " + accessToken).enqueue(new Callback<List<Order>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    List<Order> orders = response.body();
                    if (orders != null) {
                        for (int i = orders.size() - 1; i >= 0; i--) {
                            emptyOrdersText.setVisibility(View.GONE);
                            Order order = orders.get(i);
                            View orderView = LayoutInflater.from(getActivity()).inflate(R.layout.item_order, (ViewGroup) getView(), false);


                            orderView.findViewById(R.id.readyIndicator).setBackgroundColor(Color.parseColor(order.isReady() ? "#17171F" : "#800020"));

                            TextView date = orderView.findViewById(R.id.date);
                            date.setText(order.getOrder_date().substring(0, 10));

                            TextView dateTime = orderView.findViewById(R.id.dateTime);
                            dateTime.setText(order.getOrder_date().substring(11, 16));

                            TextView address = orderView.findViewById(R.id.address);
                            address.setText(order.getLocation());

                            TextView phoneNumber = orderView.findViewById(R.id.phone);
                            phoneNumber.setText(order.getPhone_number());

                            TextView pizzas = orderView.findViewById(R.id.pizzas);
                            Map<String, Integer> pizzaCount = new HashMap<>();
                            List<PizzaDto> pizzaList = order.getOrderPizzas();
                            for (PizzaDto pizza : pizzaList) {
                                String pizzaName = pizza.getPizza().getName();
                                if (pizzaCount.containsKey(pizzaName)) {
                                    pizzaCount.put(pizzaName, pizzaCount.get(pizzaName) + 1);
                                } else {
                                    pizzaCount.put(pizzaName, 1);
                                }
                            }
                            StringBuilder pizzasBuilder = new StringBuilder();
                            for (Map.Entry<String, Integer> entry : pizzaCount.entrySet()) {
                                pizzasBuilder.append(entry.getKey()).append(" ").append(entry.getValue()).append("x\n");
                            }
                            String pizzasText = pizzasBuilder.toString();
                            pizzas.setText(pizzasText);


                            TextView price = orderView.findViewById(R.id.price);
                            price.setText(order.getPrice() + " Ft");

                            orderContainer.addView(orderView);

                        }
                    } else {
                        emptyOrdersText.setVisibility(View.VISIBLE);
                    }

                } else {
                    handleResponseCode(response.code(), tokenUtils, apiService);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                navigateToLoginActivity();
            }
        });

    }

    private void handleResponseCode(int code, TokenUtils tokenUtils, ApiService apiService) {
        if (code == 451) {
            handleTokenRefresh(tokenUtils, apiService);
        } else {
            navigateToLoginActivity();
        }
    }

    private void handleTokenRefresh(TokenUtils tokenUtils, ApiService apiService) {
        String refreshToken = tokenUtils.getRefreshToken();
        if (refreshToken == null) {
            System.out.println("Hiányzó refreshtoken");
            navigateToLoginActivity();
            return;
        }

        TokenUtils.refreshUserToken(tokenUtils, apiService, new Callback<JwtResponse>() {
            @Override
            public void onResponse(@NonNull Call<JwtResponse> call, @NonNull Response<JwtResponse> response) {
                if (response.isSuccessful()) {
                    JwtResponse jwtResponse = response.body();
                    if (jwtResponse != null && jwtResponse.getAccessToken() != null) {
                        tokenUtils.saveAccessToken(jwtResponse.getAccessToken());
                        tokenUtils.setRefreshToken(jwtResponse.getRefreshToken());
                        getOrderHistory();
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
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void init(View view) {
        orderContainer = view.findViewById(R.id.orderContainer);
        emptyOrdersText = view.findViewById(R.id.emptyOrdersText);
    }
}