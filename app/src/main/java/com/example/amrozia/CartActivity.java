package com.example.amrozia;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amrozia.Activity.MoreActivity;
import com.example.amrozia.Activity.PurchaseActivity;
import com.example.amrozia.Adapter.CartAdapter;
import com.example.amrozia.Domain.ItemsDomain;
import com.example.amrozia.Helper.ChangeNumberItemsListener;
import com.example.amrozia.Helper.ManagementCart;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements ChangeNumberItemsListener {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ManagementCart managementCart;
    private TextView emptyTxt, totalFeeTxt, deliveryTxt, taxTxt, totalTxt,numberItemTxt;
    private String productId, category, title, description, price;
    private ArrayList<String> picUrl;

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

        initView();
        initList();
        calculateCart();

        // Handle back button click
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, DetailActivity.class);
                startActivity(intent);
                finish();
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
                Intent intent = new Intent(CartActivity.this, PurchaseActivity.class);

                // Get the existing cart items
                List<ItemsDomain> cartList = managementCart.getCart();

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

                startActivity(intent);
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
            double totalCost = 0.0;
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