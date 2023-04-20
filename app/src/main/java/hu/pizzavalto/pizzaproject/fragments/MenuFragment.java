package hu.pizzavalto.pizzaproject.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import hu.pizzavalto.pizzaproject.retrofit.ApiService;
import hu.pizzavalto.pizzaproject.sharedpreferences.TokenUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {
    private LinearLayout pizzasContainer, pizzas2Container;
    private int number = 1;
    private TextView numberTextView;
    private List<Pizza> pizzas = new ArrayList<>();
    private PizzaViewModel pizzaViewModel;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        pizzasContainer = view.findViewById(R.id.pizzasContainer);
        pizzas2Container = view.findViewById(R.id.pizzas2Container);
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
        ApiService apiService = networkService.getRetrofit().create(ApiService.class);
        apiService.getAllPizzas().enqueue(new Callback<List<Pizza>>() {
            @Override
            public void onResponse(@NonNull Call<List<Pizza>> call, @NonNull Response<List<Pizza>> response) {
                if (!response.isSuccessful()) {
                    navigateToLoginActivity();
                    System.out.println("status code: " + response.code());
                    return;
                }

                pizzas = response.body();
                pizzaViewModel.setPizzas(pizzas);

                int nextindex = 0;

                for (int i = 0; i < pizzas.size(); i++) {
                    Pizza pizza = pizzas.get(i);
                    if (pizza.isAvailable()) {
                        View pizzaView = LayoutInflater.from(getActivity()).inflate(R.layout.item_pizza, (ViewGroup) getView(), false);

                        ImageView imageView = pizzaView.findViewById(R.id.image_view);
                        Picasso.get().load(pizza.getPicture()).into(imageView);

                        TextView nameTextView = pizzaView.findViewById(R.id.name_text_view);
                        nameTextView.setText(pizza.getName());

                        TextView priceTextView = pizzaView.findViewById(R.id.price_text_view);
                        String priceString = getString(R.string.pizza_price, pizza.getPrice());
                        priceTextView.setText(priceString);

                        Button orderButton = pizzaView.findViewById(R.id.order_button);
                        Button detailsButton = pizzaView.findViewById(R.id.details_button);

                        detailsButton.setOnClickListener(details -> {
                            Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.pizza_details_dialog);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

                            Window window = dialog.getWindow();
                            WindowManager.LayoutParams params = window.getAttributes();
                            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);

                            ImageView pizzaImageView = dialog.findViewById(R.id.pizza_image_view);
                            Picasso.get().load(pizza.getPicture()).into(pizzaImageView);

                            TextView pizzaNameTextView = dialog.findViewById(R.id.pizza_name_text_view);
                            pizzaNameTextView.setText(pizza.getName());
                            pizzaNameTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText));

                            TextView descriptionTextView = dialog.findViewById(R.id.pizza_description_text_view);
                            descriptionTextView.setText(pizza.getDescription());
                            descriptionTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryText));

                            Button closeButton = dialog.findViewById(R.id.close_button);
                            closeButton.setOnClickListener(view -> dialog.dismiss());

                            dialog.show();
                        });

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
                                for (int j = 0; j < number; j++) {
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

                        nextindex++;
                        if (nextindex % 2 != 0) {
                            pizzasContainer.addView(pizzaView);
                        } else {
                            pizzas2Container.addView(pizzaView);
                        }
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