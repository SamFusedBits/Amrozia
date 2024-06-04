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

import com.example.amrozia.Domain.ProductDomain;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;

public class AdminActivity extends AppCompatActivity {
    private EditText idEditText, titleEditText, descriptionEditText, priceEditText, sizeEditText, picUrlEditText;
    private Spinner categorySpinner;
    private Button submitButton;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        idEditText = findViewById(R.id.idEditText);
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        picUrlEditText = findViewById(R.id.picUrlEditText);
        sizeEditText = findViewById(R.id.sizeEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        submitButton = findViewById(R.id.submitButton);
        firestore = FirebaseFirestore.getInstance();

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
                String id = idEditText.getText().toString();
                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                double price = Double.parseDouble(priceEditText.getText().toString());
                ArrayList<String> size = new ArrayList<>(Arrays.asList(sizeEditText.getText().toString().split(",")));
                ArrayList<String> picUrl = new ArrayList<>(Arrays.asList(picUrlEditText.getText().toString().split(",")));
                String category = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "";

                if (id.isEmpty()) {
                    Log.e("AdminActivity", "Product ID is empty");
                    return;
                }

                if (category.isEmpty()) {
                    Log.e("AdminActivity", "Category is empty");
                    return;
                }

                ProductDomain product = new ProductDomain(id, title, description, price, size, picUrl, category);
                addProductToCategory(product, category);
            }
        });
    }

    public void addProductToCategory(ProductDomain product, String category) {
        if (firestore == null) {
            Log.e("AdminActivity", "Firestore is null");
            return;
        }

        if (category == null || category.isEmpty()) {
            Log.e("AdminActivity", "Category is null or empty");
            return;
        }

        if (product == null) {
            Log.e("AdminActivity", "Product is null");
            return;
        }

        if (product.getId() == null || product.getId().isEmpty()) {
            Log.e("AdminActivity", "Product ID is null or empty");
            return;
        }

        // If all checks pass, proceed with Firestore operation
        firestore.collection("Categories").document(category).collection("products")
                .document(product.getId())  // Use the inputted ID to create the document
                .set(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AdminActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                        Log.d("AdminActivity", "Product added with ID: " + product.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminActivity.this, "Failed to add product!", Toast.LENGTH_SHORT).show();
                        Log.e("AdminActivity", "Error adding product", e);
                    }
                });
    }
}
