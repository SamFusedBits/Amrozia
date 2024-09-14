package com.globalfashion.amrozia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.globalfashion.amrozia.CartActivity;
import com.globalfashion.amrozia.ContactUsActivity;
import com.globalfashion.amrozia.MainActivity;
import com.globalfashion.amrozia.ProfileActivity;
import com.globalfashion.amrozia.R;
// The MoreActivity class is responsible for displaying the more screen
public class MoreActivity extends AppCompatActivity {
    // Method to close the app when the back button is pressed
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        // Set up the page navigation
        setupPageNavigation();

        // Set the onClickListeners for the cart buttons to go back to the home page
        LinearLayout profileButton = findViewById(R.id.profile_btn);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the cart activity
                Intent intent = new Intent(MoreActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Set the onClickListeners for the cart buttons to go back to the home page
        LinearLayout homeButton = findViewById(R.id.home_btn);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the cart activity
                Intent intent = new Intent(MoreActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Set the onClickListeners for the cart buttons to go back to the home page
        LinearLayout cartButton = findViewById(R.id.cart_btn);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the cart activity
                Intent intent = new Intent(MoreActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    // Set up the page navigation to navigate to different activities
    private void setupPageNavigation() {
        findViewById(R.id.nav_contact).setOnClickListener(v -> navigateTo(ContactUsActivity.class));
        findViewById(R.id.nav_blog).setOnClickListener(v -> navigateTo(BlogActivity.class));
        findViewById(R.id.nav_faq).setOnClickListener(v -> navigateTo(FaqActivity.class));
        findViewById(R.id.nav_about).setOnClickListener(v -> navigateTo(AboutUsActivity.class));
        findViewById(R.id.nav_returns).setOnClickListener(v -> navigateTo(ShippingReturnsActivity.class));
        findViewById(R.id.nav_my_orders).setOnClickListener(v -> navigateTo(MyOrdersActivity.class));
    }

    // Navigate to the specified activity
    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(MoreActivity.this, activityClass);
        startActivity(intent);
    }
}
