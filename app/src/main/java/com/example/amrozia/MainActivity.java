package com.example.amrozia;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.amrozia.Activity.MoreActivity;
import com.example.amrozia.Activity.SalesProductActivity;
import com.example.amrozia.Adapter.ProductAdapter;
import com.example.amrozia.Domain.ProductDomain;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private RecyclerView recyclerViewMashru;
    private RecyclerView recyclerViewStaple;
    private RecyclerView recyclerViewPremiumRayon;
    private RecyclerView recyclerViewRayon;
    private RecyclerView recyclerViewCotton;
    private FirebaseFirestore firestore;
    private ViewPager2 saleBanner;
    private FirebaseAnalytics mFirebaseAnalytics;
    // Notification channel ID
    private static final String CHANNEL_ID = "PRODUCT_NOTIFICATION_CHANNEL";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private EditText searchBar;
    private ImageView microphone;

    // Check if the user is logged in, if not redirect to the login page
    @Override
    protected void onStart() {
        super.onStart();
        checkAuthenticationState();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    // Check if the user is logged in
    private void checkAuthenticationState() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            // User is not logged in, redirect to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Search bar
        searchBar = findViewById(R.id.search_bar);

        // Set the onEditorActionListener for the search bar
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Start SearchResultsActivity with the search query
                    Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                    intent.putExtra("search_query", v.getText().toString());
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        // Microphone button
        ImageView microphone = findViewById(R.id.microphone);

        // Microphone button click listener
        microphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        // Set the onEditorActionListener for the search bar
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Start SearchResultsActivity with the search query
                    Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                    intent.putExtra("search_query", v.getText().toString());
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        // Initialize Firebase
        FirebaseApp.initializeApp(this);


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

        // Banner Image Slider
        saleBanner = findViewById(R.id.sale_banner);

        saleBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SalesProductActivity.class);
                startActivity(intent);
            }
        });

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

        // Set the onClickListeners for the cart buttons to go back to the home page
        TextView mashruSeeAll = findViewById(R.id.mashru_silk_see_all);
        mashruSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("category", "Mashru Silk Collection");
                startActivity(intent);
            }
        });

        // Set the onClickListeners for the cart buttons to go back to the home page
        TextView stapleSeeAll = findViewById(R.id.staple_cotton_see_all);
        stapleSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("category", "Staple Cotton Collection");
                startActivity(intent);
            }
        });

        // Set the onClickListeners for the cart buttons to go back to the home page
        TextView premiumRayonSeeAll = findViewById(R.id.premium_rayon_see_all);
        premiumRayonSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("category", "Premium Rayon Collection");
                startActivity(intent);
            }
        });

        // Set the onClickListeners for the cart buttons to go back to the home page
        TextView rayonSeeAll = findViewById(R.id.rayon_see_all);
        rayonSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("category", "Rayon Collection");
                startActivity(intent);
            }
        });

        // Set the onClickListeners for the cart buttons to go back to the home page
        TextView cottonSeeAll = findViewById(R.id.cotton_see_all);
        cottonSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                intent.putExtra("category", "Cotton Collection");
                startActivity(intent);
            }
        });


        // Request notification permission if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        }
    }

    // Voice input launcher
    private final ActivityResultLauncher<Intent> voiceInputLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    ArrayList<String> voiceResult = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (voiceResult != null && !voiceResult.isEmpty()) {
                        String searchQuery = voiceResult.get(0);
                        searchBar.setText(searchQuery);

                        // Directly start the search activity
                        Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                        intent.putExtra("search_query", searchQuery);
                        startActivity(intent);
                    }
                }
            }
    );

    // Start voice input
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
        try {
            voiceInputLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Speech recognition is not supported on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    // Start voice input
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if the request code is for voice input
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            // Get the voice input
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // Check if the result is not empty
            if (result != null && !result.isEmpty()) {
                searchBar.setText(result.get(0));
            }
        }
    }

    // Fetch data from Firestore and display it in the RecyclerView
    private void fetchDataAndDisplay(String category, RecyclerView recyclerView, ProgressBar progressBar) {
        // Fetch data from Firestore
        firestore.collection("Categories").document(category).collection("products").limit(4)
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
                        ProductAdapter productAdapter = new ProductAdapter(this, productList, category);
                        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                        recyclerView.setAdapter(productAdapter);
                        progressBar.setVisibility(View.GONE); // Hide the progress bar
                    } else {
                        progressBar.setVisibility(View.GONE); // Hide the progress bar
                        Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}