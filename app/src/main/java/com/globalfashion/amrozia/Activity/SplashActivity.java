package com.globalfashion.amrozia.Activity;

import static com.globalfashion.amrozia.BuildConfig.BUSINESS_EMAIL;
import static com.globalfashion.amrozia.BuildConfig.WORK_EMAIL;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.globalfashion.amrozia.LoginActivity;
import com.globalfashion.amrozia.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// The SplashActivity class is responsible for displaying the splash screen when the app is launched
public class SplashActivity extends AppCompatActivity {

    // Declare the FirebaseAuth instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if the user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // If user is logged in
        if (currentUser != null) {
            // Check if email is verified
            if (currentUser.isEmailVerified()) {
                // Get the user's email
                String email = currentUser.getEmail();

                // Check if the email matches the specific emails for managing orders
                if (email != null && (email.equals(BUSINESS_EMAIL) || email.equals(WORK_EMAIL))) {
                    // Redirect to ManageOrdersActivity
                    Intent intent = new Intent(SplashActivity.this, ManageOrdersActivity.class);
                    startActivity(intent);
                } else {
                    // For other users, redirect to MainActivity
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            } else {
                // Redirect to LoginActivity if email is not verified
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        } else {
            // If user is not logged in, redirect to LoginActivity
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        // Finish the SplashActivity
        finish();
    }
}

