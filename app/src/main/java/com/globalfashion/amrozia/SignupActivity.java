package com.globalfashion.amrozia;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.globalfashion.amrozia.Activity.PhoneAuthActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
// This activity allows users to sign up for an account
public class SignupActivity extends AppCompatActivity {
    // Constants for log messages and views
    private static final String TAG = "SignupActivity";
    private EditText nameEditText, emailEditText, phoneEditText, passwordEditText, rePasswordEditText;
    private Button signupButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    // Verification ID for phone number
    private String verificationId;
    // Token to resend the verification code
    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        nameEditText = findViewById(R.id.edit_text_name);
        emailEditText = findViewById(R.id.edit_text_email);
        phoneEditText = findViewById(R.id.edit_text_phone);
        passwordEditText = findViewById(R.id.edit_text_password);
        rePasswordEditText = findViewById(R.id.edit_text_repassword);
        signupButton = findViewById(R.id.button_signup);
        progressBar = new ProgressBar(this);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignupActivity.this, "Signup in progress. Please wait...", Toast.LENGTH_SHORT).show();
                createAccount();
            }
        });
    }

    // Create an account with the given details
    private void createAccount() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String rePassword = rePasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Name is required.");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required.");
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password should be at least 6 characters.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required.");
            return;
        }

        if (TextUtils.isEmpty(rePassword) || !password.equals(rePassword)) {
            rePasswordEditText.setError("Passwords do not match.");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneEditText.setError("Phone number is required.");
            return;
        }

        //Prepend the country code to the phone number
        if (!phone.startsWith("+91")) {
            phone = "+91" + phone;
        }

        progressBar.setVisibility(View.VISIBLE);

        // Verify phone number
        verifyPhoneNumber(phone);
    }

    // Verify the phone number using Firebase Authentication
    private void verifyPhoneNumber(String phone) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber(phone)       // Phone number to verify
                .setTimeout(10L, TimeUnit.SECONDS) // Timeout duration
                .setActivity(this)          // Activity for callback
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        // Automatically sign in the user
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // Verification failed due to an invalid request or other errors
                        Log.w(TAG, "onVerificationFailed", e);

                        // Get the email and password from the input fields
                        String email = emailEditText.getText().toString().trim();
                        String password = passwordEditText.getText().toString().trim();

                        // Call the verifyWithEmail method
                        verifyWithEmail(email, password);
                    }

                    // Send the verification code to the user
                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // Save the verification ID and resend token
                        SignupActivity.this.verificationId = verificationId;
                        // Save the resend token
                        resendToken = token;
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SignupActivity.this, "Verification code sent!", Toast.LENGTH_SHORT).show();
                        // Redirect to enter verification code screen
                        Intent intent = new Intent(SignupActivity.this, VerifySignupCodeActivity.class);
                        intent.putExtra("verificationId", verificationId);
                        intent.putExtra(("name"), nameEditText.getText().toString());
                        intent.putExtra("email", emailEditText.getText().toString());
                        intent.putExtra("phone", phone);
                        startActivity(intent);
                        finish();
                    }
                })
                .build();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(options);
    }

    // Verify the email and password
    private void verifyWithEmail(String email, String password) {
    mAuth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();

                    // After creating the user account, send the verification email
                    if (user != null) {
                        user.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        Toast.makeText(SignupActivity.this, "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();

                                        // Save user details to Firestore
                                        saveUserDetailsToFirestore(user.getUid(), nameEditText.getText().toString(), emailEditText.getText().toString(), phoneEditText.getText().toString());

                                        // Sign out the user after sending the verification email
                                        FirebaseAuth.getInstance().signOut();

                                        // Redirect to LoginActivity
                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.e(TAG, "sendEmailVerification", task.getException());
                                        Toast.makeText(SignupActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }
                } else {
                    // If account creation failed (account likely exists), check for existing user
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // User exists, check if email is verified
                                        FirebaseUser existingUser = mAuth.getCurrentUser();
                                        if (existingUser != null && !existingUser.isEmailVerified()) {
                                            // Resend verification email
                                            existingUser.sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "Resent verification email.");
                                                                Toast.makeText(SignupActivity.this, "Verification email resent to " + existingUser.getEmail(), Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                                                finish();
                                                            } else {
                                                                Log.e(TAG, "Resend email failed.", task.getException());
                                                                Toast.makeText(SignupActivity.this, "Failed to resend verification email.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                            FirebaseAuth.getInstance().signOut();
                                        } else if (existingUser != null && existingUser.isEmailVerified()) {
                                            // Notify the user that the email is already verified
                                            Toast.makeText(SignupActivity.this, "Email is already verified.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        // Authentication failed, account may not exist or wrong password
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(SignupActivity.this, "Authentication Failed. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    // Sign in with the phone authentication credential
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            // Save user details to Firestore
                            saveUserDetailsToFirestore(user.getUid(), nameEditText.getText().toString(), emailEditText.getText().toString(), phoneEditText.getText().toString());
                            updateUI(user);
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(SignupActivity.this, "Invalid code", Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(SignupActivity.this, "This phone number is already registered", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, PhoneAuthActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignupActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                        updateUI(null);
                    }
                });
    }

    // Save the user details to Firestore
    private void saveUserDetailsToFirestore(String userId, String name, String email, String phone) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("phone", phone);

        // Add a new document with a generated ID
        db.collection("users").document(userId)
                .set(userMap)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User details successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    // Update the UI based on the user's authentication status
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}