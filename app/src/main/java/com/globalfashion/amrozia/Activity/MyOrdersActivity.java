package com.globalfashion.amrozia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.globalfashion.amrozia.Adapter.OrderAdapter;
import com.globalfashion.amrozia.DetailActivity;
import com.globalfashion.amrozia.Domain.OrderDomain;
import com.globalfashion.amrozia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
// The MyOrdersActivity class is responsible for displaying the user's orders
public class MyOrdersActivity extends AppCompatActivity {

    // Declare the RecyclerView, ProgressBar, TextView, ImageView, and OrderAdapter
    private RecyclerView recyclerViewOrders;
    private ProgressBar progressBar;
    private TextView noOrdersFound, trackingLink;
    private OrderAdapter orderAdapter;
    private List<OrderDomain> allOrders = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ImageView backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        // Initialize views
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        progressBar = findViewById(R.id.progressBar);
        noOrdersFound = findViewById(R.id.textViewNoOrdersFound);
        backBtn = findViewById(R.id.backBtn);

        // Initialize RecyclerView
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, new ArrayList<>());
        recyclerViewOrders.setAdapter(orderAdapter);

        // Call fetchOrders method
        fetchOrders();

        // Set back button listener
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Method to fetch orders from Firestore
    private void fetchOrders() {
        // Show the progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Get the user ID
            String userId = currentUser.getUid();

            CollectionReference ordersRef = firestore
                    // Reference to the Orders collection
                    .collection("Orders")
                    // Reference to the user's document
                    .document(userId)
                    // Reference to the UserOrders sub-collection
                    .collection("UserOrders");

            // Query to fetch orders
            ordersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        // Clear the allOrders list
                        allOrders.clear();
                        // Iterate over the documents and parse them into OrderDomain objects
                        for (DocumentSnapshot document : task.getResult()) {
                            // Get product names, prices, and images
                            ArrayList<String> productNames = (ArrayList<String>) document.get("productNames");
                            ArrayList<String> productPrices = (ArrayList<String>) document.get("productPrices");
                            ArrayList<String> productImages = (ArrayList<String>) document.get("productImages");
                            ArrayList<String> categories = (ArrayList<String>) document.get("category"); // category is also an array
                            ArrayList<String> productIds = (ArrayList<String>) document.get("productId");
                            String trackingLink = document.getString("trackingLink");

                            // Get timestamp
                            Date timestamp = document.getDate("timestamp");

                            // Iterate over the products and create an OrderDomain object for each one
                            for (int i = 0; i < productNames.size(); i++) {
                                OrderDomain order = new OrderDomain();
                                order.setProductName(productNames.get(i));
                                order.setProductPrice(productPrices.get(i));
                                order.setProductImage(productImages.get(i));
                                order.setTimestamp(timestamp); // Set timestamp
                                order.setCategory(categories.get(i));  // Set the category for each product
                                order.setProductId(productIds.get(i)); // Set the product ID
                                order.setTrackingLink(trackingLink); // Set the tracking link

                                // Add the order to the allOrders list
                                allOrders.add(order);
                            }
                        }

                        // Sort the allOrders list in descending order of timestamp
                        Collections.sort(allOrders, new Comparator<OrderDomain>() {
                            @Override
                            public int compare(OrderDomain o1, OrderDomain o2) {
                                return o2.getTimestamp().compareTo(o1.getTimestamp());
                            }
                        });

                        // If the allOrders list is empty, show the noOrdersFound TextView
                        if (allOrders.isEmpty()) {
                            noOrdersFound.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            Log.d("FetchOrders", "No orders found.");
                        } else {
                            noOrdersFound.setVisibility(View.GONE);
                            orderAdapter.updateOrders(allOrders);
                            Log.d("FetchOrders", "Orders updated in adapter.");
                        }

                        // Set the OnItemClickListener here, after updating the orders
                        orderAdapter.setOnItemClickListener(new OrderAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(OrderDomain order) {
                                // Check if the product exists in the database before starting the DetailActivity
                                firestore.collection("Categories").document(order.getCategory()).collection("products").document(order.getProductId())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    // If the task is successful, get the document
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        // If the product exists, start the DetailActivity
                                                        Intent intent = new Intent(MyOrdersActivity.this, DetailActivity.class);
                                                        // Pass the product ID and category to the DetailActivity
                                                        intent.putExtra("productId", order.getProductId());
                                                        intent.putExtra("category", order.getCategory());
                                                        startActivity(intent);
                                                    } else {
                                                        // If the product doesn't exist, show a toast message to the user
                                                        Toast.makeText(MyOrdersActivity.this, "This product is out of stock", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    // If the task is not successful, show a toast message to the user
                                                    Toast.makeText(MyOrdersActivity.this, "Error while checking product: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MyOrdersActivity.this, "User not logged in.", Toast.LENGTH_SHORT).show();
                        Log.e("FetchOrders", "User not logged in.");
                    }
                }
            });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(MyOrdersActivity.this, "User not logged in.", Toast.LENGTH_SHORT).show();
            Log.e("FetchOrders", "User not logged in.");
        }
    }
}