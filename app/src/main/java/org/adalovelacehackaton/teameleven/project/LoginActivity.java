package org.adalovelacehackaton.teameleven.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.adalovelacehackaton.teameleven.project.R;
import org.adalovelacehackaton.teameleven.project.api.ProjectAPI;

public class LoginActivity extends AppCompatActivity {

    private Intent intent;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);

        intent = getIntent();
    }


    @Override
    protected void onStart() {
        super.onStart();

        Button button_login_submit = (Button) findViewById(R.id.button_login_submit);
        button_login_submit.setOnClickListener(v -> {
            EditText edit_text_username = (EditText) findViewById(R.id.editText_username);
            EditText edit_text_password = (EditText) findViewById(R.id.editText_password);

            String username = edit_text_username.getText().toString();
            String password = edit_text_password.getText().toString();

            ProjectAPI.loginUser(username, password, (responseCode, data) -> {
                if (responseCode == 200) {
                    // Access Valid !

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("access_token", data);
                    editor.putBoolean("toUpdate", true);
                    editor.apply();

                    Toast.makeText(getApplicationContext(), "Connected !", Toast.LENGTH_SHORT).show();

                    finish();
                } else if (responseCode == 400) { // Invalid Username or Password || No API key provided
                    if (data.equals("API key not provided")) {
                        Toast.makeText(getApplicationContext(), "Java API broken !", Toast.LENGTH_SHORT).show();
                    } else if (data.equals("Invalid Username or Password !")) {
                        Toast.makeText(getApplicationContext(), "Invalid Username or Password !", Toast.LENGTH_SHORT).show();
                    }
                } else if (responseCode == 401) { // API Key Error
                    if (data.equals("Invalid API Key !") || data.equals("You need greater Access Level to use this endpoint !")) {
                        Toast.makeText(getApplicationContext(), "Java API Key Invalid !", Toast.LENGTH_SHORT).show();
                    }
                } else if (responseCode == 500) {
                    if (data.equals("Server error, try again later !")) { // Can't connect to DB or any server error
                        Toast.makeText(getApplicationContext(), "Server error, please try again later !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        String reason = intent.getStringExtra("reason");
        if (reason.equals("disconnected")) {
            Toast.makeText(getApplicationContext(), "Login before !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (!sharedPreferences.getString("access_token", "null").equals("null")) {
            super.onBackPressed();
        }
    }
}