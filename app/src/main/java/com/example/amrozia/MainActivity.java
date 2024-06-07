package com.example.amrozia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amrozia.Activity.MoreActivity;
import com.example.amrozia.Adapter.ProductAdapter;
import com.example.amrozia.Domain.ProductDomain;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMashru;
    private RecyclerView recyclerViewStaple;
    private RecyclerView recyclerViewPremiumRayon;
    private RecyclerView recyclerViewRayon;
    private RecyclerView recyclerViewCotton;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerViews
        recyclerViewMashru = findViewById(R.id.recyclerViewMashru);
        recyclerViewStaple = findViewById(R.id.recyclerViewStaple);
        recyclerViewPremiumRayon = findViewById(R.id.recyclerViewPremiumRayon);
        recyclerViewRayon = findViewById(R.id.recyclerViewRayon);
        recyclerViewCotton = findViewById(R.id.recyclerViewCotton);
        firestore = FirebaseFirestore.getInstance();

        //Initialize progress bars
        ProgressBar progressBarMashru = findViewById(R.id.progressBarMashru);
        ProgressBar progressBarStaple = findViewById(R.id.progressBarStaple);
        ProgressBar progressBarPremiumRayon = findViewById(R.id.progressBarPremiumRayon);
        ProgressBar progressBarRayon = findViewById(R.id.progressBarRayon);
        ProgressBar progressBarCotton = findViewById(R.id.progressBarCotton);

        // Fetch data for each category
        fetchDataAndDisplay("Mashru Silk Collection", recyclerViewMashru, progressBarMashru);
        fetchDataAndDisplay("Staple Cotton Collection", recyclerViewStaple, progressBarStaple);
        fetchDataAndDisplay("Premium Rayon Collection", recyclerViewPremiumRayon, progressBarPremiumRayon);
        fetchDataAndDisplay("Rayon Collection", recyclerViewRayon, progressBarRayon);
        fetchDataAndDisplay("Cotton Collection", recyclerViewCotton, progressBarCotton);

        // Set the onClickListeners for the cart buttons to go back to the home page
        LinearLayout cartButton = findViewById(R.id.cart_btn);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the cart activity
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        // Set the onClickListeners for the cart buttons to go back to the home page
        LinearLayout profileButton = findViewById(R.id.profile_btn);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the cart activity
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        // Set the onClickListeners for the cart buttons to go back to the home page
        LinearLayout moreButton = findViewById(R.id.more_btn);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the cart activity
                Intent intent = new Intent(MainActivity.this, MoreActivity.class);
                startActivity(intent);
            }
        });

        TextView mashruSeeAll = findViewById(R.id.mashru_silk_see_all);
        mashruSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("category", "Mashru Silk Collection");
                startActivity(intent);
            }
        });

        TextView stapleSeeAll = findViewById(R.id.staple_cotton_see_all);
        stapleSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("category", "Staple Cotton Collection");
                startActivity(intent);
            }
        });

        TextView premiumRayonSeeAll = findViewById(R.id.premium_rayon_see_all);
        premiumRayonSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("category", "Premium Rayon Collection");
                startActivity(intent);
            }
        });

        TextView rayonSeeAll = findViewById(R.id.rayon_see_all);
        rayonSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("category", "Rayon Collection");
                startActivity(intent);
            }
        });

        TextView cottonSeeAll = findViewById(R.id.cotton_see_all);
        cottonSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("category", "Cotton Collection");
                startActivity(intent);
            }
        });
    }

    private void fetchDataAndDisplay(String category, RecyclerView recyclerView, ProgressBar progressBar) {
    // Fetch data from Firestore
    firestore.collection("Categories").document(category).collection("products").limit(8)
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    List<ProductDomain> productList = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        ProductDomain product = document.toObject(ProductDomain.class);
                        productList.add(product);
                    }
                    ProductAdapter productAdapter = new ProductAdapter(this, productList, category);
                    recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                    recyclerView.setAdapter(productAdapter);
                    progressBar.setVisibility(View.GONE); // Hide the progress bar
                } else {
                    // Handle errors
                }
            });


}
    }
