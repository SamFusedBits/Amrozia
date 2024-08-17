package com.example.amrozia;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.amrozia.Activity.ManageOrdersActivity;
import com.example.amrozia.Domain.ProductDomain;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
    private static final int PICK_IMAGES_REQUEST = 1;
    private EditText idEditText, titleEditText, descriptionEditText, priceEditText, stockEditText;
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

        // Initialize views
        idEditText = findViewById(R.id.idEditText);
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        stockEditText = findViewById(R.id.stockEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        submitButton = findViewById(R.id.submitButton);
        selectImagesButton = findViewById(R.id.selectImagesButton);
        selectedImagesTextView = findViewById(R.id.selectedImagesTextView);
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("Products Images");

        // Set up the category spinner
        String[] categories = new String[]{
                "Mashru Silk Collection",
                "Staple Cotton Collection",
                "Premium Rayon Collection",
                "Rayon Collection",
                "Cotton Collection"
        };

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        categorySpinner.setAdapter(adapter);

        // Set click listeners for buttons to select images, submit product and send notification
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

        LinearLayout manageOrdersIcon = findViewById(R.id.manage_orders_icon);
        manageOrdersIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, ManageOrdersActivity.class);
                startActivity(intent);
            }
        });
    }

    // Open the image chooser to select images
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple image selection
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES_REQUEST);
    }

    // Handle the result of the image chooser
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) { // Multiple images selected
                int count = data.getClipData().getItemCount();
                imageUris = new ArrayList<>();
                // Add each selected image URI to the list
                for (int i = 0; i < count; i++) {
                    // Get the URI of the selected image
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    // Add the URI to the list
                    imageUris.add(imageUri);
                }
                // Update the text view to show the number of images selected
                selectedImagesTextView.setText(count + " images selected");
            } else if (data.getData() != null) { // Single image selected
                // Create a new list to hold the selected image URI
                imageUris = new ArrayList<>();
                // Add the URI of the selected image to the list
                imageUris.add(data.getData());
                selectedImagesTextView.setText("1 image selected");
            }
        }
    }

    // Upload the selected images to Firebase Storage and save the product details to Firestore
    private void uploadImagesAndSaveProduct() {
        // Check if images are selected
        if (imageUris != null && !imageUris.isEmpty()) {
            // Get the selected category
            String category = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "";

            if (category.isEmpty()) {
                Toast.makeText(this, "Category is not selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a list to hold the URLs of the uploaded images
            final ArrayList<String> uploadedImageUrls = new ArrayList<>();
            // Get the total number of images to upload
            final int totalImages = imageUris.size();

            // Get the product ID
            String productId = idEditText.getText().toString();
            if (productId.isEmpty()) {
                Toast.makeText(this, "Product ID is empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a StorageReference specific to the selected category and product ID
            StorageReference categoryStorageReference = storageReference.child(category).child(productId);

            // Upload each image to Firebase Storage
            for (Uri imageUri : imageUris) {
                // Create a unique reference to the image file in Firebase Storage
                final StorageReference fileReference = categoryStorageReference.child(System.currentTimeMillis() + ".jpg");
                fileReference.putFile(imageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            // Get the download URL of the uploaded image
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    // Add the URL to the list of uploaded image URLs
                                    public void onSuccess(Uri uri) {
                                        uploadedImageUrls.add(uri.toString());
                                        // Check if all images have been uploaded
                                        if (uploadedImageUrls.size() == totalImages) {
                                            // Save the product details to Firestore
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

    // Save the product details to Firestore
    private void saveProductToFirestore(ArrayList<String> imageUrls) {
        String id = idEditText.getText().toString();
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        double price;
        int stock;
        String category = categorySpinner.getSelectedItem() != null ? categorySpinner.getSelectedItem().toString() : "";


        try {
            price = Double.parseDouble(priceEditText.getText().toString());
        } catch (NumberFormatException e) {
            Log.e("AdminActivity", "Invalid price input", e);
            Toast.makeText(this, "Invalid price input", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            stock = Integer.parseInt(stockEditText.getText().toString());
        } catch (NumberFormatException e) {
            Log.e("AdminActivity", "Invalid stock input", e);
            Toast.makeText(this, "Invalid stock input", Toast.LENGTH_SHORT).show();
            return;
        }

        if (id.isEmpty()) {
            Log.e("AdminActivity", "Product ID is empty");
            return;
        }

        if (title.isEmpty()) {
            Log.e("AdminActivity", "Product title is empty");
            return;
        }

        if (description.isEmpty()) {
            Log.e("AdminActivity", "Product description is empty");
            return;
        }

        if (price <= 0) {
            Log.e("AdminActivity", "Product price is invalid");
            return;
        }

        if (category.isEmpty()) {
            Log.e("AdminActivity", "Category is empty");
            return;
        }

        if (imageUrls.isEmpty()) {
            Log.e("AdminActivity", "Image URLs are empty");
            return;
        }

        // Create a new ProductDomain object with the product details
        ProductDomain product = new ProductDomain(id, title, description, price, imageUrls, category, stock, 0);
        // Save the product to Firestore
        addProductToCategory(product, category);
    }

    // Add the product to the specified category in Firestore
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

        if (product.getTitle() == null || product.getTitle().isEmpty()) {
            Log.e("AdminActivity", "Product title is null or empty");
            return;
        }

        if (product.getId() == null || product.getId().isEmpty()) {
            Log.e("AdminActivity", "Product ID is null or empty");
            return;
        }

        // Get the product ID
        String id = product.getId();

        // Add the product to the specified category in Firestore
        firestore.collection("Categories").document(category).collection("products")
                .document(id)
                .set(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    // Show a success message if the product is added successfully
                    public void onSuccess(Void aVoid) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AdminActivity.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d("AdminActivity", "Product added with title: " + product.getId());
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
