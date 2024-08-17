package com.example.amrozia;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileActivity extends AppCompatActivity {
    private TextView usernameTextView;
    private EditText usernameEditText;
    private TextView phoneTextView;
    private EditText phoneEditText;
    private TextView emailTextView;
    private Button editProfileButton;
    private Button saveProfileButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private User currentUser;
    private boolean isEditMode = false;
    private Uri profileImageUri; // To store selected image URI
    private ImageView profileImageView;
    LinearLayout shareRow, logoutRow;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.profile_image);  // Initialization

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        usernameTextView = findViewById(R.id.username_textview);
        usernameEditText = findViewById(R.id.username_edittext);
        phoneTextView = findViewById(R.id.phone_textview);
        phoneEditText = findViewById(R.id.phone_edittext);
        emailTextView = findViewById(R.id.email_textview);
        editProfileButton = findViewById(R.id.edit_profile_button);
        saveProfileButton = findViewById(R.id.save_profile_button);
        shareRow = findViewById(R.id.shareRow);
        logoutRow = findViewById(R.id.logoutRow);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Fetch and display user details
        fetchUserDetails();

        // Load cached profile image
        loadCachedProfileImage();

        // Handle edit profile button click
        editProfileButton.setOnClickListener(v -> toggleEditMode());

        // Handle save profile button click
        saveProfileButton.setOnClickListener(v -> saveProfileChanges());

        // Handle profile image click to open gallery
        profileImageView.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                requestStoragePermission();
            }
        });

        // Handle share button click
        shareRow.setOnClickListener(v -> shareApp());

        logoutRow.setOnClickListener(v -> logoutUser());
    }

    // Method to share product details
    private void shareApp() {
        // Create and launch the share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out the Amrozia app on the Play Store! https://play.google.com/store/apps/details?id=com.globalfashion.amrozia");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void logoutUser() {
        // Log out the user from Firebase Authentication
        mAuth.signOut();

        // Clear cached Google Sign-In account information
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            // Redirect the user to the login screen
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Uri selectedImageUri = data != null ? data.getData() : null;
                    if (selectedImageUri != null) {
                        profileImageUri = selectedImageUri;
                        profileImageView.setImageURI(selectedImageUri);
                        // Optionally, you can upload the image to Firebase Storage here
                        uploadProfileImage(profileImageUri);
                    }
                }
            });

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void uploadProfileImage(Uri imageUri) {
        if (mAuth.getCurrentUser() != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("User Profile Images/" + mAuth.getCurrentUser().getUid() + ".jpg");
            storageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL from Firebase Storage
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Update Firestore with the new image URL
                            updateProfileImageUrl(uri.toString());
                        }).addOnFailureListener(exception ->
                                Toast.makeText(this, "Failed to retrieve download URL: " + exception.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    })
                    .addOnFailureListener(exception ->
                            Toast.makeText(this, "Failed to upload image: " + exception.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private void updateProfileImageUrl(String imageUrl) {
        if (mAuth.getCurrentUser() != null) {
            // Update Firestore with new image URL
            firestore.collection("users").document(mAuth.getCurrentUser().getUid())
                    .update("profileImageUrl", imageUrl)
                    .addOnSuccessListener(aVoid -> {
                        // Update local cached user object if necessary
                        if (currentUser != null) {
                            currentUser.setProfileImageUrl(imageUrl);
                        }
                        Toast.makeText(this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(exception ->
                            Toast.makeText(this, "Failed to update profile image: " + exception.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1011
        );
    }

    private void fetchUserDetails() {
        if (mAuth.getCurrentUser() != null) {
            firestore.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(document -> {
                        if (document != null && document.exists()) {
                            currentUser = document.toObject(User.class);
                            if (currentUser != null) {
                                // Set other fields (username, email, phone)
                                usernameTextView.setText(currentUser.getName() != null ? currentUser.getName() : "N/A");
                                emailTextView.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "N/A");
                                phoneTextView.setText(currentUser.getPhone() != null ? currentUser.getPhone() : "Not Specified");

                                // Load and cache profile image URL
                                loadProfileImage(currentUser.getProfileImageUrl());

                                // Cache profile image URL in SharedPreferences
                                saveProfileImageUrl(currentUser.getProfileImageUrl());
                            }
                        } else {
                            Toast.makeText(this, "No such document", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(exception ->
                            Toast.makeText(this, "Error getting documents: " + exception, Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private void saveProfileImageUrl(String imageUrl) {
        getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
                .edit()
                .putString("profileImageUrl", imageUrl)
                .apply();
    }

    private void loadProfileImage(String imageUrl) {
        if (imageUrl != null) {
            // Load image using Glide into profileImageView
            Glide.with(this)
                    .load(imageUrl)
                    .circleCrop()
                    .placeholder(R.drawable.user_icon) // Optional placeholder
                    .error(R.drawable.user_icon) // Optional error image
                    .into(profileImageView);
        }
    }

    private void loadCachedProfileImage() {
        String profileImageUrl = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
                .getString("profileImageUrl", null);
        if (profileImageUrl != null) {
            loadProfileImage(profileImageUrl);
        }
    }

    private void toggleEditMode() {
        if (!isEditMode) {
            // Switch to edit mode
            usernameTextView.setVisibility(View.GONE);
            usernameEditText.setVisibility(View.VISIBLE);


            // Only show phoneEditText if phone number is not available
            if (currentUser.getPhone() == null || currentUser.getPhone().isEmpty()) {
                phoneTextView.setVisibility(View.GONE);
                phoneEditText.setVisibility(View.VISIBLE);
            }

            saveProfileButton.setVisibility(View.VISIBLE);
            editProfileButton.setVisibility(View.GONE);
        } else {
            // Switch back to view mode
            usernameTextView.setVisibility(View.VISIBLE);
            usernameEditText.setVisibility(View.GONE);
            phoneTextView.setVisibility(View.VISIBLE);
            phoneEditText.setVisibility(View.GONE);
            saveProfileButton.setVisibility(View.GONE);
            editProfileButton.setVisibility(View.VISIBLE);
        }
        isEditMode = !isEditMode;
    }

    private void saveProfileChanges() {
        String newUsername = usernameEditText.getText().toString();
        String newPhone = phoneEditText.getText().toString();

        // If username or phone number is not changed, keep the previous value
        if (newUsername.isEmpty()) {
            newUsername = currentUser.getName();
        }
        if (newPhone.isEmpty()) {
            newPhone = currentUser.getPhone();
        }

        if (mAuth.getCurrentUser() != null) {
            String finalNewUsername = newUsername;
            String finalNewPhone = newPhone;
            firestore.collection("users").document(mAuth.getCurrentUser().getUid())
                    .update("name", newUsername, "phone", newPhone)
                    .addOnSuccessListener(aVoid -> {
                        // Update UI
                        usernameTextView.setText(finalNewUsername);
                        phoneTextView.setText(finalNewPhone);
                        // Switch back to view mode
                        toggleEditMode();
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(exception ->
                            Toast.makeText(this, "Failed to update profile: " + exception.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1011) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // User class to map user data from Firestore
    public static class User {
        private String name;
        private String email;
        private String phone;
        private String profileImageUrl;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getProfileImageUrl() {
            return profileImageUrl;
        }

        public void setProfileImageUrl(String profileImageUrl) {
            this.profileImageUrl = profileImageUrl;
        }
    }
}