package com.globalfashion.amrozia;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.globalfashion.amrozia.Activity.MoreActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
// This activity displays the user profile details and allows the user to edit and save the profile information
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
    LinearLayout shareRow, logoutRow, homeBtn, cartBtn, moreBtn;
    private GoogleSignInClient googleSignInClient;
    // Request code for reading external storage
    private static final int READ_EXTERNAL_STORAGE_PERMISSION_CODE = 1;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finish();
        } else {
            finish();
        }
    }

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
        homeBtn = findViewById(R.id.home_btn);
        cartBtn = findViewById(R.id.cart_btn);
        moreBtn = findViewById(R.id.more_btn);
        shareRow = findViewById(R.id.shareRow);
        logoutRow = findViewById(R.id.logoutRow);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // Request the user's ID token
                .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
                // Request the user's email address
                .requestEmail()
                // Build the GoogleSignInClient with the options specified by gso
                .build();

        // Initialize GoogleSignInClient
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Fetch and display user details
        fetchUserDetails();

        // Load cached profile image
        loadCachedProfileImage();

        // Handle edit profile button click
        editProfileButton.setOnClickListener(v -> toggleEditMode());

        // Handle save profile button click
        saveProfileButton.setOnClickListener(v -> saveProfileChanges());

        // Handle home button click
        homeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Handle cart button click
        cartBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Handle more button click
        moreBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, MoreActivity.class);
            startActivity(intent);
        });

        // Handle share button click
        shareRow.setOnClickListener(v -> shareApp());

        logoutRow.setOnClickListener(v -> logoutUser());
    }

    // Method to share product details
    private void shareApp() {
        // Create and launch the share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        // Set the type of content to share
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out the Amrozia app on the Play Store! https://play.google.com/store/apps/details?id=com.globalfashion.amrozia");
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    // Method to log out the user
    private void logoutUser() {
        // Log out the user from Firebase Authentication
        mAuth.signOut();

        // Clear cached Google Sign-In account information
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            // Redirect the user to the login screen
            Intent intent = new Intent(this, MainActivity.class);
            // Clear the back stack and start the new activity
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    // Activity result launcher for selecting an image from the gallery
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            // StartActivityForResult contract to launch the gallery
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Check if the result is OK and the data is not null
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Get the selected image URI
                    Intent data = result.getData();
                    // Check if the data is not null
                    if (data != null && data.getData() != null) {
                        // Get the selected image URI
                        Uri selectedImage = data.getData();
                        // Get the MIME type of the selected image
                        String mimeType = getContentResolver().getType(selectedImage);
                        // Check if the MIME type is not null and the image is a JPEG, JPG or PNG image
                        if (mimeType != null && (mimeType.equals("image/jpeg") || (mimeType.equals("image/jpg") || mimeType.equals("image/png")))) {
                            // Process the image
                            uploadProfileImage(selectedImage);
                        } else {
                            Toast.makeText(this, "Please select a JPG, JPEG, or PNG image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
    );

    // Method to open the gallery to select an image
    private void openGallery() {
        // Check if the device is running Android 10 (Q) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Check if the app has permission to read external storage
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            // Set the type of files to select
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            // Set the MIME type to images
            intent.setType("image/*");

            // Specify the MIME types
            String[] mimeTypes = {"image/jpeg","image/jpg", "image/png"};
            // Set the MIME types
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

            // Launch the gallery activity
            galleryLauncher.launch(intent);
        } else {
            // Check if the app has permission to read external storage
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Create an intent to pick an image from the gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Set the type of files to select
                intent.setType("image/*");

                // Specify the MIME types
                String[] mimeTypes = {"image/jpeg", "image/jpg", "image/png"};
                // Set the MIME types
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

                // Launch the gallery activity
                galleryLauncher.launch(intent);
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    // Activity result launcher for requesting permissions
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted
                    openGallery();
                } else {
                    Toast.makeText(this, "Permission Denied. Cannot access images.", Toast.LENGTH_SHORT).show();
                }
            });

    // Method to upload the profile image to Firebase Storage
    private void uploadProfileImage(Uri imageUri) {
        if (mAuth.getCurrentUser() != null) {
            // Create a storage reference with the user's ID as the filename
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("User Profile Images/" + mAuth.getCurrentUser().getUid() + ".jpg");
            // Upload the image to Firebase Storage
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

    // Method to update the profile image URL in Firestore
    private void updateProfileImageUrl(String imageUrl) {
        if (mAuth.getCurrentUser() != null) {
            // Update Firestore with new image URL
            firestore.collection("users").document(mAuth.getCurrentUser().getUid())
                    .update("profileImageUrl", imageUrl)
                    .addOnSuccessListener(aVoid -> {
                        // Update local cached user object if necessary
                        if (currentUser != null) {
                            // Update profile image URL in the cached user object
                            currentUser.setProfileImageUrl(imageUrl);
                        }
                        Toast.makeText(this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();
                        // Load the new profile image
                        loadProfileImage(imageUrl);
                    })
                    .addOnFailureListener(exception ->
                            Toast.makeText(this, "Failed to update profile image: " + exception.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }

    // Method to request storage permission
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // Call super method to ensure the activity result is handled
        super.onActivityResult(requestCode, resultCode, data);
        // Check if the request code matches the storage permission request code
        if (requestCode == 2296) {
            // Check if the device is running Android 11 (R) or higher
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Check if the app has permission to read external storage
                if (Environment.isExternalStorageManager()) {
                    // Permission is granted
                    openGallery();
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Method to request storage permission
    private void requestStoragePermission() {
        // Check if the device is running Android 11 (R) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Check if the app has permission to read external storage
            try {
                // Request permission to manage all files
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                // Set the package name
                intent.addCategory("android.intent.category.DEFAULT");
                // Set the data URI to the app's package name
                intent.setData(Uri.parse(String.format("package:%s", getPackageName())));
                // Start the activity for result with the request code
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                // Set the action to manage all files access permission
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                // Start the activity for result with the request code
                startActivityForResult(intent, 2296);
            }
        } else {
            // Check if the app has permission to read external storage
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_PERMISSION_CODE
            );
        }
    }

    // Method to fetch user details from Firestore
    private void fetchUserDetails() {
        // Check if the user is logged in
        if (mAuth.getCurrentUser() != null) {
            firestore.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                    .addOnSuccessListener(document -> {
                        // Check if the document exists
                        if (document != null && document.exists()) {
                            // Convert the document to a User object
                            currentUser = document.toObject(User.class);
                            // Check if the user object is not null
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

    // Method to save profile image URL in SharedPreferences
    private void saveProfileImageUrl(String imageUrl) {
        getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
                .edit()
                .putString("profileImageUrl", imageUrl)
                .apply();
    }

    // Method to load profile image using Glide
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

    // Method to load cached profile image URL from SharedPreferences
    private void loadCachedProfileImage() {
        // Load cached profile image URL from SharedPreferences
        String profileImageUrl = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
                .getString("profileImageUrl", null);
        // Load profile image if URL is not null
        if (profileImageUrl != null) {
            loadProfileImage(profileImageUrl);
        }
    }

    // Method to toggle between edit and view mode
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

            // Enable click listener on profileImageView
            profileImageView.setOnClickListener(v ->
                openGallery());
        } else {
            // Switch back to view mode
            usernameTextView.setVisibility(View.VISIBLE);
            usernameEditText.setVisibility(View.GONE);
            phoneTextView.setVisibility(View.VISIBLE);
            phoneEditText.setVisibility(View.GONE);
            saveProfileButton.setVisibility(View.GONE);
            editProfileButton.setVisibility(View.VISIBLE);
        }
        // Toggle isEditMode flag value to switch between modes
        isEditMode = !isEditMode;
    }

    // Method to save profile changes to Firestore
    private void saveProfileChanges() {
        // Get the new username and phone number
        String newUsername = usernameEditText.getText().toString();
        String newPhone = phoneEditText.getText().toString();

        // If username or phone number is not changed, keep the previous value
        if (newUsername.isEmpty()) {
            newUsername = currentUser.getName();
        }
        if (newPhone.isEmpty()) {
            newPhone = currentUser.getPhone();
        }

        // Update Firestore with the new username and phone number
        if (mAuth.getCurrentUser() != null) {
            String finalNewUsername = newUsername;
            String finalNewPhone = newPhone;
            // Update Firestore with new username and phone number
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

    // Method to request storage permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check if the request code matches the storage permission request code
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
            // Check if the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission Denied. Please enable storage access permission in app settings.", Toast.LENGTH_SHORT).show();
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