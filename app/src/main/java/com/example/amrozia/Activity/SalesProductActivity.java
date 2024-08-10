package com.example.amrozia.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amrozia.Adapter.ProductAdapter;
import com.example.amrozia.Domain.ProductDomain;
import com.example.amrozia.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SalesProductActivity extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private ProgressBar progressBar;
    private TextView noProductsFound;
    private ProductAdapter productAdapter;
    private List<ProductDomain> selectedProducts = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ImageView backBtn;
    private static final long TWO_HOURS = 2 * 60 * 60 * 1000; // 2 hours in milliseconds
    private static final long THREE_HOURS = 3 * 60 * 60 * 1000; // 3 hours in milliseconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_product);

        // Initialize views
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        progressBar = findViewById(R.id.progressBar);
        noProductsFound = findViewById(R.id.textViewNoProductsFound);
        backBtn = findViewById(R.id.backBtn);

        // Setup RecyclerView and adapter for products list display in 2 columns
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        // Create a new ProductAdapter with an empty list of products
        productAdapter = new ProductAdapter(this, new ArrayList<>(), "Sales Products");
        // Set the adapter to the RecyclerView
        recyclerViewProducts.setAdapter(productAdapter);

        // Load products
        loadProducts();

        // Set back button click listener
        backBtn.setOnClickListener(v -> finish());
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);

        // List to hold tasks for querying each category's products
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        // Define the categories to query
        List<String> categories = List.of(
                "Cotton Collection",
                "Mashru Silk Collection",
                "Premium Rayon Collection",
                "Rayon Collection",
                "Staple Cotton Collection"
        );

        // Iterate over each category
        for (String category : categories) {
            Query productsRef = firestore.collection("Categories").document(category).collection("products");
            Log.d("SalesProductActivity", "Querying category: " + category);
            tasks.add(productsRef.get());
        }

        // When all product queries are complete, process the results
        Task<List<QuerySnapshot>> allTasks = com.google.android.gms.tasks.Tasks.whenAllSuccess(tasks);
        allTasks.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ProductDomain> allProductsFromCategories = new ArrayList<>();

                // Iterate over each category's products
                SharedPreferences sharedPreferences = getSharedPreferences("ShufflePrefs", MODE_PRIVATE);
                // Get the last shuffle time
                long lastShuffleTime = sharedPreferences.getLong("lastShuffleTime", 0);
                // Get the current time
                long currentTime = System.currentTimeMillis();
                boolean shuffleProducts = currentTime - lastShuffleTime > THREE_HOURS; // Check if 3 hours have passed

                // Iterate over each category's products
                for (int i = 0; i < task.getResult().size(); i++) {
                    // Get the products in this category
                    QuerySnapshot querySnapshot = task.getResult().get(i);
                    // Get the category name
                    String category = categories.get(i);
                    List<ProductDomain> productsInCategory = new ArrayList<>();
                    // Iterate over each product in the category
                    for (DocumentSnapshot productDoc : querySnapshot) {
                        if (productDoc.exists()) {
                            // Convert the document to a ProductDomain object
                            ProductDomain product = productDoc.toObject(ProductDomain.class);
                            // Ensure the product is not null
                            if (product != null && product.getStock() > 0) { // Check if the quantity is greater than 0
                                // Set the product ID
                                productsInCategory.add(product);
                            }
                        }
                    }

                    if (shuffleProducts) {
                        // Shuffle and select 3 products from this category
                        Collections.shuffle(productsInCategory);
                        if (productsInCategory.size() > 3) {
                            selectedProducts.addAll(productsInCategory.subList(0, 3));
                        } else {
                            selectedProducts.addAll(productsInCategory);
                        }
                    } else {
                        // Load the previously stored order if shuffle is not needed
                        int categorySize = sharedPreferences.getInt(category + "_size", 0);
                        // Load the products in the stored order
                        for (int j = 0; j < categorySize; j++) {
                            // Get the product ID
                            String productId = sharedPreferences.getString(category + "_product_" + j, null);
                            for (ProductDomain product : productsInCategory) {
                                // Check if the product ID matches
                                if (product.getId().equals(productId)) {
                                    selectedProducts.add(product);
                                    break;
                                }
                            }
                        }
                    }
                }

                // Shuffle the selected products
                if (shuffleProducts) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    // Store the current time
                    editor.putLong("lastShuffleTime", currentTime);

                    // Store the current shuffled order
                    for (String category : categories) {
                        // Get the products in this category
                        List<ProductDomain> productsInCategory = new ArrayList<>();
                        // Add the products in the selected order
                        for (ProductDomain product : selectedProducts) {
                            // Check if the product is in this category
                            if (category.equals(product.getCategory())) {
                                productsInCategory.add(product);
                            }
                        }
                        // Store the order of products in this category
                        editor.putInt(category + "_size", productsInCategory.size());
                        // Store the product IDs in the order they appear
                        for (int i = 0; i < productsInCategory.size(); i++) {
                            // Store the product ID
                            editor.putString(category + "_product_" + i, productsInCategory.get(i).getId());
                        }
                    }
                    // Apply the changes
                    editor.apply();
                }

                // Check if we have 15 products
                if (selectedProducts.size() > 15) {
                    selectedProducts = selectedProducts.subList(0, 15);
                }

                // Apply discount to each product
                applyDiscount(selectedProducts);

                // Update adapter with selected products
                productAdapter.updateProducts(selectedProducts);
                progressBar.setVisibility(View.GONE);

                // Show a message if no products are found
                if (selectedProducts.isEmpty()) {
                    noProductsFound.setVisibility(View.VISIBLE);
                }
            } else {
                showError();
            }
        });
    }

    private void applyDiscount(List<ProductDomain> products) {
        SharedPreferences sharedPreferences = getSharedPreferences("DiscountPrefs", MODE_PRIVATE);
        long lastUpdated = sharedPreferences.getLong("lastUpdated", 0);
        long currentTime = System.currentTimeMillis();
        boolean generateNewDiscounts = currentTime - lastUpdated > 2 * 60 * 60 * 1000; // Check if 2 hours have passed

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Random random = new Random();

        for (ProductDomain product : products) {
            int discount;
            if (generateNewDiscounts) {
                // Randomly decide the range for discount
                if (random.nextDouble() < 0.8) { // 80% chance for ₹15 to ₹30
                    discount = 15 + random.nextInt(16);
                } else { // 20% chance for ₹31 to ₹50
                    discount = 31 + random.nextInt(20);
                }
                editor.putInt(product.getId(), discount); // Store the discount in SharedPreferences
            } else {
                discount = sharedPreferences.getInt(product.getId(), 0); // Retrieve the stored discount
            }

            double originalPrice = product.getPrice(); // Assuming getPrice() returns a double
            double discountedPrice = Math.max(originalPrice - discount, 0); // Ensure price does not go negative
            product.setPrice(discountedPrice);
        }

        if (generateNewDiscounts) {
            editor.putLong("lastUpdated", currentTime); // Update the timestamp
            editor.apply();
        }
    }

    private void showNoProductsFound() {
        progressBar.setVisibility(View.GONE);
        noProductsFound.setVisibility(View.VISIBLE);
    }

    private void showError() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(SalesProductActivity.this, "Error loading products. Please try again later.", Toast.LENGTH_SHORT).show();
    }
}
