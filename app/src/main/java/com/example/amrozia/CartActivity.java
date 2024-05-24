package com.example.amrozia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class CartActivity extends MainActivity {
    private View backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize the back button
        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the current activity
            }
        });
    }
    // Add methods to handle adding and removing items from the cart
    // Add methods to calculate the total price of the items in the cart
    // Add methods to display the items in the cart
    // Add methods to clear the cart
    }
