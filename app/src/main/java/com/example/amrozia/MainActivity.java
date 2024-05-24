package com.example.amrozia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewMashru;
    private RecyclerView recyclerViewStaple;
    private RecyclerView recyclerViewPremiumRayon;
    private RecyclerView recyclerViewRayon;
    private RecyclerView recyclerViewCotton;

    private DatabaseReference database;


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

        database = FirebaseDatabase.getInstance().getReference();

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
        // Fetch data from Firebase
        // Use limitToLast(8) to get only the last 8 items(most recent 8 items in each category)
        database.child(category).limitToLast(8).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Product> productList = new ArrayList<>();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    productList.add(product);
                }
                ProductAdapter productAdapter = new ProductAdapter(productList);
                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2)); // Changed to GridLayoutManager
                recyclerView.setAdapter(productAdapter);
                progressBar.setVisibility(View.GONE); // Hide the progress bar

                // Set the custom layout manager
                GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this,2) {
                    @Override
                    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
                        if (getChildCount() > 0) {
                            View firstChild = recycler.getViewForPosition(0);
                            measureChild(firstChild, widthSpec, heightSpec);
                            setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), firstChild.getMeasuredHeight() * state.getItemCount());
                        } else {
                            super.onMeasure(recycler, state, widthSpec, heightSpec);
                        }
                    }
                };
                recyclerView.setLayoutManager(layoutManager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }

        });
    }

}