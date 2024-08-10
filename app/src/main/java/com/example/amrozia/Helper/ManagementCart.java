package com.example.amrozia.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.amrozia.Domain.ItemsDomain;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ManagementCart {

    private static final String SHARED_PREF_NAME = "cart";
    private static final String KEY_CART = "cart";
    private static ManagementCart mInstance;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public ManagementCart(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized ManagementCart getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ManagementCart(context);
        }
        return mInstance;
    }

    // Add item to cart
    public void addToCart(ItemsDomain item) {
        List<ItemsDomain> cart = getCart();
        boolean itemExists = false;

        for (ItemsDomain cartItem : cart) {
            if (cartItem.getTitle().equals(item.getTitle())) { // Assuming each item has a unique id
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            item.setQuantity(1); // Set the default quantity to 1
            cart.add(item);
        }

        saveCart(cart);
    }

    // Remove item from cart
    public void removeFromCart(int position) {
        List<ItemsDomain> cart = getCart();
        cart.remove(position);
        saveCart(cart);
    }

    // Get all items in cart
    public List<ItemsDomain> getCart() {
        String cartString = mSharedPreferences.getString(KEY_CART, null);
        if (cartString != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ItemsDomain>>() {}.getType();
            return gson.fromJson(cartString, type);
        }
        return new ArrayList<>();
    }

    // Clear cart
    public void clearCart() {
        mSharedPreferences.edit().remove(KEY_CART).apply();
    }

    // Save cart to SharedPreferences
    public void saveCart(List<ItemsDomain> cart) {
        Gson gson = new Gson();
        String cartString = gson.toJson(cart);
        mSharedPreferences.edit().putString(KEY_CART, cartString).apply();
    }

    // Calculate total fee in cart
    public double getTotalFee() {
        double totalFee = 0.0;
        List<ItemsDomain> cart = getCart();
        for (ItemsDomain item : cart) {
            totalFee += item.getPrice() * item.getQuantity();
        }
        return totalFee;
    }
}
