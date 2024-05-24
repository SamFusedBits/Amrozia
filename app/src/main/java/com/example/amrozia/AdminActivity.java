package com.example.amrozia;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {
    private EditText nameEditText, descriptionEditText, priceEditText, imageUrlEditText;
    private Spinner categorySpinner;
    private Button submitButton;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        nameEditText = findViewById(R.id.nameEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        imageUrlEditText = findViewById(R.id.imageUrlEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        submitButton = findViewById(R.id.submitButton);

        database = FirebaseDatabase.getInstance().getReference();

        String[] categories = new String[]{
            "Mashru Silk Collection",
            "Staple Cotton Collection",
            "Premium Rayon Collection",
            "Rayon Collection",
            "Cotton Collection"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                double price = Double.parseDouble(priceEditText.getText().toString());
                String imageUrl = imageUrlEditText.getText().toString();
                String category = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "";

                if (category.isEmpty()) {
                    // Show an error message to the user
                    Log.e("AdminActivity", "Category is empty");
                    return;
                }

                Product product = new Product(name, description, price, imageUrl, category);
                addProduct(product);
            }
        });
    }
    public void addProduct(Product product) {
        Log.d("AdminActivity", "Adding product: " + product.getCategory());

        // Create a new unique key for the product
        String key = database.child(product.getCategory()).push().getKey();

        // Check if the key was generated successfully
        Log.d("AdminActivity", "Generated key: " + key);

        if(key == null) {
            // Show an error message to the user
            Log.e("AdminActivity", "Failed to generate unique key for product");
            return;
        }
        // Use the unique key to store the product under the category
        database.child(product.getCategory()).child(key).setValue(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AdminActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Show an error message to the user
                        Toast.makeText(AdminActivity.this, "Failed to add product!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}