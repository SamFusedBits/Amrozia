package com.globalfashion.amrozia.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.globalfashion.amrozia.R;
// The FaqActivity class is responsible for displaying the FAQ screen
public class FaqActivity extends MoreActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call the superclass's onCreate method with the saved instance state and persistent state
        super.onCreate(savedInstanceState);
        setContentView(R.layout.faq);

        // Back button to go back to the previous activity
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}