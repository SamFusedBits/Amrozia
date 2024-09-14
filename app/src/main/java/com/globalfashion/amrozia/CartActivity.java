package com.globalfashion.amrozia;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.globalfashion.amrozia.Activity.MoreActivity;
import com.globalfashion.amrozia.Activity.PurchaseActivity;
import com.globalfashion.amrozia.Adapter.CartAdapter;
import com.globalfashion.amrozia.Domain.ItemsDomain;
import com.globalfashion.amrozia.Helper.ChangeNumberItemsListener;
import com.globalfashion.amrozia.Helper.ManagementCart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
// This activity displays the items in the cart and allows the user to proceed to checkout
public class CartActivity extends AppCompatActivity implements ChangeNumberItemsListener {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ManagementCart managementCart;
    private TextView emptyTxt, totalFeeTxt, deliveryTxt, taxTxt, totalTxt,numberItemTxt;
    private String productId, category, title, description, price;
    private ArrayList<String> picUrl;
    public double totalCost = 0.0;

    // Calculate the total cost of the cart
    @Override
    public void changed() {
        calculateCart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize the management cart
        managementCart = new ManagementCart(this);

        // Receive data from DetailActivity
        Intent intent = getIntent();
        // Get the item object from the intent
        ItemsDomain item = (ItemsDomain) intent.getSerializableExtra("item");

        // Extract item details from the received object
        if (item != null) {
            productId = item.getId();
            category = item.getCategory();
            title = item.getTitle();
            description = item.getDescription();
            picUrl = item.getPicUrl();
            price = String.valueOf(item.getPrice());

            // Add the new item to the cart and save the updated cart
            managementCart.addToCart(new ItemsDomain(productId, title, description, picUrl, Double.parseDouble(price), category));

        } else {
            // Handle case where item is null
            setContentView(R.layout.activity_cart);
        }

        // Get the category and productId from the intent
        String category = intent.getStringExtra("productCategories");
        String productId = intent.getStringExtra("productIds");

        // Initialize the view
        initView();
        // Initialize the list
        initList();
        // Calculate the total cost of the cart
        calculateCart();

        // Handle back button click
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        EditText couponCodeEditTxt = findViewById(R.id.couponCodeEditTxt);

        Button applyBtn = findViewById(R.id.applyBtn);
        TextView discountTxt = findViewById(R.id.discountTxt); // Reference to the discount TextView
        TextView textView15 = findViewById(R.id.textView15); // Reference to the discount label

        applyBtn.setOnClickListener(new View.OnClickListener() {
            private boolean isCouponApplied = false; // To ensure the coupon is applied only once

            @Override
            public void onClick(View v) {
                // Get the coupon code entered by the user (EditText for coupon input)
                String enteredCouponCode = couponCodeEditTxt.getText().toString().trim();

                if (TextUtils.isEmpty(enteredCouponCode)) {
                    Toast.makeText(CartActivity.this, "Please enter a coupon code", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the coupon has already been applied
                if (isCouponApplied) {
                    Toast.makeText(CartActivity.this, "Coupon already applied", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Extract the numeric part from the coupon code (e.g., '25' from 'AM25')
                String discountString = enteredCouponCode.replaceAll("\\D+", ""); // Removes all non-digit characters

                if (TextUtils.isEmpty(discountString)) {
                    Toast.makeText(CartActivity.this, "Invalid coupon code", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Parse the numeric part as the discount percentage
                int discountPercentage = Integer.parseInt(discountString);

                // Firestore reference
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Query the 'coupons' collection to find a matching coupon code
                db.collection("coupons").document(enteredCouponCode)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        // Coupon exists, apply the discount
                                        // Calculate the discount amount (percentage of the totalCost)
                                        double discountAmount = totalCost * (discountPercentage / 100.0);

                                        // Subtract the discount amount from the total cost
                                        totalCost -= discountAmount;

                                        textView15.setVisibility(View.VISIBLE);
                                        discountTxt.setVisibility(View.VISIBLE);
                                        // Update the discount TextView
                                        discountTxt.setText(String.format("₹%.2f", discountAmount));

                                        // Display updated total amount in the UI
                                        totalTxt.setText(String.format("₹%.2f", totalCost));

                                        // Mark the coupon as applied
                                        isCouponApplied = true;

                                        // Show success message
                                        Toast.makeText(CartActivity.this, "Coupon applied! " + discountPercentage + "% off", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Coupon code does not exist
                                        Toast.makeText(CartActivity.this, "Invalid coupon code", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Handle any errors that occurred during the query
                                    Toast.makeText(CartActivity.this, "Error applying coupon", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // Set the onClickListeners for the cart buttons to go back to the home page
        LinearLayout profileButton = findViewById(R.id.profile_btn);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the cart activity
                Intent intent = new Intent(CartActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Set the onClickListeners for the cart buttons to go back to the home page
        LinearLayout homeButton = findViewById(R.id.home_btn);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the cart activity
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Set the onClickListeners for the cart buttons to go back to the home page
        LinearLayout moreButton = findViewById(R.id.more_btn);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the cart activity
                Intent intent = new Intent(CartActivity.this, MoreActivity.class);
                startActivity(intent);
            }
        });

        Button checkOutBtn = findViewById(R.id.checkOutBtn);
        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the existing cart items
                List<ItemsDomain> cartList = managementCart.getCart();

                // Check if the cart is empty
                if (cartList.isEmpty()) {
                    // Show a toast message to the user
                    Toast.makeText(CartActivity.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                } else {
                    // Open the purchase activity and pass the cart items
                    Intent intent = new Intent(CartActivity.this, PurchaseActivity.class);

                    // Create lists to store product names and prices
                    ArrayList<String> productNames = new ArrayList<>();
                    ArrayList<String> productPrices = new ArrayList<>();
                    ArrayList<String> productImages = new ArrayList<>();
                    ArrayList<String> productIds = new ArrayList<>();
                    ArrayList<String> productCategories = new ArrayList<>();
                    ArrayList<Integer> productQuantities = new ArrayList<>();

                    // Populate the lists with product names, prices and images
                    for (ItemsDomain item : cartList) {
                        productNames.add(item.getTitle());
                        productPrices.add(String.valueOf(item.getPrice()));
                        productImages.add(item.getPicUrl().get(0)); // Assuming picUrl is a list and we're taking the first image
                        productIds.add(item.getId());
                        productCategories.add(item.getCategory());
                        productQuantities.add(item.getQuantity());
                    }

                    // Pass the lists as extras in the intent
                    intent.putStringArrayListExtra("productNames", productNames);
                    intent.putStringArrayListExtra("productPrices", productPrices);
                    intent.putStringArrayListExtra("productImages", productImages);
                    intent.putStringArrayListExtra("productIds", productIds);
                    intent.putStringArrayListExtra("productCategories", productCategories);
                    intent.putIntegerArrayListExtra("productQuantities", productQuantities);
                    Log.d("CartActivity", "productQuantities: " + productQuantities);
                    intent.putExtra("totalCost", totalCost);

                    startActivity(intent);
                }
            }
        });
    }

    // Initialize the view
    private void initView() {
        recyclerView = findViewById(R.id.cartView);
        emptyTxt = findViewById(R.id.emptyTxt);
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        taxTxt = findViewById(R.id.taxTxt);
        totalTxt = findViewById(R.id.totalTxt);
    }

    // Initialize the list
    private void initList() {
        // Set the layout manager for the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the existing cart items
        List<ItemsDomain> cartList = managementCart.getCart();

        // Check if the cart is empty
        if (cartList.isEmpty()) {
            emptyTxt.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            // Hide the empty text and show the recycler view
            emptyTxt.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new CartAdapter(this, cartList, managementCart);
            recyclerView.setAdapter(adapter);
        }
    }

    public void calculateCart() {
            totalCost = 0.0;
            // Get the existing cart items
            List<ItemsDomain> cartList = managementCart.getCart();

            // Calculate the total cost of the cart
            for (ItemsDomain item : cartList) {
                totalCost += item.getPrice() * item.getQuantity();
                Log.d("Quantity", "Quantity: " + item.getQuantity());
            }

            // Calculate the delivery fee
            totalFeeTxt.setText(String.format("₹%.2f", totalCost));
            // Calculate the tax
            totalTxt.setText(String.format("₹%.2f", totalCost));
    }
}