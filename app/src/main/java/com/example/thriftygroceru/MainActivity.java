package com.example.thriftygroceru;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView productRecyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private DatabaseHelper databaseHelper;
    private Button cartButton, logoutButton, bluetoothButton, speechToTextButton, locationButton, settingsButton, profileButton, searchButton;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        initializeFirebase();

        loadProducts();

        setupButtonListeners();
    }


    private void initializeUI() {
        productRecyclerView = findViewById(R.id.productRecyclerView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        productAdapter = new ProductAdapter(productList, databaseHelper);
        productRecyclerView.setAdapter(productAdapter);

        cartButton = findViewById(R.id.cartButton);
        logoutButton = findViewById(R.id.logoutButton);
        bluetoothButton = findViewById(R.id.bluetoothButton);
        speechToTextButton = findViewById(R.id.speechToTextButton);
        locationButton = findViewById(R.id.locationButton);
        settingsButton = findViewById(R.id.settingsButton);
        profileButton = findViewById(R.id.profileButton);
        searchEditText = findViewById(R.id.searchEditText);
        searchButton = findViewById(R.id.searchButton);
    }


    private void initializeFirebase() {
        db = FirebaseFirestore.getInstance();
    }


    private void loadProducts() {
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        productList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            productList.add(product);
                        }
                        productAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load products.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void setupButtonListeners() {
        cartButton.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        bluetoothButton.setOnClickListener(v -> startActivity(new Intent(this, BluetoothActivity.class)));
        speechToTextButton.setOnClickListener(v -> startActivity(new Intent(this, SpeechToTextActivity.class)));
        locationButton.setOnClickListener(v -> startActivity(new Intent(this, LocationActivity.class)));
        settingsButton.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        searchButton.setOnClickListener(v -> searchProducts(searchEditText.getText().toString()));
    }


    private void searchProducts(String query) {
        List<Product> searchResults = new ArrayList<>();
        for (Product product : productList) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(product);
            }
        }

        productAdapter = new ProductAdapter(searchResults, databaseHelper);
        productRecyclerView.setAdapter(productAdapter);

        displaySuggestions(query);
    }


    private void displaySuggestions(String query) {
        List<Product> suggestions = getSuggestedProducts(query);

        if (!suggestions.isEmpty()) {
            StringBuilder suggestionText = new StringBuilder("You might also like: ");
            for (Product suggestion : suggestions) {
                suggestionText.append(suggestion.getName()).append(", ");
            }
            suggestionText.setLength(suggestionText.length() - 2);
            Toast.makeText(this, suggestionText.toString(), Toast.LENGTH_LONG).show();
        }
    }


    private List<Product> getSuggestedProducts(String query) {
        List<Product> suggestions = new ArrayList<>();
        String[] keywords = query.toLowerCase().split("\\s+");

        for (Product product : productList) {
            if (!product.getName().toLowerCase().contains(query.toLowerCase())) {
                for (String keyword : keywords) {
                    if (product.getName().toLowerCase().contains(keyword)) {
                        suggestions.add(product);
                        break;
                    }
                }
            }
            if (suggestions.size() >= 3) {
                break;
            }
        }
        return suggestions;
    }
}
