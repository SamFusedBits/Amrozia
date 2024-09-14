package com.globalfashion.amrozia;

import static com.globalfashion.amrozia.BuildConfig.BUSINESS_EMAIL;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.globalfashion.amrozia.Activity.MoreActivity;
import com.globalfashion.amrozia.Helper.SendinblueHelper;
// This activity allows users to contact the business by filling out a form
public class ContactUsActivity extends MoreActivity {
    private EditText nameEditText, emailEditText, phoneEditText, subjectEditText, messageEditText;
    private Button submitButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call the superclass's onCreate method with the saved instance state and persistent state
        super.onCreate(savedInstanceState);

        // Set the layout for this activity to be R.layout.contact_us
        setContentView(R.layout.contact_us);

        // Initialize views
        nameEditText = findViewById(R.id.editText_name);
        emailEditText = findViewById(R.id.editText_email);
        phoneEditText = findViewById(R.id.editText_phone);
        subjectEditText = findViewById(R.id.editText_subject);
        messageEditText = findViewById(R.id.editText_message);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendContactForm();
            }
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Method to send the contact form data
    private void sendContactForm() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String subject = subjectEditText.getText().toString().trim();
        String message = messageEditText.getText().toString().trim();

        // Validate inputs (add your own validation logic here)
        if (name.isEmpty() || email.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate phone (if provided)
        if (!phone.isEmpty() && !isValidPhoneNumber(phone)) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare email content for the contact form submission
        String emailSubject = "New Contact Form Submission: " + subject;
        String emailContent = "<html><body>" +
                "<h2>New Contact Form Submission</h2>" +
                "<p><strong>Name:</strong> " + name + "</p>" +
                "<p><strong>Email:</strong> " + email + "</p>" +
                "<p><strong>Phone:</strong> " + phone + "</p>" +
                "<p><strong>Subject:</strong> " + subject + "</p>" +
                "<p><strong>Message:</strong> " + message + "</p>" +
                "</body></html>";

        // Send email using SendinblueHelper
        SendinblueHelper.sendOrderNotification(
                BUSINESS_EMAIL,
                emailSubject,
                emailContent,
                this,
                new SendinblueHelper.EmailCallback() {
                    @Override
                    public void onResult(boolean success, String message) {
                        if (success) {
                            Toast.makeText(ContactUsActivity.this, "Form submitted successfully", Toast.LENGTH_SHORT).show();
                            // Clear the form details after successful submission
                            clearForm();
                        } else {
                            Toast.makeText(ContactUsActivity.this, "Failed to submit form: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void clearForm() {
        nameEditText.setText("");
        emailEditText.setText("");
        phoneEditText.setText("");
        subjectEditText.setText("");
        messageEditText.setText("");
    }

    // Email validation method
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    // Phone number validation method
    private boolean isValidPhoneNumber(String phone) {
        // This is a basic validation for a 10-digit phone number
        // You may want to adjust this based on your specific requirements
        String phonePattern = "^[0-9]{10}$";
        return phone.matches(phonePattern);
    }
}