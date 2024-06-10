package com.example.amrozia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amrozia.Activity.MoreActivity;
import com.example.amrozia.Activity.OrderConfirmationActivity;
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
    private TextView emptyTxt, totalFeeTxt, deliveryTxt, taxTxt, totalTxt;
    private String productId, category, title, description, price, size;
    private ArrayList<String> picUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        managementCart = new ManagementCart(this);

        // Receive data from DetailActivity
        Intent intent = getIntent();
        ItemsDomain item = (ItemsDomain) intent.getSerializableExtra("item");

        // Extract item details from the received object
        if (item != null) {
            productId = item.getId();
            category = item.getCategory();
            title = item.getTitle();
            description = item.getDescription();
            picUrl = item.getPicUrl();
            price = String.valueOf(item.getPrice());
            size = item.getSize();

            // Add the new item to the cart and save the updated cart
            managementCart.addToCart(new ItemsDomain(productId, title, description, picUrl, Double.parseDouble(price), category, size));

        } else {
            // Handle case where item is null
            setContentView(R.layout.activity_cart);
        }

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
                Intent intent = new Intent(CartActivity.this, OrderConfirmationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        recyclerView = findViewById(R.id.cartView);
        emptyTxt = findViewById(R.id.emptyTxt);
        totalFeeTxt = findViewById(R.id.totalFeeTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        taxTxt = findViewById(R.id.taxTxt);
        totalTxt = findViewById(R.id.totalTxt);
    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the existing cart items
        List<ItemsDomain> cartList = managementCart.getCart();

        if (cartList.isEmpty()) {
            emptyTxt.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyTxt.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new CartAdapter(this, cartList, managementCart);
            recyclerView.setAdapter(adapter);
        }
    }


    @Override
    public void changed() {
        calculateCart();
    }

    public void calculateCart() {
            double totalCost = 0.0;
            List<ItemsDomain> cartList = managementCart.getCart();

            for (ItemsDomain item : cartList) {
                totalCost += item.getPrice() * item.getQuantity();
            }

            totalFeeTxt.setText(String.format("₹%.2f", totalCost));
            totalTxt.setText(String.format("₹%.2f", totalCost));
    }
}