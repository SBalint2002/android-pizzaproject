package hu.pizzavalto.pizzaproject.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import hu.pizzavalto.pizzaproject.R;
import hu.pizzavalto.pizzaproject.components.LoginActivity;
import hu.pizzavalto.pizzaproject.components.MainPage;
import hu.pizzavalto.pizzaproject.auth.JwtResponse;
import hu.pizzavalto.pizzaproject.model.User;
import hu.pizzavalto.pizzaproject.retrofit.UserApi;
import hu.pizzavalto.pizzaproject.sharedpreferences.TokenUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private EditText profileLastName, profileFirstName, profileEmail;
    private Button saveProfileButton;
    private String originalLastName, originalFirstName, originalEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        init(view);

        User user = ((MainPage) requireActivity()).getUser();
        profileLastName.setText(user.getLast_name());
        profileFirstName.setText(user.getFirst_name());
        profileEmail.setText(user.getEmail());

        // Add a TextWatcher to detect changes in any of the EditText fields.
        profileLastName.addTextChangedListener(textWatcher);
        profileFirstName.addTextChangedListener(textWatcher);
        profileEmail.addTextChangedListener(textWatcher);

        saveProfileButton.setOnClickListener(saveProfile -> {
            //saveUser();
        });

        return view;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            // Enable the save button when any of the EditText fields have changed.
            saveProfileButton.setEnabled(hasChanges());
        }
    };

    private void init(View view) {
        profileLastName = view.findViewById(R.id.profileLastName);
        profileFirstName = view.findViewById(R.id.profileFirstName);
        profileEmail = view.findViewById(R.id.profileEmail);

        saveProfileButton = view.findViewById(R.id.saveProfileButton);
        saveProfileButton.setEnabled(!hasChanges());

        User user = ((MainPage) requireActivity()).getUser();
        originalLastName = user.getLast_name();
        originalFirstName = user.getFirst_name();
        originalEmail = user.getEmail();
    }

    private boolean hasChanges() {
        String currentLastName = profileLastName.getText().toString().trim();
        String currentFirstName = profileFirstName.getText().toString().trim();
        String currentEmail = profileEmail.getText().toString().trim();

        return !currentLastName.equals(originalLastName) || !currentFirstName.equals(originalFirstName) || !currentEmail.equals(originalEmail);
    }

    //getAllPizzas helyett save user
    /*private void saveUser(){
        TokenUtils tokenUtils = new TokenUtils(getActivity());
        String accessToken = tokenUtils.getAccessToken();
        if (accessToken == null) {
            navigateToLoginActivity();
            return;
        }
        System.out.println(accessToken);

        NetworkService networkService = new NetworkService();
        UserApi userApi = networkService.getRetrofit().create(UserApi.class);
        userApi.getAllPizzas().enqueue(new Callback<Pizza>() {
            @Override
            public void onResponse(Call<Pizza> call, Response<Pizza> response) {
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
                Gson gson = new Gson();
                Type type = new TypeToken<List<Pizza>>(){}.getType();
                List<Pizza> pizzas = gson.fromJson(response.body().toString(), type);

                System.out.println(pizzas);
            }

            @Override
            public void onFailure(Call<Pizza> call, Throwable t) {
                navigateToLoginActivity();
            }
        });
    }*/

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
                if (!response.isSuccessful()) {
                    navigateToLoginActivity();
                    return;
                }
                JwtResponse jwtResponse = response.body();
                if (jwtResponse != null) {
                    tokenUtils.saveAccessToken(jwtResponse.getJwttoken());
                }
                if (jwtResponse != null) {
                    tokenUtils.setRefreshToken(jwtResponse.getRefreshToken());
                }
                //saveUser();
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
}
