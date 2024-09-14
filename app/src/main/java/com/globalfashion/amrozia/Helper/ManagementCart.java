package com.globalfashion.amrozia.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import com.globalfashion.amrozia.Domain.ItemsDomain;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
// This class is used to manage the cart and its items
public class ManagementCart {
    // Constants for the shared preferences
    private static final String SHARED_PREF_NAME = "cart";
    private static final String KEY_CART = "cart";
    private static ManagementCart mInstance;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    // Constructor to initialize the context and shared preferences
    public ManagementCart(Context context) {
        mContext = context;
        // Get the shared preferences for the cart items and their details
        mSharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    // Get the instance of the ManagementCart class to manage the cart
    public static synchronized ManagementCart getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ManagementCart(context);
        }
        return mInstance;
    }


    // Add item to cart or increase the quantity if the item already exists
    public void addToCart(ItemsDomain item) {
        List<ItemsDomain> cart = getCart();
        boolean itemExists = false;

        // Check if the item already exists in the cart
        for (ItemsDomain cartItem : cart) {
            // If the item exists, increase the quantity by 1
            if (cartItem.getTitle().equals(item.getTitle())) { // Assuming each item has a unique id
                // Increase the quantity by 1 for the existing item
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                // Set the flag to true to indicate that the item exists
                itemExists = true;
                break;
            }
        }

        // If the item does not exist, add it to the cart
        if (!itemExists) {
            item.setQuantity(1); // Set the default quantity to 1
            cart.add(item);
        }
        // Save the updated cart
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
        // If the cart is not empty, convert the JSON string to a list of items
        if (cartString != null) {
            // Use Gson to convert the JSON string to a list of items
            Gson gson = new Gson();
            // Define the type of the list using TypeToken
            Type type = new TypeToken<List<ItemsDomain>>() {}.getType();
            // Return the list of items from the JSON string
            return gson.fromJson(cartString, type);
        }
        return new ArrayList<>();
    }

    // Remove a specific item from the cart
    public void removeItemFromCart(String itemId) {
        List<ItemsDomain> cart = getCart();
        // Iterate through the cart to find the item with the specified id
        for (ItemsDomain item : cart) {
            // If the item id matches, remove the item from the cart
            if (item.getId().equals(itemId)) {
                cart.remove(item);
                break;
            }
        }
        saveCart(cart);
    }

    // Clear cart
    public void clearCart() {
        mSharedPreferences.edit().remove(KEY_CART).apply();
    }

    // Save cart to SharedPreferences
    public void saveCart(List<ItemsDomain> cart) {
        // Convert the list of items to a JSON string using Gson
        Gson gson = new Gson();
        String cartString = gson.toJson(cart);
        // Save the JSON string to SharedPreferences
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
