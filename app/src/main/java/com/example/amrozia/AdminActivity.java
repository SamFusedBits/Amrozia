package com.example.amrozia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.amrozia.Domain.ProductDomain;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private static final int PICK_IMAGES_REQUEST = 1;

    private EditText idEditText, titleEditText, descriptionEditText, priceEditText, sizeEditText;
    private Spinner categorySpinner;
    private Button submitButton, selectImagesButton;
    private TextView selectedImagesTextView;
    private List<Uri> imageUris;
    private FirebaseFirestore firestore;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        idEditText = findViewById(R.id.idEditText);
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        sizeEditText = findViewById(R.id.sizeEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        submitButton = findViewById(R.id.submitButton);
        selectImagesButton = findViewById(R.id.selectImagesButton);
        selectedImagesTextView = findViewById(R.id.selectedImagesTextView);
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("product_images");

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

        selectImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImagesAndSaveProduct();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple image selection
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) { // Multiple images selected
                int count = data.getClipData().getItemCount();
                imageUris = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                }
                selectedImagesTextView.setText(count + " images selected");
            } else if (data.getData() != null) { // Single image selected
                imageUris = new ArrayList<>();
                imageUris.add(data.getData());
                selectedImagesTextView.setText("1 image selected");
            }
        }
    }

    private void uploadImagesAndSaveProduct() {
        if (imageUris != null && !imageUris.isEmpty()) {
            final ArrayList<String> uploadedImageUrls = new ArrayList<>();
            final int totalImages = imageUris.size();

            for (Uri imageUri : imageUris) {
                final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        uploadedImageUrls.add(uri.toString());
                                        if (uploadedImageUrls.size() == totalImages) {
                                            saveProductToFirestore(uploadedImageUrls);
                                        }
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AdminActivity.this, "Failed to upload image!", Toast.LENGTH_SHORT).show();
                                Log.e("AdminActivity", "Error uploading image", e);
                            }
                        });
            }
        } else {
            Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProductToFirestore(ArrayList<String> imageUrls) {
        String id = idEditText.getText().toString();
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        double price = Double.parseDouble(priceEditText.getText().toString());
        ArrayList<String> size = new ArrayList<>(Arrays.asList(sizeEditText.getText().toString().split(",")));
        String category = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "";

        if (id.isEmpty()) {
            Log.e("AdminActivity", "Product ID is empty");
            return;
        }

        if (category.isEmpty()) {
            Log.e("AdminActivity", "Category is empty");
            return;
        }

        ProductDomain product = new ProductDomain(id, title, description, price, size, imageUrls, category);
        addProductToCategory(product, category);
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

        firestore.collection("Categories").document(category).collection("products")
                .document(product.getId())
                .set(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AdminActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d("AdminActivity", "Product added with ID: " + product.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AdminActivity.this, "Failed to add product!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.e("AdminActivity", "Error adding product", e);
                    }
                });
    }
}
