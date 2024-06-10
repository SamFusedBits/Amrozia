package com.example.amrozia.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.amrozia.R;

public class ShippingReturnsActivity extends MoreActivity {

    // Override the onCreate method which is called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call the superclass's onCreate method with the saved instance state and persistent state
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shipping_returns);

        // Find the contact_support_email button and set OnClickListener
        findViewById(R.id.contact_support_email).setOnClickListener(v -> {
            // Create an intent to send an email
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@amrozia.in"}); // Email address
            intent.putExtra(Intent.EXTRA_SUBJECT, "Shipping & Returns Support"); // Subject of the email
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }else {
                // No email app found, show a toast or handle the situation accordingly
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}