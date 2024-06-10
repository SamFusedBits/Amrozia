package com.example.amrozia.Activity;

import android.os.Bundle;

import com.example.amrozia.R;

public class FaqActivity extends MoreActivity {

    // Override the onCreate method which is called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call the superclass's onCreate method with the saved instance state and persistent state
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq);
    }
}