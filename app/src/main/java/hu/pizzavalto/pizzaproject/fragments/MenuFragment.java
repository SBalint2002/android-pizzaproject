package hu.pizzavalto.pizzaproject.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import hu.pizzavalto.pizzaproject.R;
import hu.pizzavalto.pizzaproject.components.LoginActivity;
import hu.pizzavalto.pizzaproject.model.Pizza;
import hu.pizzavalto.pizzaproject.model.PizzaViewModel;
import hu.pizzavalto.pizzaproject.retrofit.NetworkService;
import hu.pizzavalto.pizzaproject.retrofit.UserApi;
import hu.pizzavalto.pizzaproject.sharedpreferences.TokenUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {
    private LinearLayout pizzasContainer;
    private int number = 1;
    private TextView numberTextView;
    private List<Pizza> pizzas = new ArrayList<>();
    private PizzaViewModel pizzaViewModel;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        pizzasContainer = view.findViewById(R.id.pizzasContainer);
        pizzaViewModel = new ViewModelProvider(requireActivity()).get(PizzaViewModel.class);
        getAllPizzas();


        return view;
    }

    private void getAllPizzas() {
        TokenUtils tokenUtils = new TokenUtils(requireActivity());
        String accessToken = tokenUtils.getAccessToken();
        if (accessToken == null) {
            navigateToLoginActivity();
            return;
        }

        NetworkService networkService = new NetworkService();
        UserApi userApi = networkService.getRetrofit().create(UserApi.class);
        userApi.getAllPizzas().enqueue(new Callback<List<Pizza>>() {
            @Override
            public void onResponse(@NonNull Call<List<Pizza>> call, @NonNull Response<List<Pizza>> response) {
                if (!response.isSuccessful()) {
                    if (response.code() == 401) {
                        System.out.println("Nincs hozzáférés 401-es status kód");
                        navigateToLoginActivity();
                    } else {
                        System.out.println("Valami más status code" + response.code());
                        navigateToLoginActivity();
                    }
                    return;
                }

                pizzas = response.body();
                pizzaViewModel.setPizzas(pizzas);

                for (Pizza pizza : pizzas) {
                    if (pizza.isAvailable()) {
                        View pizzaView = LayoutInflater.from(getActivity()).inflate(R.layout.item_pizza, (ViewGroup) getView(), false);

                        ImageView imageView = pizzaView.findViewById(R.id.image_view);
                        Picasso.get().load(pizza.getPicture()).into(imageView);

                        TextView nameTextView = pizzaView.findViewById(R.id.name_text_view);
                        nameTextView.setText(pizza.getName());

                        TextView priceTextView = pizzaView.findViewById(R.id.price_text_view);
                        String priceString = getString(R.string.pizza_price, pizza.getPrice());
                        priceTextView.setText(priceString);

                        TextView descriptionTextView = pizzaView.findViewById(R.id.description_text_view);
                        descriptionTextView.setText(pizza.getDescription());

                        Button orderButton = pizzaView.findViewById(R.id.order_button);

                        orderButton.setOnClickListener(order -> {
                            Dialog orderDialog = new Dialog(getActivity());
                            View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.order_dialog, (ViewGroup) getView(), false);
                            orderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            orderDialog.setContentView(dialogView);

                            Window window = orderDialog.getWindow();
                            WindowManager.LayoutParams params = window.getAttributes();
                            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
                            window.setAttributes(params);

                            numberTextView = dialogView.findViewById(R.id.text_number);
                            Button minusButton = dialogView.findViewById(R.id.button_minus);
                            Button plusButton = dialogView.findViewById(R.id.button_plus);
                            Button addButton = dialogView.findViewById(R.id.btn_yes);
                            Button closeButton = dialogView.findViewById(R.id.btn_no);

                            minusButton.setOnClickListener(decrease -> {
                                number--;
                                if (number < 1) {
                                    number = 1;
                                }
                                numberTextView.setText(String.valueOf(number));
                            });

                            plusButton.setOnClickListener(increase -> {
                                number++;
                                numberTextView.setText(String.valueOf(number));
                            });

                            addButton.setOnClickListener(add -> {
                                for (int i = 0; i < number; i++) {
                                    pizzaViewModel.addPizza(pizza);
                                }
                                number = 1;
                                orderDialog.dismiss();
                            });

                            closeButton.setOnClickListener(close -> {
                                number = 1;
                                orderDialog.dismiss();
                            });

                            orderDialog.show();
                        });

                        pizzasContainer.addView(pizzaView);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Pizza>> call, @NonNull Throwable t) {
                navigateToLoginActivity();
            }
        });
    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}