package com.example.thriftygroceru;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private TextView totalPriceTextView;
    private Button checkoutButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        databaseHelper = new DatabaseHelper(this);
        cartItems = databaseHelper.getCartItems();
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cartItems, new CartAdapter.CartItemListener() {
            @Override
            public void onUpdateCartItem(CartItem item) {
                updateCartItem(item);
            }

            @Override
            public void onRemoveCartItem(CartItem item) {
                removeCartItem(item);
            }
        });
        cartRecyclerView.setAdapter(cartAdapter);

        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        checkoutButton = findViewById(R.id.checkoutButton);

        updateTotalPrice();

        checkoutButton.setOnClickListener(v -> {
            databaseHelper.clearCart();
            startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
            finish();
        });
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getProduct().getPrice() * item.getQuantity();
        }
        totalPriceTextView.setText(String.format("Total: $%.2f", totalPrice));
    }

    private void updateCartItem(CartItem item) {
        databaseHelper.updateCartItemQuantity(item.getProduct().getId(), item.getQuantity());
        updateTotalPrice();
    }

    private void removeCartItem(CartItem item) {
        databaseHelper.removeFromCart(item.getProduct().getId());
        cartItems.remove(item);
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
    }
}
