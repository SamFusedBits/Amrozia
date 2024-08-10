package com.example.amrozia;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amrozia.Adapter.ProductAdapter;
import com.example.amrozia.Domain.ProductDomain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewSearchResults;
    private ProgressBar progressBarSearch;
    private TextView NoProductsFound;
    private ProductAdapter productAdapter;
    private List<ProductDomain> allProducts = new ArrayList<>();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Initialize RecyclerView and ProgressBar
        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);
        progressBarSearch = findViewById(R.id.progressBarSearchResults);
        NoProductsFound = findViewById(R.id.textViewNoProductsFound);
        backBtn = findViewById(R.id.backBtn);

        // Initialize RecyclerView for search results
        recyclerViewSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, new ArrayList<>(), "Search Results");
        recyclerViewSearchResults.setAdapter(productAdapter);

        // Get the search query from the intent
        String query = getIntent().getStringExtra("search_query");
        Log.d("SearchResultsActivity", "Search query: " + query);

        // Perform search with the query
        checkProductPresence(query);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void checkProductPresence(String searchedTitle) {
        progressBarSearch.setVisibility(View.VISIBLE);
        String searchLowerCase = searchedTitle.toLowerCase();  // Convert search title to lowercase for case-insensitive search

        // Split the search query into individual words
        List<String> searchKeywords = Arrays.asList(searchLowerCase.split(" "));

        // List to hold tasks for querying each category's products
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        // Define the categories to search
        List<String> categories = Arrays.asList(
                "Cotton Collection",
                "Mashru Silk Collection",
                "Premium Rayon Collection",
                "Rayon Collection",
                "Staple Cotton Collection"
        );

        // Iterate over each category
        for (String category : categories) {
            CollectionReference productsRef = firestore.collection("Categories").document(category).collection("products");

            // Log category being queried
            Log.d("SearchResultsActivity", "Querying category: " + category);

            // Query the products subcollection within each category
            tasks.add(productsRef.get());
        }

        // When all product queries are complete, process the results
        Task<List<QuerySnapshot>> allTasks = com.google.android.gms.tasks.Tasks.whenAllSuccess(tasks);
        allTasks.addOnCompleteListener(new OnCompleteListener<List<QuerySnapshot>>() {
            @Override
            public void onComplete(@NonNull Task<List<QuerySnapshot>> task) {
                if (task.isSuccessful()) {
                    boolean productFound = false;

                    // Clear the list of all products before adding new results
                    allProducts.clear();

                    for (QuerySnapshot querySnapshot : task.getResult()) {
                        for (DocumentSnapshot productDoc : querySnapshot) {
                            if (productDoc.exists()) {
                                ProductDomain product = productDoc.toObject(ProductDomain.class);
                                if (product != null) {
                                    for (String keyword : searchKeywords) {
                                        if (product.getTitle().toLowerCase().contains(keyword)) {
                                            // Log product found
                                            Log.d("SearchResultsActivity", "Product found: " + product.getTitle());
                                            productFound = true;
                                            // Add the found product to the list
                                            allProducts.add(product);
                                            break;  // Break the loop once a match is found
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (productFound) {
                        Log.d("SearchResultsActivity", "Products found: " + allProducts.size());
                        // Update the adapter with the new product list
                        productAdapter = new ProductAdapter(SearchResultsActivity.this, allProducts, "Search Results");
                        recyclerViewSearchResults.setAdapter(productAdapter);
                    } else {
                        progressBarSearch.setVisibility(View.GONE);
                        NoProductsFound.setVisibility(View.VISIBLE);
                    }

                    // Update the adapter with the new product list
                    productAdapter.notifyDataSetChanged();
                    progressBarSearch.setVisibility(View.GONE);
                } else {
                    progressBarSearch.setVisibility(View.GONE);
                    Toast.makeText(SearchResultsActivity.this, "Connectivity Error! Please try again after some time.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}