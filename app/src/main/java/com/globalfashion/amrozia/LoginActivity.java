package com.globalfashion.amrozia;

import static com.globalfashion.amrozia.BuildConfig.BUSINESS_EMAIL;
import static com.globalfashion.amrozia.BuildConfig.GOOGLE_CLIENT_ID;
import static com.globalfashion.amrozia.BuildConfig.WORK_EMAIL;
import static com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.globalfashion.amrozia.Activity.ManageOrdersActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
// This activity is responsible for handling user login
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        emailEditText = findViewById(R.id.editTextTextPersonName);
        passwordEditText = findViewById(R.id.editTextTextPersonName2);
        loginButton = findViewById(R.id.button);
        forgotPasswordTextView = findViewById(R.id.recover_password);
        progressBar = new ProgressBar(this);

        mAuth = FirebaseAuth.getInstance();  // Initialize Firebase Auth
        db = FirebaseFirestore.getInstance();  // Initialize Firestore

        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);  // Initialize shared preferences

        // Initialize the ActivityResultLauncher
        signInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            // Handle the result of the Google Sign-In process
            if (result.getResultCode() == Activity.RESULT_OK) {
                // Get the result data from the Intent
                Intent data = result.getData();
                // Handle the Google Sign-In result
                handleSignInResult(data);
            }
        });

        // Check if the user is already logged in
        if (isLoggedIn()) {
            // Redirect to the main activity
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Set OnClickListener on Forgot Password TextView
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = emailEditText.getText().toString().trim();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (input.matches(emailPattern)) {
                    // Handle email forgot password
                    sendPasswordResetEmail(input);
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set OnClickListener on Google Sign-In button
        findViewById(R.id.google_signin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(); // Start Google Sign-In process
            }
        });

        // Set OnClickListener on Signup Now TextView
        TextView signupNowTextView = findViewById(R.id.signup_now);
        signupNowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener on Phone Sign-In button
        /*LinearLayout phoneSignInButton = findViewById(R.id.phone_signin_button);
        phoneSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, PhoneAuthActivity.class);
                startActivity(intent);
            }
        });*/

    }

    // Function to check if the user is already logged in
    private boolean isLoggedIn() {
        // Check if the user is logged in based on stored login state
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }

    // Function to log in the user
    private void loginUser() {
        // Get the email and password from the input fields
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate the email and password
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (TextUtils.isEmpty(email) || !email.matches(emailPattern)) {
            emailEditText.setError("Please enter a valid email address.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Sign in the user with the email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                // Email is verified, proceed to next screen
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("isLoggedIn", true);
                                editor.apply();

                                checkUserEmail(email);
                                finish();
                            } else {
                                // Email not verified
                                Toast.makeText(LoginActivity.this, "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed. Please check your credentials.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Function to handle email-based redirection
    private void checkUserEmail(String email) {
        if (email.equals(BUSINESS_EMAIL) || email.equals(WORK_EMAIL)) {
            // Redirect to ManageOrdersActivity if email is allowed
            Intent intent = new Intent(LoginActivity.this, ManageOrdersActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Redirect to MainActivity for all other users
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Function to send a password reset email
    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (task.getException() instanceof FirebaseTooManyRequestsException) {
                                Toast.makeText(LoginActivity.this, "Too many requests. Please try again later.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        });
    }

    // Start the Google Sign-In process
    private void signIn() {
        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(DEFAULT_SIGN_IN)
                // Request the user's ID token
                .requestIdToken(GOOGLE_CLIENT_ID)
                // Request the user's email address
                .requestEmail()
                // Build the GoogleSignInClient
                .build();

        // Create a GoogleSignInClient with the options
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        // Get the sign-in intent
        Intent signInIntent = googleSignInClient.getSignInIntent();
        // Start the sign-in process
        signInLauncher.launch(signInIntent);
    }

    // Handle the result of the Google Sign-In process
    private void handleSignInResult(Intent data) {
        // Get the result of the sign-in process
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign-In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            // Authenticate with Firebase using the Google ID token
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
            // Add more detailed error handling
            switch (e.getStatusCode()) {
                case GoogleSignInStatusCodes.SIGN_IN_CANCELLED:
                    Log.d(TAG, "Google Sign In cancelled");
                    break;
                case GoogleSignInStatusCodes.NETWORK_ERROR:
                    Log.d(TAG, "Network error occurred");
                    break;
                default:
                    Log.d(TAG, "Google Sign In failed with status code: " + e.getStatusCode());
            }
            Toast.makeText(this, "Google Sign In failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Authenticate with Firebase using the Google ID token
    private void firebaseAuthWithGoogle(String idToken) {
        // Create a Google credential with the ID token
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            if (user != null) {
                                // Get user email and name
                                String email = user.getEmail();
                                String name = user.getDisplayName();
                                String image = user.getPhotoUrl().toString();
                                String phone = user.getPhoneNumber();

                                // Check if user already exists in Firestore
                                db.collection("users").document(user.getUid()).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "signInWithCredential:success");
                                                    // Check if the document exists in Firestore
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        // User already exists, no need to create a new document
                                                        Log.d(TAG, "User already exists in Firestore");
                                                    } else {
                                                        // Create a new user with a name and email
                                                        Map<String, Object> userMap = new HashMap<>();
                                                        userMap.put("name", name);
                                                        userMap.put("email", email);
                                                        userMap.put("profileImageUrl", image);
                                                        userMap.put("phone", phone);

                                                        // Add a new document with the user ID
                                                        db.collection("users").document(user.getUid())
                                                                .set(userMap)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.d(TAG, "User details successfully written!");
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Log.w(TAG, "Error writing document", e);
                                                                        Toast.makeText(LoginActivity.this, "Failed to save user details. Please try again.",
                                                                                Toast.LENGTH_SHORT).show();
                                                                        // Update the UI with a null user object to indicate failure
                                                                        updateUI(null);
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                                    // If sign in fails, display a message to the user.
                                                    Toast.makeText(LoginActivity.this, "Authentication failed. Please try again.",
                                                            Toast.LENGTH_SHORT).show();
                                                    // Update the UI with a null user object to indicate failure
                                                    updateUI(null);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Store login state locally
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // Set the isLoggedIn flag to true
            editor.putBoolean("isLoggedIn", true);
            editor.apply();

            checkUserEmail(user.getEmail());
        }else {
            Toast.makeText(LoginActivity.this, "Failed to sign in. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
}