package com.example.amrozia.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.amrozia.R;

public class GiftCardActivity extends MoreActivity {
    Button buy_now;

    // Override the onCreate method which is called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call the superclass's onCreate method with the saved instance state and persistent state
        super.onCreate(savedInstanceState);

        // Set the layout for this activity to be R.layout.contact_us
        setContentView(R.layout.gift_card);

        buy_now = findViewById(R.id.buy_now);
        buy_now.setOnClickListener(v -> {
            Toast.makeText(GiftCardActivity.this, "Thank you for your purchase!", Toast.LENGTH_SHORT).show();
        });
    }
}