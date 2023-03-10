package hu.pizzavalto.pizzaproject.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import hu.pizzavalto.pizzaproject.R;
import hu.pizzavalto.pizzaproject.components.MainPage;
import hu.pizzavalto.pizzaproject.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    private EditText profileLastName, profileFirstName, profileEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        init(view);

        User user = ((MainPage)getActivity()).getUser();
        profileLastName.setText(user.getLast_name());
        profileFirstName.setText(user.getFirst_name());
        profileEmail.setText(user.getEmail());

        return view;
    }

    private void init(View view) {
        profileLastName = view.findViewById(R.id.profileLastName);
        profileFirstName = view.findViewById(R.id.profileFirstName);
        profileEmail = view.findViewById(R.id.profileEmail);
    }
}