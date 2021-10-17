package org.adalovelacehackaton.teameleven.ecoscan.ui.rewards;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.adalovelacehackaton.teameleven.ecoscan.LoginActivity;
import org.adalovelacehackaton.teameleven.ecoscan.R;
import org.adalovelacehackaton.teameleven.ecoscan.api.ProjectAPI;
import org.adalovelacehackaton.teameleven.ecoscan.databinding.FragmentRewardsBinding;

public class RewardsFragment extends Fragment {

    private RewardsViewModel dashboardViewModel;
    private FragmentRewardsBinding binding;

    private SharedPreferences sharedPreferences;

    private String accessToken;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(RewardsViewModel.class);

        binding = FragmentRewardsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Init Shared Preferences with Global Value
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);

        // Check access_token (And by the way username...)
        accessToken = sharedPreferences.getString("access_token", "null");
        if (accessToken.equals("null")) {
            // ReLogin
            connect();
        }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        ProjectAPI.getUserRanking(accessToken, (responseCode, data) -> {
            if (responseCode == 200) {
                // Data to Update
                TextView textView_ranking = (TextView) getView().findViewById(R.id.textView_ranking);
                textView_ranking.post(() -> {
                    textView_ranking.setText(data);
                });
            } else {
                System.err.println("Error when trying to get user ranking !");
                System.err.println(data);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void connect() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.putExtra("reason", "disconnected");
        startActivity(intent);
    }
}