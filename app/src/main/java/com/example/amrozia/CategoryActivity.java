package com.example.amrozia;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amrozia.Adapter.ProductAdapter;
import com.example.amrozia.Domain.ProductDomain;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerView = findViewById(R.id.recyclerView);
        firestore = FirebaseFirestore.getInstance();

        // Get the category from the Intent
        String category = getIntent().getStringExtra("category");

        // Set the ActionBar title to the category name
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        progressBar = findViewById(R.id.progressBar);
        toolbarTitle.setText(category);

        // Handle the back button click
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Fetch data for the category
        fetchDataAndDisplay(category);
    }

    private void fetchDataAndDisplay(String category) {
        // Fetch data from Firestore
        firestore.collection("Categories").document(category).collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ProductDomain> productList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            ProductDomain product = document.toObject(ProductDomain.class);
                            if (product != null && product.getStock() > 0) { // Check if the quantity is greater than 0
                                productList.add(product);
                            }
                        }
                        ProductAdapter productAdapter = new ProductAdapter(this,productList,category);
                        recyclerView.setLayoutManager(new GridLayoutManager(CategoryActivity.this, 2));
                        recyclerView.setAdapter(productAdapter);
                        progressBar.setVisibility(View.GONE);
                    } else {
                        // Handle the case where the task is not successful
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CategoryActivity.this, "Error fetching data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
