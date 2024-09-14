package com.globalfashion.amrozia.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.globalfashion.amrozia.R;
// The BlogActivity class is responsible for displaying the blog screen
public class BlogActivity extends MoreActivity {

    // Override the onCreate method which is called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call the superclass's onCreate method with the saved instance state and persistent state
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blog);

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