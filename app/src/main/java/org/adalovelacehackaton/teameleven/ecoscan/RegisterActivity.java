package org.adalovelacehackaton.teameleven.ecoscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.adalovelacehackaton.teameleven.ecoscan.api.ProjectAPI;

public class RegisterActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private Button buttonRegister;

    private EditText editText_username;
    private EditText editText_email;
    private EditText editText_firstname;
    private EditText editText_lastname;
    private EditText editText_password;
    private EditText editText_passwordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_key), MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        TextView textView_login = findViewById(R.id.register_textView_login);
        textView_login.setOnClickListener((v) -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

            finish();
        });

        editText_username =         findViewById(R.id.register_editText_username);
        editText_email =            findViewById(R.id.register_editText_email);
        editText_firstname =        findViewById(R.id.register_editText_firstname);
        editText_lastname =         findViewById(R.id.register_editText_lastname);
        editText_password =         findViewById(R.id.register_editText_password);
        editText_passwordConfirm =  findViewById(R.id.register_editText_password_confirm);


        buttonRegister = findViewById(R.id.register_button_register);
        buttonRegister.setOnClickListener((v) -> {
            String username = editText_username.getText().toString();
            String email = editText_email.getText().toString();
            String firstname = editText_firstname.getText().toString();
            String lastname = editText_lastname.getText().toString();
            String password = editText_password.getText().toString();
            String passwordConfirm = editText_passwordConfirm.getText().toString();

            ProjectAPI.registerUser(username, email, firstname, lastname, password, passwordConfirm, (responseCode, data) -> {
                if (responseCode == 200) {
                    // Access Valid !

                    Toast.makeText(getApplicationContext(), "Account Created !", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);

                    finish();
                } else if (responseCode == 400) { // Invalid Username or Password || No API key provided
                    if (data.equals("API key not provided")) {
                        Toast.makeText(getApplicationContext(), "Java API broken !", Toast.LENGTH_SHORT).show();
                    } else if (data.equals("Passwords aren't the same !")) {
                        Toast.makeText(getApplicationContext(), "Passwords aren't the same !", Toast.LENGTH_SHORT).show();
                    } else if (data.equals("Username already exists !")) {
                        Toast.makeText(getApplicationContext(), "Username is taken !", Toast.LENGTH_SHORT).show();
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
    }
}