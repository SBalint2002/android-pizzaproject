package hu.pizzavalto.pizzaproject.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
 * Egy {@link Fragment} alosztály, amely megjeleníti a rendelési előzményt rendelési adatokkal.
 * Az új rendelés ami még nem zárult le az piros, a többi szürke.
 */
public class OrderHistoryFragment extends Fragment {

    /**
     * Előzményeket tartalmazó konténer.
     */
    private LinearLayout orderContainer;

    /**
     * Ha nem volt még korábban rendelés akkor ezt a szöveget jeleníti meg.
     */
    private TextView emptyOrdersText;


    /**
     * Az onCreateView metódus feladata a fragment layoutjának inicializálása és azon belül az összes view elem
     * inicializálása.
     *
     * @param inflater           Az inflater objektum segítségével hozható létre a fragment layoutja.
     * @param container          A konténer objektum, amelybe a fragment layoutja helyezhető.
     * @param savedInstanceState A Bundle objektum, amely a fragment előzőleg mentett állapotát tartalmazza,
     *                           amennyiben van ilyen.
     * @return Az inicializált layoutot tartalmazó View objektum.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);

        init(view);

        getOrderHistory();

        return view;
    }

    /**
     * A megrendelési előzmények lekérdezéséhez szükséges adatokat tartalmazó függvény.
     * Először ellenőrzi az access tokent, majd ennek megfelelően kéri le az adatokat az API szolgáltatásból.
     * Az adatok sikeres lekérése esetén feltölti az adatokkal a megfelelő View-kat és megjeleníti őket a megfelelő helyen.
     * Ha az adatok lekérdezése sikertelen, kezeli az esetleges hibakódokat.
     */
    private void getOrderHistory() {
        TokenUtils tokenUtils = new TokenUtils(requireActivity());
        String accessToken = tokenUtils.getAccessToken();
        if (accessToken == null) {
            navigateToLoginActivity();
            return;
        }

        ApiService apiService = new NetworkService().getRetrofit().create(ApiService.class);
        apiService.getOrders("Bearer " + accessToken).enqueue(new Callback<List<Order>>() {
            /**
             * Sikeres API hívás esetén ez a metódus fut le.
             *
             * @param call Válaszként várt Rendelés típusú lista.
             * @param response A hívás válasza ami tartalmazza a hívás adatait mint például státusz kód, válasz teste..stb
             */
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

            /**
             * Sikertelen API hívás esetén ez a metódus fut le.
             *
             * @param call Válaszként várt rendelés típusú lista.
             * @param t visszadobott hibaüzenet.
             */
            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
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
     * Inicializálja a rendeléskonténert és az üres rendelésre vonatkozó szöveget a megadott {@code view}-val.
     * Ezek a megjelenítési elemek szolgálnak a felhasználó rendelési előzményeinek megjelenítésére.
     *
     * @param view a nézet, amely tartalmazza a rendeléskonténert és az üres rendelésre vonatkozó szöveget
     */
    private void init(View view) {
        orderContainer = view.findViewById(R.id.orderContainer);
        emptyOrdersText = view.findViewById(R.id.emptyOrdersText);
    }
}