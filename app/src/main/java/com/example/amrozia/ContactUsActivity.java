package com.example.amrozia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.amrozia.Activity.MoreActivity;

public class ContactUsActivity extends MoreActivity {
    Button submit_button;

    // Override the onCreate method which is called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call the superclass's onCreate method with the saved instance state and persistent state
        super.onCreate(savedInstanceState);

        // Set the layout for this activity to be R.layout.contact_us
        setContentView(R.layout.contact_us);

        submit_button = findViewById(R.id.submit_button);
        submit_button.setOnClickListener(v -> {
            Toast.makeText(ContactUsActivity.this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
        });
    }
}