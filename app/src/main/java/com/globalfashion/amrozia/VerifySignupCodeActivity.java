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

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
// This activity verifies the signup code sent to the user's phone number
public class VerifySignupCodeActivity extends AppCompatActivity {
    // Constants for log messages and views
    private static final String TAG = "VerifySignupCodeActivity";
    private EditText codeEditText;
    private Button verifyButton;
    private ProgressBar progressBar;
    private String verificationId;
    private String name, email, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_signup_code);

        codeEditText = findViewById(R.id.edit_text_code);
        verifyButton = findViewById(R.id.button_verify);
        progressBar = new ProgressBar(this);

        // Get the verification ID and user details from the intent
        verificationId = getIntent().getStringExtra("verificationId");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");

        // Set a click listener for the verify button
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the code entered by the user
                String code = codeEditText.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    codeEditText.setError("Code is required.");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                // Verify the code
                verifyCode(code);
            }
        });
    }

    // Verify the code entered by the user
    private void verifyCode(String code) {
        // Create a PhoneAuthCredential object with the verification ID and code
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // Sign in with the credential
        signInWithPhoneAuthCredential(credential);
    }

    // Sign in with the phone authentication credential
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        // Get an instance of FirebaseAuth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Sign in with the credential
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                   // Check if the task is successful
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            // Save user details to Firestore
                            saveUserDetailsToFirestore(user.getUid(), phone);
                            // Update the UI with the user details
                            updateUI(user);
                        }
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(VerifySignupCodeActivity.this, "Invalid code.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VerifySignupCodeActivity.this, "Verification failed.", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    // Save the user details to Firestore
    private void saveUserDetailsToFirestore(String userId, String phone) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Create a map to store the user details
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", userId);
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("phone", phone);

        // Add the user details to the Firestore database
        db.collection("users").document(userId)
                .set(userMap)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User details successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    // Update the UI based on the user's authentication status
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(VerifySignupCodeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
