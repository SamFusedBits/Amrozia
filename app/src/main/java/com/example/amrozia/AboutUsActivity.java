package com.example.amrozia;

import static androidx.core.view.GravityCompat.START;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.cardview.widget.CardView;

import com.google.android.material.navigation.NavigationView;

public class AboutUsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Declare ImageView for the grid icon
    ImageView gridIcon;

    // Declare DrawerLayout for the navigation drawer
    DrawerLayout drawerLayout;

    // Declare NavigationView
    NavigationView navigationView;

    // Declare CardViews
    CardView facebookCard, twitterCard, instagramCard, whatsappCard;

    // Override the onCreate method which is called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call the superclass's onCreate method with the saved instance state and persistent state
        super.onCreate(savedInstanceState);

        // Set the layout for this activity to be R.layout.contact_us
        setContentView(R.layout.about_us);

        // Initialize the grid icon ImageView by finding it in the layout
        gridIcon = findViewById(R.id.grid_icon);

        // Initialize the NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize the DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);

        // Initialize CardViews
        facebookCard = findViewById(R.id.card_facebook);
        twitterCard = findViewById(R.id.card_twitter);
        instagramCard = findViewById(R.id.card_instagram);
        whatsappCard = findViewById(R.id.card_whatsapp);

        // Set click listeners for the CardViews
        // Open UrLs in the app installed, otherwise in the default browser
        facebookCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInApp("https://facebook.com/amrozia.in");
            }
        });

        twitterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInApp("https://twitter.com/globalfashion85");
            }
        });

        instagramCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInApp("https://instagram.com/global_fashion_amrozia");
            }
        });

        whatsappCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUrlInApp("https://wa.me/+917219777055");
            }
        });

        // Set an OnClickListener on the grid icon
        // When the grid icon is clicked, the navigation drawer will open
        gridIcon.setOnClickListener(v -> {
            drawerLayout.openDrawer(START);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationUtils.navigateTo(item, this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
