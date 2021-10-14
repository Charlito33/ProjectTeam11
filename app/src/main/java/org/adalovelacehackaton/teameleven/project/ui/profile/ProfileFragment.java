package org.adalovelacehackaton.teameleven.project.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.adalovelacehackaton.teameleven.project.LoginActivity;
import org.adalovelacehackaton.teameleven.project.R;
import org.adalovelacehackaton.teameleven.project.api.ProjectAPI;
import org.adalovelacehackaton.teameleven.project.api.User;
import org.adalovelacehackaton.teameleven.project.databinding.FragmentProfileBinding;
import org.adalovelacehackaton.teameleven.project.ui.rewards.RewardsViewModel;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private SharedPreferences sharedPreferences;

    private String accessToken;

    private String username;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Default Init
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Activity != null check
        if (getActivity() == null) {
            System.err.println("Error when initializing Fragment !");
            Toast.makeText(getContext(), "Fragment Error", Toast.LENGTH_SHORT).show();
            return null;
        }

        // Init Shared Preferences with Global Value
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);

        // Check access_token (And by the way username...)
        accessToken = sharedPreferences.getString("access_token", "null");
        if (accessToken.equals("null")) {
            // ReLogin
            connect();
        }

        // Get values
        username = sharedPreferences.getString("username", "null");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        // View != null check
        if (getView() == null) {
            System.err.println("Error when initializing Fragment !");
            Toast.makeText(getContext(), "Fragment Error", Toast.LENGTH_SHORT).show();
            return;
        }

        Button disconnectButton = getView().findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply();
            Toast.makeText(getContext(), "Disconnected !", Toast.LENGTH_SHORT).show();
            connect();
        });

        // Data to Update
        TextView textView_username = (TextView) getView().findViewById(R.id.textView_username);
        textView_username.setText(username);
    }

    private void connect() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.putExtra("reason", "disconnected");
        startActivity(intent);
    }
}