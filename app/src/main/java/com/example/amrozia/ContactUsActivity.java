package com.example.amrozia;

import static androidx.core.view.GravityCompat.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class ContactUsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
        setContentView(R.layout.contact_us);

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
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavigationUtils.navigateTo(item, this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}