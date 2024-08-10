package com.example.amrozia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amrozia.Adapter.PurchaseProductAdapter;
import com.example.amrozia.Domain.ProductDomain;
import com.example.amrozia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PurchaseActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextAddress, editTextPhone;
    private Button buttonSubmit;
    private List<ProductDomain> productList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhone = findViewById(R.id.editTextPayment);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Get the passed product names, prices and images
        Intent intent = getIntent();
        ArrayList<String> productNames = intent.getStringArrayListExtra("productNames");
        ArrayList<String> productPrices = intent.getStringArrayListExtra("productPrices");
        ArrayList<String> productImages = intent.getStringArrayListExtra("productImages");
        ArrayList<String> productIds = intent.getStringArrayListExtra("productIds");
        ArrayList<String> productCategories = intent.getStringArrayListExtra("productCategories");
        ArrayList<Integer> productQuantities = intent.getIntegerArrayListExtra("productQuantities");
        Log.d("PurchaseActivity", "Product Quantities: " + productQuantities);

        // Create a list of products
        productList = new ArrayList<>();
        for (int i = 0; i < productNames.size(); i++) {
            String name = productNames.get(i);
            String price = productPrices.get(i);
            ArrayList<String> image = new ArrayList<>();
            image.add(productImages.get(i));
            String id = productIds.get(i);
            String category = productCategories.get(i);
            int quantity = productQuantities.get(i);

            productList.add(new ProductDomain(id, name, "", Double.parseDouble(price), image, category, 0, quantity));
        }

        RecyclerView recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        PurchaseProductAdapter productAdapter = new PurchaseProductAdapter(this, productList);
        recyclerViewProducts.setAdapter(productAdapter);

        // Set click listener for the submit button
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePurchaseInfo(productNames, productPrices, productImages, productList);
            }

        });
    }

    // Save the purchase information to Firestore
    private void savePurchaseInfo(ArrayList<String> productNames, ArrayList<String> productPrices, ArrayList<String> productImages, List<ProductDomain> productList) {
        // Get user input
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        // Get the passed product IDs
        Intent intent = getIntent();
        ArrayList<String> productIds = intent.getStringArrayListExtra("productIds");
        ArrayList<String> productCategories = intent.getStringArrayListExtra("productCategories");
        ArrayList<Integer> productQuantities = intent.getIntegerArrayListExtra("productQuantities");

        // Validate the input
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter your address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user's ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();

        // Create a map to store the data
        Map<String, Object> purchase = new HashMap<>();
        purchase.put("userId", userId);
        purchase.put("name", name);
        purchase.put("email", email);
        purchase.put("address", address);
        purchase.put("phone", phone);
        purchase.put("productNames", productNames);
        purchase.put("productPrices", productPrices);
        purchase.put("productImages", productImages);
        purchase.put("timestamp", FieldValue.serverTimestamp());
        purchase.put("productId", productIds);
        purchase.put("category", productCategories);
        purchase.put("productQuantities", productQuantities);

        // Save the data to Firestore under the Orders collection with userId
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Orders").document(userId).collection("UserOrders")
                .add(purchase) // Use add to create a new document with a unique ID
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("PurchaseActivity", "DocumentSnapshot successfully written!");
                        Toast.makeText(PurchaseActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                        updateProductStock(productList, productQuantities);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PurchaseActivity.this, "Error while proceeding to payment! Please try again.", Toast.LENGTH_SHORT).show();
                        Log.e("PurchaseActivity", "Error adding document", e);
                    }
                });
    }

    // Update the stock of the purchased products
    private void updateProductStock(List<ProductDomain> productList, ArrayList<Integer> productQuantities) {
        Log.d("PurchaseActivity", "Updating product stock in Firestore");
        for (int i = 0; i < productList.size(); i++) {
            ProductDomain product = productList.get(i);
            int productQuantity = productQuantities.get(i);

            // Fetch category and ID
            String category = product.getCategory();
            String productId = product.getId();

            // Log category and ID for debugging
            Log.d("PurchaseActivity", "Product Category: " + category);
            Log.d("PurchaseActivity", "Product ID: " + productId);


            // Check if category and ID are valid
            if (category != null && !category.isEmpty() && productId != null && !productId.isEmpty()) {
                // Create a reference to the specific product document
                DocumentReference productRef = db.collection("Categories")
                        .document(category) // Category (e.g., "Cotton Collection")
                        .collection("products")          // Subcollection "products"
                        .document(productId);


                productRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Convert the stock value to int
                            int stock = documentSnapshot.getLong("stock").intValue();
                            int newStock = stock - productQuantity; // Adjust as necessary based on your logic

                            if (newStock <= 0) {
                                // Remove product from Firestore if the stock is 0 or less
                                productRef.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("PurchaseActivity", "Product deleted from Firestore");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("PurchaseActivity", "Error deleting product", e);
                                            }
                                        });
                            } else {
                                // Update product stock in Firestore
                                productRef.update("stock", newStock)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("PurchaseActivity", "Product stock updated in Firestore");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("PurchaseActivity", "Error updating product stock", e);
                                            }
                                        });
                            }
                        } else {
                            Log.e("PurchaseActivity", "Product document does not exist!");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("PurchaseActivity", "Error getting product document", e);
                    }
                });
            } else {
                Log.e("PurchaseActivity", "Category or Product ID is null or empty");
            }
        }
    }
}