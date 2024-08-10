package com.example.amrozia.Activity;

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

import com.example.amrozia.Adapter.OrderAdapter;
import com.example.amrozia.DetailActivity;
import com.example.amrozia.Domain.OrderDomain;
import com.example.amrozia.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {
    private RecyclerView recyclerViewOrders;
    private ProgressBar progressBar;
    private TextView noOrdersFound;
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

        // Get the category and productId from the intent
        Intent intent = getIntent();

        // Fetch orders
        fetchOrders();

        // Set back button listener
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fetchOrders() {
        progressBar.setVisibility(View.VISIBLE);

        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Reference to the user's orders sub-collection
            CollectionReference ordersRef = FirebaseFirestore.getInstance()
                    .collection("Orders")
                    .document(userId)
                    .collection("UserOrders");

            // Query to fetch orders
            ordersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        allOrders.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            // Get product names, prices, and images
                            ArrayList<String> productNames = (ArrayList<String>) document.get("productNames");
                            ArrayList<String> productPrices = (ArrayList<String>) document.get("productPrices");
                            ArrayList<String> productImages = (ArrayList<String>) document.get("productImages");

                            // Get the 'category' field as an Object
                            Object categoryObject = document.get("category");

                            // Check if 'category' is a String before casting
                            if (categoryObject instanceof String) {
                                String category = (String) categoryObject;
                                String productId = document.getString("productId");

                                // Log received values
                                Log.d("FetchOrders", "ProductId: " + productId);
                                Log.d("FetchOrders", "Category: " + category);

                                // Get timestamp
                                Date timestamp = document.getDate("timestamp");

                                // Iterate over the products and create an OrderDomain object for each one
                                for (int i = 0; i < productNames.size(); i++) {
                                    OrderDomain order = new OrderDomain();
                                    order.setProductName(productNames.get(i));
                                    order.setProductPrice(productPrices.get(i));
                                    order.setProductImage(productImages.get(i));
                                    order.setTimestamp(timestamp); // Set timestamp
                                    order.setCategory(category);
                                    order.setProductId(productId);
                                    allOrders.add(order);
                                }
                            } else {
                                // Handle the case where 'category' is not a String
                                Log.e("FetchOrders", "'category' is not a String: " + categoryObject);
                            }
                        }

                        if (allOrders.isEmpty()) {
                            noOrdersFound.setVisibility(View.VISIBLE);
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
                                Intent intent = new Intent(MyOrdersActivity.this, DetailActivity.class);
                                intent.putExtra("productId", order.getProductId());
                                intent.putExtra("category", order.getCategory());
                                startActivity(intent);
                            }
                        });

                        progressBar.setVisibility(View.GONE);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MyOrdersActivity.this, "Failed to fetch orders. Please try again.", Toast.LENGTH_SHORT).show();
                        Log.e("FetchOrders", "Failed to fetch orders: ", task.getException());
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
