package org.adalovelacehackaton.teameleven.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.adalovelacehackaton.teameleven.project.api.ProjectAPI;
import org.adalovelacehackaton.teameleven.project.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SharedPreferences sharedPreferences;

    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
        checkLoggedInState();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_rewards, R.id.navigation_profile)
                .build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);

        NavController navController = null;

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        } else {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        }

        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    protected void onStart() {
        super.onStart();

        accessToken = sharedPreferences.getString("access_token", "null");
        if (accessToken.equals("null")) {
            // ReLogin
            connect();
        }

        if (sharedPreferences.getBoolean("toUpdate", false)) {
            updateUserData();
        }
    }

    private void updateUserData() {
        // Get Userdata and change values
        ProjectAPI.getUser(accessToken, (responseCode, data, user) -> {
            if (responseCode == 200) {
                sharedPreferences.edit().putString("username", user.getUsername()).apply();
            } else {
                System.err.println("Error when trying to get userdata !");
                System.err.println(data);
            }
        });
    }

    private void checkLoggedInState() {
        String loginToken = sharedPreferences.getString("access_token", "null");

        if (loginToken.equals("null")) {
            // Connect
            connect();
        } else {
            // Connected, check token

            updateUserData();
        }
    }

    private void connect() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("reason", "disconnected");
        startActivity(intent);
    }
}