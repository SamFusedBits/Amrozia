package com.example.amrozia.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.amrozia.R;

public class AboutUsActivity extends MoreActivity {

    ImageView facebookLogo, twitterLogo, instagramLogo, whatsappLogo;

    // Override the onCreate method which is called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call the superclass's onCreate method with the saved instance state and persistent state
        super.onCreate(savedInstanceState);

        // Set the layout for this activity to be R.layout.contact_us
        setContentView(R.layout.about_us);
        
        facebookLogo = findViewById(R.id.facebook_logo);
        twitterLogo = findViewById(R.id.twitter_logo);
        instagramLogo = findViewById(R.id.instagram_logo);
        whatsappLogo = findViewById(R.id.whatsapp_logo);

        // Set click listeners for the CardViews
        // Open UrLs in the app installed, otherwise in the default browser
        facebookLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInApp("https://facebook.com/amrozia.in");
            }
        });

        twitterLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInApp("https://twitter.com/globalfashion85");
            }
        });

        instagramLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInApp("https://instagram.com/global_fashion_amrozia");
            }
        });

        whatsappLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInApp("https://wa.me/+917219777055");
            }
        });
    }

    // The openUrlInApp method will construct a URL and start an Intent to view the URL.
    // If the corresponding app (Facebook, Twitter, Instagram, WhatsApp) is installed on the device, the URL will open in the app.
    // Otherwise, the URL will open in the default web browser
    private void openUrlInApp(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
