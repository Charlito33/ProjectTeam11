package org.adalovelacehackaton.teameleven.ecoscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.adalovelacehackaton.teameleven.ecoscan.api.Item;
import org.adalovelacehackaton.teameleven.ecoscan.api.ItemType;
import org.adalovelacehackaton.teameleven.ecoscan.api.ProjectAPI;

public class AddItemActivity extends AppCompatActivity {

    private String scancode;
    private TextView textViewScancode;
    private Button buttonAddItem;

    private EditText editTextName;
    private Spinner spinnerType;
    private EditText editTextWeight;

    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        scancode = getIntent().getStringExtra("scancode");
        textViewScancode = findViewById(R.id.textView_name);
        textViewScancode.setText(scancode);

        spinnerType = (Spinner)  findViewById(R.id.spinner_itemType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        editTextName = (EditText) findViewById(R.id.editText_name);
        editTextWeight = (EditText) findViewById(R.id.editText_weight);

        // Check access_token (And by the way username...)
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString("access_token", "null");
        if (accessToken.equals("null")) {
            // ReLogin
            connect();
        }

        buttonAddItem = findViewById(R.id.button_addItem);
        buttonAddItem.setOnClickListener(v -> {
            if (editTextName.length() == 0 || editTextWeight.length() == 0) {
                Toast.makeText(getApplicationContext(), "Please fill all fields !", Toast.LENGTH_SHORT).show();
            } else {
                ItemType type;

                switch ((int) spinnerType.getSelectedItemId()) {
                    case 0:
                        type = ItemType.BIO_DEGRADABLE;
                        break;
                    case 1:
                        type = ItemType.CARDBOARD_PAPER;
                        break;
                    case 2:
                        type = ItemType.OTHER;
                        break;
                    case 3:
                        type = ItemType.GLASS;
                        break;
                    case 4:
                        type = ItemType.PLASTIC;
                        break;
                    case 5:
                        type = ItemType.TEXTILE;
                        break;
                    case 6:
                        type = ItemType.METAL;
                        break;
                    default:
                        type = null;
                }

                ProjectAPI.addItem(new Item(-1, scancode, editTextName.getText().toString(), type, Integer.parseInt(editTextWeight.getText().toString()), -1), (responseCode, data) -> {
                    if (responseCode == 200) {
                        ProjectAPI.getItem(scancode, (responseCode1, data1, item) -> {
                            if (responseCode1 == 200) {
                                ProjectAPI.logItemToUserAccount(accessToken, item);
                            } else {
                                System.err.println("Error when trying to add item to user account !");
                                System.err.println(data);
                                Toast.makeText(getApplicationContext(), "Error when trying to add item to your account !", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Toast.makeText(getApplicationContext(), "Added Item with Success !", Toast.LENGTH_SHORT).show();

                        finish();
                    } else if (responseCode == 400) {
                        if (data.equals("Fake item !")) {
                            Toast.makeText(getApplicationContext(), "Server detected fake item, please correct data !", Toast.LENGTH_SHORT).show();
                        } else {
                            System.err.println("Error when trying to add item !");
                            System.err.println(data);
                            Toast.makeText(getApplicationContext(), "Error when trying to add item !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        System.err.println("Error when trying to add item !");
                        System.err.println(data);
                        Toast.makeText(getApplicationContext(), "Error when trying to add item !", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    private void connect() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("reason", "disconnected");
        startActivity(intent);
    }
}