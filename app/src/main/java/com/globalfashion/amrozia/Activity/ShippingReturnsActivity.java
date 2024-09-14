package com.globalfashion.amrozia.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.globalfashion.amrozia.R;
// The ShippingReturnsActivity class is responsible for displaying the shipping and returns screen
public class ShippingReturnsActivity extends MoreActivity {

    // Override the onCreate method which is called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call the superclass's onCreate method with the saved instance state and persistent state
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipping_returns);

        // Find the contact_support_email button and set OnClickListener
        findViewById(R.id.contact_support_email).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:support@amrozia.in")); // Set the recipient email address directly in the URI
            intent.putExtra(Intent.EXTRA_SUBJECT, "Shipping & Returns Support");

            // Try to start the email intent and show a toast if no email apps are installed
            try {
                startActivity(Intent.createChooser(intent, "Send email using..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "No email apps installed.", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the activity when the back button is clicked
                finish();
            }
        });
    }
}