package com.example.thriftygroceru;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems = new ArrayList<>();
    private final CartItemListener cartItemListener;

    public interface CartItemListener {
        void onUpdateCartItem(CartItem cartItem); // Called on quantity change
        void onRemoveCartItem(CartItem cartItem); // Called when item is removed
    }

    public CartAdapter(List<CartItem> cartItems, CartItemListener cartItemListener) {
        if (cartItems != null) {
            this.cartItems = cartItems;
        }
        this.cartItemListener = cartItemListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        if (product != null) {
            // Set product details
            holder.productName.setText(product.getName());
            holder.productPrice.setText(String.format("$%.2f", product.getPrice()));
            holder.quantity.setText(String.valueOf(cartItem.getQuantity()));

            // Load product image using Glide
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl()) // URL of the product image
                    .placeholder(R.drawable.app_logo) // Placeholder during loading
                    .error(R.drawable.app_logo) // Fallback image if error occurs
                    .into(holder.productImage);
        } else {
            // Set default values in case product is null
            holder.productName.setText("Unknown Product");
            holder.productPrice.setText("$0.00");
            holder.quantity.setText("0");
            holder.productImage.setImageResource(R.drawable.app_logo); // Default image
        }

        // Increment button logic
        holder.incrementButton.setOnClickListener(v -> {
            cartItem.incrementQuantity();
            holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
            cartItemListener.onUpdateCartItem(cartItem); // Notify listener
        });

        // Decrement button logic
        holder.decrementButton.setOnClickListener(v -> {
            if (cartItem.getQuantity() > 1) {
                cartItem.decrementQuantity();
                holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
                cartItemListener.onUpdateCartItem(cartItem); // Notify listener
            } else {
                cartItems.remove(position); // Remove item from list
                notifyItemRemoved(position); // Notify RecyclerView
                notifyItemRangeChanged(position, cartItems.size()); // Update remaining items
                cartItemListener.onRemoveCartItem(cartItem); // Notify listener
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems = newCartItems != null ? newCartItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView quantity;
        ImageButton incrementButton;
        ImageButton decrementButton;

        CartViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantity = itemView.findViewById(R.id.quantity);
            incrementButton = itemView.findViewById(R.id.incrementButton);
            decrementButton = itemView.findViewById(R.id.decrementButton);
        }
    }
}
