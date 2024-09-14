package com.globalfashion.amrozia;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.globalfashion.amrozia.Activity.MoreActivity;
import com.globalfashion.amrozia.Adapter.BannerAdapter;
import com.globalfashion.amrozia.Adapter.ProductAdapter;
import com.globalfashion.amrozia.Domain.ProductDomain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
// This activity displays the main screen of the app with different categories and a search bar
public class MainActivity extends AppCompatActivity {
    // Request code for speech input
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private RecyclerView recyclerViewMashru;
    private RecyclerView recyclerViewStaple;
    private RecyclerView recyclerViewPremiumRayon;
    private RecyclerView recyclerViewRayon;
    private RecyclerView recyclerViewCotton;
    private FirebaseFirestore firestore;
    private ViewPager2 saleBanner;
    private FirebaseAnalytics mFirebaseAnalytics;
    // Notification permission code for Android 12 and above
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
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

    // Handle back button press to close the app
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAffinity();
        } else {
            finishAffinity();
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
                    // Pass the search query to the SearchResultsActivity
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
                    // Pass the search query to the SearchResultsActivity
                    intent.putExtra("search_query", v.getText().toString());
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        requestNotificationPermission();
        setupFirebaseMessaging();

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

        saleBanner = findViewById(R.id.sale_banner);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference bannersRef = storage.getReference().child("Banners");

        // List all items in the Banners folder
        bannersRef.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    // List of banner image URLs
                    List<String> bannerUrls = new ArrayList<>();
                    // Loop through each item in the list
                    ListResult result = task.getResult();
                    for (StorageReference item : result.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Add the URL to the list
                                bannerUrls.add(uri.toString());
                                // If all URLs have been fetched, set the adapter for the ViewPager
                                if (bannerUrls.size() == result.getItems().size()) {
                                    // Set the adapter for the ViewPager
                                    BannerAdapter adapter = new BannerAdapter(MainActivity.this, bannerUrls);
                                    saleBanner.setAdapter(adapter);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Toast.makeText(MainActivity.this, "Error fetching banner images", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    // Handle any errors
                    Log.e("MainActivity", "Error fetching banner images", task.getException());
                }
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

        // Check if the device is running Android 12 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Request notification permission
            requestNotificationPermission();
        }
    }

    // Request notification permission for Android 12 and above
    private void requestNotificationPermission() {
        // Check if the device is running Android 12 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the notification permission is granted
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request the notification permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
            }
        }
    }

    // Setup Firebase Cloud Messaging
    private void setupFirebaseMessaging() {
        // Subscribe to the topic "all_users"
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
                // Add a listener to handle the completion of the task
                .addOnCompleteListener(task -> {
                    // Log the result of the task
                    String msg = task.isSuccessful() ? "Subscribed to all_users" : "Subscribe failed";
                    Log.d("MainActivity", msg);
                });
    }

    // Handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check the request code
        switch (requestCode) {
            case 1012: // Microphone permission request code
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, you can now send notifications
                    startVoiceInput();
                } else {
                    // Permission denied, show a message to the user
                    Toast.makeText(this, "Microphone permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case 123: // Notification permission request code
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity", "Notification permission granted");
                } else {
                    Log.e("MainActivity", "Notification permission denied");
                }
                break;
        }
    }

    // Voice input launcher for Android 12 and above
    private final ActivityResultLauncher<Intent> voiceInputLauncher = registerForActivityResult(
            // Register the activity result contract for starting the voice input activity
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Check if the result is OK and the data is not null
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    ArrayList<String> voiceResult = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (voiceResult != null && !voiceResult.isEmpty()) {
                        // Get the search query from the voice input
                        String searchQuery = voiceResult.get(0);
                        // Set the search query in the search bar
                        searchBar.setText(searchQuery);

                        // Directly start the search activity
                        Intent intent = new Intent(MainActivity.this, SearchResultsActivity.class);
                        intent.putExtra("search_query", searchQuery);
                        startActivity(intent);
                    }
                }
            }
    );

    // Start voice input method
    private void startVoiceInput() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // Request the microphone permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1012);
        } else {
            // Start the voice input activity
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            // Set the language model and language for the voice input activity intent to the default locale
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            // Set the prompt message for the voice input activity
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
            try {
                // Launch the voice input activity
                voiceInputLauncher.launch(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Speech recognition is not supported on this device.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Start voice input method for older versions of Android
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check if the request code is for voice input
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            // Get the voice input
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            // Check if the result is not empty
            if (result != null && !result.isEmpty()) {
                // Set the search query in the search bar
                searchBar.setText(result.get(0));
            }
        }
    }

    // Fetch data from Firestore and display it in the RecyclerView
    private void fetchDataAndDisplay(String category, RecyclerView recyclerView, ProgressBar progressBar) {
        // Fetch data from Firestore
        firestore.collection("Categories").document(category).collection("products").limit(8)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Create a list to store the products
                        List<ProductDomain> productList = new ArrayList<>();
                        // Loop through the documents and add the products to the list
                        for (DocumentSnapshot document : task.getResult()) {
                            // Convert the document to a ProductDomain object
                            ProductDomain product = document.toObject(ProductDomain.class);
                            if (product != null && product.getStock() > 0) { // Check if the quantity is greater than 0
                                productList.add(product);
                            }
                        }
                        // Create a ProductAdapter and set it to the RecyclerView to display the products
                        ProductAdapter productAdapter = new ProductAdapter(this, productList, category);
                        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                        recyclerView.setAdapter(productAdapter);
                        progressBar.setVisibility(View.GONE); // Hide the progress bar
                    } else {
                        progressBar.setVisibility(View.GONE); // Hide the progress bar
                        Log.e("MainActivity", "Error getting documents: ", task.getException());
                    }
                });
    }
}