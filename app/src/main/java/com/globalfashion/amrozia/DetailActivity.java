package com.globalfashion.amrozia;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager2.widget.ViewPager2;

import com.globalfashion.amrozia.Adapter.ViewPagerAdapter;
import com.globalfashion.amrozia.Domain.ItemsDomain;
import com.globalfashion.amrozia.Domain.ProductDomain;
import com.globalfashion.amrozia.Fragment.DescriptionFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// This activity displays the details of a product and allows the user to add it to the cart
public class DetailActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private List<String> picUrl; // This should be your list of image URLs
    private List<String> sizeTxt;
    private String description, userId, userEmail;
    private FirebaseFirestore firestore;
    private ImageView favBtn, shareBtn;
    private ImageView favDarkBtn, shareDarkBtn;
    private TextView titleTxt, priceTxt;

    private boolean isFavChecked = false; // To keep track of the button state
    private static final String PREFS_NAME = "FavPrefs"; // Shared preferences file name
    private static final String FAV_STATE_KEY = "favState"; // Key for the favorite state

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Get the Product ID from the Intent
        String productId = getIntent().getStringExtra("productId");
        String category = getIntent().getStringExtra("category");

        // Validate the productId and category
        if (productId == null || category == null) {
            Toast.makeText(this, "Product not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        titleTxt = findViewById(R.id.titleTxt);
        priceTxt = findViewById(R.id.priceTxt);

        // Get references to the views in activity_detail
        ViewPager2 viewpageSlider = findViewById(R.id.viewpageSlider);
        TextView titleTxt = findViewById(R.id.titleTxt);
        TextView priceTxt = findViewById(R.id.priceTxt);

        favBtn = findViewById(R.id.favBtn);
        favDarkBtn = findViewById(R.id.favDarkBtn);
        shareBtn = findViewById(R.id.shareBtn);
        shareDarkBtn = findViewById(R.id.shareDarkBtn);

        // Load saved state
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String favStateKey = FAV_STATE_KEY + "_" + productId; // This will create a unique key for each product

        // Load the favorite state from shared preferences
        isFavChecked = sharedPreferences.getBoolean(favStateKey, false);

        // Update the favorite button based on the saved state
        updateFavoriteButton();
        // Set the click listeners for the save button
        favBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite(favStateKey);
            }
        });

        // Set the click listener for the saved button
        favDarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite(favStateKey);
            }
        });


    // Fetch the Product from Firestore based on its ID
    firestore.collection("Categories").document(category).collection("products").document(productId)
        .get()
        .addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Product found, populate the views with its data
                ProductDomain product = documentSnapshot.toObject(ProductDomain.class);
                titleTxt.setText(product.getTitle());
                // Get the price from the intent
                Intent intent = getIntent();

                // Check if the intent is from SalesProductActivity
                boolean fromSalesProductActivity = intent.getBooleanExtra("fromSalesProductActivity", false);
                double price;
                // If the intent is from SalesProductActivity, get the price from the intent
                if (fromSalesProductActivity) {
                    price = intent.getDoubleExtra("price", 0);
                } else {
                    // If the intent is not from SalesProductActivity, get the price from the product
                    ItemsDomain item = (ItemsDomain) intent.getSerializableExtra("item");
                    // Check if the item is not null
                    if (item != null) {
                        price = item.getPrice();
                    } else {
                        // Handle case where item is null
                        price = product.getPrice();
                    }
                }
                // Display the price
                TextView priceTextView = findViewById(R.id.priceTxt);
                // Format the price as a currency string
                priceTextView.setText(String.format("₹%.2f", price));
                description = product.getDescription(); // Fetch the description here

                // Set up the ViewPager for the product images
                if (product.getPicUrl() != null) {
                    picUrl = product.getPicUrl(); // Populate picUrl with the product's image URLs
                    List<String> picUrls = new ArrayList<>();
                    picUrls.addAll(product.getPicUrl());
                    // Create a new instance of ViewPagerAdapter and set it as the adapter for the ViewPager
                    ViewPagerAdapter adapter = new ViewPagerAdapter(picUrls);
                    // Set the adapter for the ViewPager
                    viewpageSlider.setAdapter(adapter);
                    viewpageSlider.getAdapter().notifyDataSetChanged();
                } else {
                    // If the product has no images, create an empty adapter
                    ViewPagerAdapter adapter = new ViewPagerAdapter(new ArrayList<>());
                    // Set the adapter for the ViewPager
                    viewpageSlider.setAdapter(adapter);
                    // Notify the adapter that the data set has changed
                    viewpageSlider.getAdapter().notifyDataSetChanged();
                }

                // Create a new instance of DescriptionFragment
                DescriptionFragment descriptionFragment = new DescriptionFragment();

                // Create a new Bundle
                Bundle bundle = new Bundle();

                // Put the product's description into the Bundle
                bundle.putString("description", product.getDescription());

                // Set the Bundle as the arguments for the DescriptionFragment
                descriptionFragment.setArguments(bundle);

                // Use the FragmentManager to begin a transaction, replace the fragment_container with the fragment, and commit the transaction
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, descriptionFragment)
                        .commit();
            } else {
                // Product not found, handle
                // error
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(e -> {
            // Error fetching data, handle the error
            Toast.makeText(this, "Failed to fetch product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

        // Handle back button click
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Handle share button click
        ImageView shareBtn = findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareProductDetails();
            }
        });

        // Handle share dark button click
        ImageView shareDarkBtn = findViewById(R.id.shareDarkBtn);
        shareDarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareProductDetails();
            }
        });

        // Initialize picUrl as an empty list
        picUrl = new ArrayList<>();
        // Handle add to cart button click
        AppCompatButton addToCartBtn = findViewById(R.id.addTocartBtn);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, CartActivity.class);

                // Create an ItemsDomain object with the product details
                ArrayList<String> picUrlList = new ArrayList<>(picUrl);

                // Remove the "₹" symbol before parsing the price to a double
                String priceStr = priceTxt.getText().toString().replace("₹", "").trim();
                double price = Double.parseDouble(priceStr);

                // Create an ItemsDomain object with the product details
                ItemsDomain item = new ItemsDomain(productId, titleTxt.getText().toString(), description, picUrlList, price, category);

                // Put the ItemsDomain object into the intent
                intent.putExtra("item", item);

                // Pass the category and productId as extras in the intent
                intent.putExtra("category", category);
                intent.putExtra("productId", productId);

                startActivity(intent);
            }
        });
    }

    // Method to share product details
    private void shareProductDetails() {
        // Get product details
        String productName = titleTxt.getText().toString();
        String productPrice = priceTxt.getText().toString();

        // Create share message with product details
        String shareMessage = " Check out this product on Amrozia:\n\n" +
                              "Product Name: " + productName + "\n" +
                              "Price: " + productPrice + "\n\n";

        // Create and launch the share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Product");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out the Amrozia app on the Play Store!"+shareMessage+"https://play.google.com/store/apps/details?id=com.globalfashion.amrozia");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    // Method to toggle the favorite state
    private void toggleFavorite(String favStateKey) {
        isFavChecked = !isFavChecked;

        // Save the state
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(favStateKey, isFavChecked);
        editor.apply();

        // Show a toast message based on the new state
        if (isFavChecked) {
            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
        }

        // Update the favorite button based on the new state
        updateFavoriteButton();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userId = currentUser.getUid(); // Get user id
            userEmail = currentUser.getEmail(); // Get user email
        } else {
            // Handle case where user is not logged in
            return;
        }

        // Get the product's id and name
        String productId = favStateKey.replace(FAV_STATE_KEY + "_", "");
        String productName = titleTxt.getText().toString();

        if (isFavChecked) {
            // If the product is saved, add it to Firestore
            Map<String, Object> savedProduct = new HashMap<>();
            savedProduct.put("userId", userId);
            savedProduct.put("email", userEmail);
            savedProduct.put("productId", productId);
            savedProduct.put("productName", productName);

            firestore.collection("saved").document(userId + "_" + productId).set(savedProduct)
                    .addOnSuccessListener(aVoid -> Log.d("DetailActivity", "Product added to favorites"))
                    .addOnFailureListener(e -> Log.d("DetailActivity", "Failed to add product: " + e.getMessage()));
        } else {
            // If the product is unsaved, remove it from Firestore
            firestore.collection("saved").document(userId + "_" + productId).delete()
                    .addOnSuccessListener(aVoid -> Log.d("DetailActivity", "Product removed from favorites"))
                    .addOnFailureListener(e -> Log.d("DetailActivity", "Failed to remove product: " + e.getMessage()));
        }
    }

    // Method to update the favorite button based on the state
    private void updateFavoriteButton() {
        if (isFavChecked) {
            favBtn.setVisibility(View.GONE);
            favDarkBtn.setVisibility(View.VISIBLE);
            shareBtn.setVisibility(View.GONE);
            shareDarkBtn.setVisibility(View.VISIBLE);
        } else {
            favBtn.setVisibility(View.VISIBLE);
            favDarkBtn.setVisibility(View.GONE);
            shareBtn.setVisibility(View.VISIBLE);
            shareDarkBtn.setVisibility(View.GONE);
        }
    }
}
