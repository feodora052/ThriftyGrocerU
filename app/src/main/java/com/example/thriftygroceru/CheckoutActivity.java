
package com.example.thriftygroceru;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class CheckoutActivity extends AppCompatActivity {
    private Button confirmOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        confirmOrderButton = findViewById(R.id.confirmOrderButton);

        confirmOrderButton.setOnClickListener(v -> {

            Toast.makeText(this, "Order confirmed!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

