package com.globalfashion.amrozia.Activity;

import static com.globalfashion.amrozia.BuildConfig.GOOGLE_CLIENT_ID;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.globalfashion.amrozia.Adapter.Order;
import com.globalfashion.amrozia.Adapter.OrderDetailAdapter;
import com.globalfashion.amrozia.AdminActivity;
import com.globalfashion.amrozia.LoginActivity;
import com.globalfashion.amrozia.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
// The ManageOrdersActivity class is responsible for managing the orders
public class ManageOrdersActivity extends AppCompatActivity {

    // Declare the RecyclerView and the OrderDetailAdapter
    private RecyclerView recyclerViewOrders;
    private OrderDetailAdapter orderDetailAdapter;
    private List<Order> ordersList = new ArrayList<>();
    // Declare the FirebaseFirestore instance
    private FirebaseFirestore db;

    // Override the onBackPressed method to finish the activity
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finish();
        } else {
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_orders);

        // Retrieve the RecyclerView and set the layout manager and adapter
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        orderDetailAdapter = new OrderDetailAdapter(this, ordersList);
        recyclerViewOrders.setAdapter(orderDetailAdapter);

        // Initialize the FirebaseFirestore instance
        db = FirebaseFirestore.getInstance();

        // Fetch the orders from the Firestore database
        fetchOrders();

        // Set click listeners for the icons
        LinearLayout adminIcon = findViewById(R.id.product_management_icon);
        adminIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageOrdersActivity.this, AdminActivity.class);
                startActivity(intent);
                finish();
            }
        });

        ImageView logout_icon = findViewById(R.id.logout_icon);
        logout_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out the user
                FirebaseAuth.getInstance().signOut();

                // Sign out the user from GoogleSignInClient
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(GOOGLE_CLIENT_ID)
                        .requestEmail()
                        .build();

                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(ManageOrdersActivity.this, gso);

                googleSignInClient.signOut()
                        .addOnCompleteListener(ManageOrdersActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out from Google Sign-In
                            }
                        });
                // Navigate back to LoginActivity
                Intent intent = new Intent(ManageOrdersActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the activity stack
                startActivity(intent);
            }
        });
    }

    // Fetch the orders from the Firestore database
    private void fetchOrders() {
        // Fetch all the orders from the UserOrders collection
        db.collectionGroup("UserOrders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Clear the ordersList
                            ordersList.clear();
                            // Iterate over the documents and parse them into Order objects
                            for (DocumentSnapshot document : task.getResult()) {
                                // Parse the document into an Order object
                                Order order = document.toObject(Order.class);
                                // If the order is not null
                                if (order != null) {
                                    // Set the orderId and totalCost
                                    order.setOrderId(document.getId());
                                    if (document.contains("totalCost")) {
                                        // Set the totalCost
                                        order.setTotalCost(document.getDouble("totalCost"));
                                    }
                                    // Add the order to the ordersList
                                    ordersList.add(order);
                                } else {
                                    Log.e("ManageOrdersActivity", "Failed to parse order document: " + document.getId());
                                }
                            }

                            // Sort the ordersList in descending order of timestamp
                            Collections.sort(ordersList, new Comparator<Order>() {
                                @Override
                                public int compare(Order o1, Order o2) {
                                    return o2.getTimestamp().compareTo(o1.getTimestamp());
                                }
                            });

                            // Notify the adapter that the data has changed
                            orderDetailAdapter.notifyDataSetChanged();
                            // Log the number of orders fetched
                            if (ordersList.isEmpty()) {
                                Log.d("ManageOrdersActivity", "No orders found.");
                            } else {
                                Log.d("ManageOrdersActivity", "Fetched " + ordersList.size() + " orders.");
                            }
                        } else {
                            Log.e("ManageOrdersActivity", "Error getting orders.", task.getException());
                        }
                    }
                });
    }
}