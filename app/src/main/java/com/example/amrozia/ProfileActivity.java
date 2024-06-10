package com.example.amrozia;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.amrozia.Activity.GiftCardActivity;
import com.example.amrozia.Activity.MoreActivity;
import com.example.amrozia.Activity.ShippingReturnsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private TextView userNameTextView;
    private TextView userEmailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize TextViews
        userNameTextView = findViewById(R.id.userNameTextView);
        userEmailTextView = findViewById(R.id.userEmailTextView);


        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Set the user's email
            String userEmail = user.getEmail();
            userEmailTextView.setText(userEmail);

            // Extract username from email (assuming email is in the format of "username@example.com")
            String username = userEmail.substring(0, userEmail.indexOf('@'));
            userNameTextView.setText(username);


            // Get the saved username and set it to userNameTextView
            String savedUsername = getSavedUsername();
            userNameTextView.setText(savedUsername);

            // Set up pen icon click listener
            ImageView pen = findViewById(R.id.pen);
            pen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Show dialog for username editing
                    showUsernameEditDialog();
                }
            });

            // Gift Cards Row
            LinearLayout giftCardsRow = findViewById(R.id.giftCardsRow);
            giftCardsRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, GiftCardActivity.class);
                    startActivity(intent);
                }
            });

            // Returns and Refunds Row
            LinearLayout returnsAndRefundsRow = findViewById(R.id.returnsAndRefundsRow);
            returnsAndRefundsRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, ShippingReturnsActivity.class);
                    startActivity(intent);
                }
            });

            // Share Row
            LinearLayout shareRow = findViewById(R.id.shareRow);
            shareRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Code to share the app
                    shareApp();
                }
            });

            // Logout Row
            LinearLayout logoutRow = findViewById(R.id.logoutRow);
            logoutRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Code to logout from the app
                    logout();
                }
            });

            // Set the onClickListeners for the cart buttons to go back to the home page
            LinearLayout cartButton = findViewById(R.id.cart_btn);
            cartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open the cart activity
                    Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            });

            // Set the onClickListeners for the cart buttons to go back to the home page
            LinearLayout homeButton = findViewById(R.id.home_btn);
            homeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open the cart activity
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            // Set the onClickListeners for the cart buttons to go back to the home page
            LinearLayout moreButton = findViewById(R.id.more_btn);
            moreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open the cart activity
                    Intent intent = new Intent(ProfileActivity.this, MoreActivity.class);
                    startActivity(intent);
                }
            });
        }

    }

    // Method to show dialog for username editing
    private void showUsernameEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Username");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newUsername = input.getText().toString();
                // Save the new username to SharedPreferences
                saveUsername(newUsername);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Method to save new username in SharedPreferences
    private void saveUsername(String newUsername) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("username", newUsername);
        editor.apply();
        // Update the displayed username
        userNameTextView.setText(newUsername);
    }

    // Method to retrieve username from SharedPreferences
    private String getSavedUsername() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return preferences.getString("username", "DefaultUsername");
    }

    // Method to share the app
    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing app!");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void logout() {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();

        // Clear session or authentication data
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        // Sign out from Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient signInClient = GoogleSignIn.getClient(this, gso);
        signInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Navigate to LoginActivity after signing out from Google
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finish the current activity to prevent user from navigating back
            }
        });
    }
}