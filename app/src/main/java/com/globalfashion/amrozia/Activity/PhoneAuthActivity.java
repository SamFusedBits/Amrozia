package com.globalfashion.amrozia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.globalfashion.amrozia.MainActivity;
import com.globalfashion.amrozia.R;
import com.globalfashion.amrozia.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;
// The PhoneAuthActivity class is responsible for authenticating the user using phone number verification(Not in use)
public class PhoneAuthActivity extends AppCompatActivity {
    private TextView btntextviewSendVerificationCode;
    private EditText editTextPhone, editTextCode;
    private Button buttonVerifyCode;
    private FirebaseAuth mAuth;
    private String verificationId;
    // Resend token for re-sending the verification code
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextCode = findViewById(R.id.editTextCode);
        btntextviewSendVerificationCode = findViewById(R.id.btntextviewSendVerificationCode);
        buttonVerifyCode = findViewById(R.id.btnVerifyCode);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Send verification code button click
        btntextviewSendVerificationCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the phone number from the input field
                String phoneNumber = editTextPhone.getText().toString().trim();

                // Check if the phone number is empty
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(PhoneAuthActivity.this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Add country code if not present
                if (!phoneNumber.startsWith("+91")) {
                    phoneNumber = "+91" + phoneNumber;
                }
                // Save the final phone number
                final String finalPhoneNumber = phoneNumber;

                // Check if the phone number is already registered
                checkPhoneNumberExists(finalPhoneNumber);
            }
        });

        // Verify code button click
        buttonVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the verification code from the input field
                String code = editTextCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(PhoneAuthActivity.this, "Enter verification code", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Verify the code
                verifyCode(code);
            }
        });

        // Back button click
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneAuthActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Check if the phone number is already registered in the Firestore database
    private void checkPhoneNumberExists(final String phoneNumber) {
        // Get all the users from the Firestore database
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Check if the phone number exists
                            boolean phoneExists = false;
                            // Iterate over the documents and check if the phone number exists
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String userPhone = document.getString("phone");
                                if (phoneNumber.equals(userPhone)) {
                                    phoneExists = true;
                                    break;
                                }
                            }

                            if (phoneExists) {
                                // Phone number registered, proceed with verification
                                Toast.makeText(PhoneAuthActivity.this, "Sending verification code.", Toast.LENGTH_SHORT).show();
                                ProgressBar progressBar = findViewById(R.id.progressBar);
                                progressBar.setVisibility(View.VISIBLE);
                                sendVerificationCode(phoneNumber);
                                finish();
                            } else {
                                // Phone number not registered, send to SignUpActivity
                                Toast.makeText(PhoneAuthActivity.this, "Phone number not registered.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(PhoneAuthActivity.this, SignupActivity.class);
                                intent.putExtra("phoneNumber", phoneNumber);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(PhoneAuthActivity.this, "Phone number not registered.", Toast.LENGTH_SHORT).show();
                            Log.e("PhoneAuth", "Error getting documents: ", task.getException());
                            Intent intent = new Intent(PhoneAuthActivity.this, SignupActivity.class);
                            intent.putExtra("phoneNumber", phoneNumber);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    // Send the verification code to the phone number
    private void sendVerificationCode(String phoneNumber) {
        // PhoneAuthOptions for the verification process
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        // Start the verification process
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Callbacks for the verification process
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            // Auto-retrieval or instant verification
            String code = credential.getSmsCode();
            // Set the code in the input field
            if (code != null) {
                editTextCode.setText(code);
                verifyCode(code);
            }
        }

        // Handle the verification failed case
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(PhoneAuthActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("PhoneAuth", "Verification failed", e);
        }

        // Handle the code sent case
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
            // Save the verification ID and resend token
            super.onCodeSent(s, token);
            verificationId = s;
            resendToken = token;
            Toast.makeText(PhoneAuthActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
        }
    };

    // Verify the code entered by the user
    private void verifyCode(String code) {
        // Verify the code entered by the user
            if (verificationId != null && code != null) {
                // Create a PhoneAuthCredential with the verification ID and code
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                signInWithPhoneAuthCredential(credential);
            } else {
                Toast.makeText(PhoneAuthActivity.this, "Verification ID or code is invalid", Toast.LENGTH_SHORT).show();
            }
    }

    // Sign in with the PhoneAuthCredential
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(PhoneAuthActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(PhoneAuthActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
