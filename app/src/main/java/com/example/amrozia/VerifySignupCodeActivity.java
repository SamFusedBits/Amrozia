package com.example.amrozia;

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

public class VerifySignupCodeActivity extends AppCompatActivity {
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

        verificationId = getIntent().getStringExtra("verificationId");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = codeEditText.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    codeEditText.setError("Code is required.");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = task.getResult().getUser();
                        if (user != null) {
                            // Save user details to Firestore
                            saveUserDetailsToFirestore(user.getUid(), phone);
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

    private void saveUserDetailsToFirestore(String userId, String phone) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", userId);
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("phone", phone);

        db.collection("users").document(userId)
                .set(userMap)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User details successfully written!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(VerifySignupCodeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
