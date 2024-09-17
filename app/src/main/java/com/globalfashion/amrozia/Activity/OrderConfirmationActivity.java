package com.globalfashion.amrozia.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.globalfashion.amrozia.MainActivity;
import com.globalfashion.amrozia.R;
// The OrderConfirmationActivity class is responsible for displaying the order confirmation screen when the user places an order
public class OrderConfirmationActivity extends AppCompatActivity {

    // Declare the views for the order confirmation activity
    private LottieAnimationView paymentStatusSuccess;
    private TextView orderPlacedMessage;
    private LottieAnimationView paymentStatusFailed;
    private TextView orderFailedMessage;
    private TextView continueShoppingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        // Initialize views
        paymentStatusSuccess = findViewById(R.id.paymentStatusSuccess);
        orderPlacedMessage = findViewById(R.id.orderPlacedMessage);
        paymentStatusFailed = findViewById(R.id.paymentStatusFailed);
        orderFailedMessage = findViewById(R.id.orderFailedMessage);
        continueShoppingBtn = findViewById(R.id.continueShoppingBtn);

        // Get the intent and check the status
        Intent intent = getIntent();
        String status = intent.getStringExtra("status");

        // Show the appropriate UI based on the status
        if ("success".equals(status)) {
            showSuccessUI();
        } else if ("failure".equals(status)) {
            showFailureUI();
        }

        // Set up the back button
        findViewById(R.id.continueShoppingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderConfirmationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Show the success UI if the payment was successful
    private void showSuccessUI() {
        paymentStatusSuccess.setVisibility(View.VISIBLE);
        orderPlacedMessage.setVisibility(View.VISIBLE);
        paymentStatusFailed.setVisibility(View.GONE);
        orderFailedMessage.setVisibility(View.GONE);
    }

    // Show the failure UI if the payment failed
    private void showFailureUI() {
        paymentStatusSuccess.setVisibility(View.GONE);
        orderPlacedMessage.setVisibility(View.GONE);
        paymentStatusFailed.setVisibility(View.VISIBLE);
        orderFailedMessage.setVisibility(View.VISIBLE);
    }
}
