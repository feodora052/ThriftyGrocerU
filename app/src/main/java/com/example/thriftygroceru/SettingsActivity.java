
package com.example.thriftygroceru;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText emailEditText;
    private Button saveButton;

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ThriftyGrocerUPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        saveButton = findViewById(R.id.saveButton);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load saved preferences
        String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
        String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");

        usernameEditText.setText(savedUsername);
        emailEditText.setText(savedEmail);

        saveButton.setOnClickListener(v -> savePreferences());
    }

    private void savePreferences() {
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.apply();

        Toast.makeText(this, "Preferences saved", Toast.LENGTH_SHORT).show();
    }
}

