package com.example.amrozia;

import static androidx.core.view.GravityCompat.*;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ShippingReturnsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Declare ImageView for the grid icon
    ImageView gridIcon;

    // Declare DrawerLayout for the navigation drawer
    DrawerLayout drawerLayout;

    // Declare NavigationView
    NavigationView navigationView;

    // Override the onCreate method which is called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call the superclass's onCreate method with the saved instance state and persistent state
        super.onCreate(savedInstanceState);

        // Set the layout for this activity to be R.layout.contact_us
        setContentView(R.layout.shipping_returns);

        // Initialize the grid icon ImageView by finding it in the layout
        gridIcon = findViewById(R.id.grid_icon);

        // Initialize the NavigationView
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize the DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);

        // Set an OnClickListener on the grid icon
        // When the grid icon is clicked, the navigation drawer will open
        gridIcon.setOnClickListener(v -> {
            drawerLayout.openDrawer(START);
        });

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
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationUtils.navigateTo(item, this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}