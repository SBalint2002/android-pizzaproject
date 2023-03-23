package hu.pizzavalto.pizzaproject.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import hu.pizzavalto.pizzaproject.R;
import hu.pizzavalto.pizzaproject.components.LoginActivity;
import hu.pizzavalto.pizzaproject.components.MainPage;
import hu.pizzavalto.pizzaproject.auth.JwtResponse;
import hu.pizzavalto.pizzaproject.model.User;
import hu.pizzavalto.pizzaproject.retrofit.NetworkService;
import hu.pizzavalto.pizzaproject.retrofit.UserApi;
import hu.pizzavalto.pizzaproject.sharedpreferences.TokenUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private EditText profileLastName, profileFirstName, profileEmail, profilePassword;
    private Button saveProfileButton;
    private String originalLastName, originalFirstName, originalEmail;
    private User user;
    private User modifyUser;

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
            if (!profilePassword.getText().toString().isEmpty()){
                if (profilePassword.getText().length() < 6){
                    System.out.println("Legalább 6 karakter legyen");
                    return;
                }
                password = profilePassword.getText().toString();
            }
            modifyUser = new User(
                    user.getId(),
                    profileFirstName.getText().toString(),
                    profileLastName.getText().toString(),
                    profileEmail.getText().toString(),
                    password,
                    user.getRole());
            saveUserInformation();
        });

        return view;
    }

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

    private boolean hasChanges() {
        String currentLastName = profileLastName.getText().toString().trim();
        String currentFirstName = profileFirstName.getText().toString().trim();
        String currentEmail = profileEmail.getText().toString().trim();
        String currentPassword = profilePassword.getText().toString().trim();

        return !currentLastName.equals(originalLastName)
                || !currentFirstName.equals(originalFirstName)
                || !currentEmail.equals(originalEmail)
                || !currentPassword.isEmpty();
    }

    private void saveUserInformation() {
        TokenUtils tokenUtils = new TokenUtils(requireActivity());
        String accessToken = tokenUtils.getAccessToken();
        if (accessToken == null) {
            navigateToLoginActivity();
            return;
        }

        UserApi userApi = new NetworkService().getRetrofit().create(UserApi.class);
        userApi.saveUser("Bearer " + accessToken, user.getId(), modifyUser).enqueue(new Callback<ResponseBody>() {
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
                    handleResponseCode(response.code(), tokenUtils, userApi);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                navigateToLoginActivity();
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
