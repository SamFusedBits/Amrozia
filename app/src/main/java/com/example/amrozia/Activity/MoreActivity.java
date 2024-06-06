package com.example.amrozia.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.amrozia.ContactUsActivity;
import com.example.amrozia.R;

public class MoreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        setupPageNavigation();
    }

    private void setupPageNavigation() {
        findViewById(R.id.nav_ship).setOnClickListener(v -> navigateTo(ShippingReturnsActivity.class));
        findViewById(R.id.nav_contact).setOnClickListener(v -> navigateTo(ContactUsActivity.class));
        findViewById(R.id.nav_blog).setOnClickListener(v -> navigateTo(BlogActivity.class));
        findViewById(R.id.nav_faq).setOnClickListener(v -> navigateTo(FaqActivity.class));
        findViewById(R.id.nav_about).setOnClickListener(v -> navigateTo(AboutUsActivity.class));
        findViewById(R.id.nav_gift).setOnClickListener(v -> navigateTo(GiftCardActivity.class));
    }

    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(MoreActivity.this, activityClass);
        startActivity(intent);
    }
}
