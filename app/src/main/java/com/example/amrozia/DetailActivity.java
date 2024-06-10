package com.example.amrozia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.amrozia.Adapter.SizeAdapter;
import com.example.amrozia.Adapter.ViewPagerAdapter;
import com.example.amrozia.Domain.ItemsDomain;
import com.example.amrozia.Domain.ProductDomain;
import com.example.amrozia.Fragment.DescriptionFragment;
import com.google.android.datatransport.backend.cct.BuildConfig;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private List<String> picUrl; // This should be your list of image URLs
    private List<String> sizeTxt;
    private String description;


    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        firestore = FirebaseFirestore.getInstance();

        // Get the Product ID from the Intent
        String productId = getIntent().getStringExtra("productId");
        String category = getIntent().getStringExtra("category");

        // Set a default adapter and layout manager for the RecyclerView Size
        RecyclerView sizeRecyclerView = findViewById(R.id.sizeRecyclerView);
        sizeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        sizeRecyclerView.setAdapter(new SizeAdapter(new ArrayList<>()));

        // Get references to the views in activity_detail
        ViewPager2 viewpageSlider = findViewById(R.id.viewpageSlider);
        TextView titleTxt = findViewById(R.id.titleTxt);
        TextView priceTxt = findViewById(R.id.priceTxt);
        TextView sizeTxt = findViewById(R.id.sizeTxt);

    // Fetch the Product from Firestore based on its ID
    firestore.collection("Categories").document(category).collection("products").document(productId)
        .get()
        .addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Product found, populate the views with its data
                ProductDomain product = documentSnapshot.toObject(ProductDomain.class);
                titleTxt.setText(product.getTitle());
                priceTxt.setText(String.format("₹%s", product.getPrice()));
                description = product.getDescription(); // Fetch the description here

                // Set up the ViewPager for the product images
                if (product.getPicUrl() != null) {
                    picUrl = product.getPicUrl(); // Populate picUrl with the product's image URLs
                    List<String> picUrls = new ArrayList<>();
                    picUrls.addAll(product.getPicUrl());
                    ViewPagerAdapter adapter = new ViewPagerAdapter(picUrls);
                    viewpageSlider.setAdapter(adapter);
                    viewpageSlider.getAdapter().notifyDataSetChanged();
                } else {
                    ViewPagerAdapter adapter = new ViewPagerAdapter(new ArrayList<>());
                    viewpageSlider.setAdapter(adapter);
                    viewpageSlider.getAdapter().notifyDataSetChanged();
                }

                // Set up the RecyclerView for the sizes
                if (product.getSize() != null) {
                    SizeAdapter sizeAdapter = new SizeAdapter(product.getSize());
                    sizeRecyclerView.setAdapter(sizeAdapter);
                }
                // Create a new instance of DescriptionFragment
                DescriptionFragment descriptionFragment = new DescriptionFragment();

                // Create a new Bundle
                Bundle bundle = new Bundle();

                // Put the product's description into the Bundle
                bundle.putString("description", product.getDescription());

                // Set the Bundle as the arguments for the DescriptionFragment
                descriptionFragment.setArguments(bundle);

                // Use the FragmentManager to begin a transaction, replace the fragment_container with the fragment, and commit the transaction
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, descriptionFragment)
                        .commit();
            } else {
                // Product not found, handle
                // error
                Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(e -> {
            // Error fetching data, handle the error
            Toast.makeText(this, "Failed to fetch product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

        // Handle back button click
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Handle share button click
        ImageView shareBtn = findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share App");
                String shareMessage = "Check out this app: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID; //Replace "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID with the actual URL of your app on the Google Play Store.
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share via"));
            }
        });

        // Initialize picUrl as an empty list
        picUrl = new ArrayList<>();
        // Handle add to cart button click
        AppCompatButton addToCartBtn = findViewById(R.id.addTocartBtn);
        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, CartActivity.class);


                // Create an ItemsDomain object with the product details
                ArrayList<String> picUrlList = new ArrayList<>(picUrl);


                // Remove the "₹" symbol before parsing the price to a double
                String priceStr = priceTxt.getText().toString().replace("₹", "").trim();
                double price = Double.parseDouble(priceStr);

                // Get the selected size from the SizeAdapter
                SizeAdapter sizeAdapter = (SizeAdapter) sizeRecyclerView.getAdapter();
                String selectedSize = sizeAdapter.getSelectedSize();

                ItemsDomain item = new ItemsDomain(productId, titleTxt.getText().toString(), description, picUrlList, price, category, selectedSize);


                    // Put the ItemsDomain object into the intent
                    intent.putExtra("item", item);

                    startActivity(intent);

            }
        });
    }
}
