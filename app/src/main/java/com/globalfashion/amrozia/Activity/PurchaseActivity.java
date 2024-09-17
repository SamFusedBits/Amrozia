package com.globalfashion.amrozia.Activity;

import static com.globalfashion.amrozia.BuildConfig.BUSINESS_EMAIL;
import static com.globalfashion.amrozia.BuildConfig.CLIENT_ID;
import static com.globalfashion.amrozia.BuildConfig.CLIENT_SECRET;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cashfree.pg.api.CFPaymentGatewayService;
import com.cashfree.pg.core.api.CFSession;
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback;
import com.cashfree.pg.core.api.exception.CFException;
import com.cashfree.pg.core.api.utils.CFErrorResponse;
import com.cashfree.pg.ui.api.upi.intent.CFIntentTheme;
import com.cashfree.pg.ui.api.upi.intent.CFUPIIntentCheckout;
import com.cashfree.pg.ui.api.upi.intent.CFUPIIntentCheckoutPayment;
import com.globalfashion.amrozia.Adapter.PurchaseProductAdapter;
import com.globalfashion.amrozia.CartActivity;
import com.globalfashion.amrozia.Domain.ProductDomain;
import com.globalfashion.amrozia.Helper.ManagementCart;
import com.globalfashion.amrozia.Helper.SendinblueHelper;
import com.globalfashion.amrozia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
// This activity allows users to purchase products from the cart
public class PurchaseActivity extends AppCompatActivity implements CFCheckoutResponseCallback {
    private EditText editTextName, editTextEmail, editTextAddress, editTextPhone;
    private String paymentSessionID;
    private String orderID;
    private Button buttonSubmit;
    private List<ProductDomain> productList;
    private FirebaseFirestore db;
    private CFSession.Environment cfEnvironment = CFSession.Environment.PRODUCTION; // Change to CFSession.Environment.PRODUCTION for live;
    private double totalCost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPhone = findViewById(R.id.editTextPhone);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        TextView textViewTotal = findViewById(R.id.textViewTotal);


        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to the CartActivity
                Intent intent = new Intent(PurchaseActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        // Get the passed product names, prices and images
        Intent intent = getIntent();
        ArrayList<String> productNames = intent.getStringArrayListExtra("productNames");
        ArrayList<String> productPrices = intent.getStringArrayListExtra("productPrices");
        ArrayList<String> productImages = intent.getStringArrayListExtra("productImages");
        ArrayList<String> productIds = intent.getStringArrayListExtra("productIds");
        ArrayList<String> productCategories = intent.getStringArrayListExtra("productCategories");
        ArrayList<Integer> productQuantities = intent.getIntegerArrayListExtra("productQuantities");
        totalCost = intent.getDoubleExtra("totalCost", 0.0);
        Log.d("PurchaseActivity", "Product Quantities: " + productQuantities);

        // Set the total cost in the TextView
        textViewTotal.setText("Total: ₹" + totalCost);

        orderID = generateOrderID(); // Implement this method to generate a unique order ID

        // Create a list of products
        productList = new ArrayList<>();
        for (int i = 0; i < productNames.size(); i++) {
            String name = productNames.get(i);
            String price = productPrices.get(i);
            ArrayList<String> image = new ArrayList<>();
            image.add(productImages.get(i));
            String id = productIds.get(i);
            String category = productCategories.get(i);
            int quantity = productQuantities.get(i);

            // Add the product to the list
            productList.add(new ProductDomain(id, name, "", Double.parseDouble(price), image, category, 0, quantity));
        }

        // Initialize the RecyclerView
        RecyclerView recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        PurchaseProductAdapter productAdapter = new PurchaseProductAdapter(this, productList);
        recyclerViewProducts.setAdapter(productAdapter);

        // Set click listener for the submit button
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    createCashfreeOrder();
                } else {
                    // Optionally, you can add a message here to inform the user that input validation failed
                    Toast.makeText(PurchaseActivity.this, "Please fill all the required details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        try {
            // Set the checkout callback for Cashfree Payment Gateway Service instance
            CFPaymentGatewayService.getInstance().setCheckoutCallback(this);
        } catch (CFException e) {
            // Handle exception
            e.printStackTrace();
        }
    }

    // Validate user input
    private boolean validateInput() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        if (name.isEmpty()) {
            editTextName.setError("Name is required");
            editTextName.requestFocus();
            return false;
        }

        if (name.length() < 2) {
            editTextName.setError("Name must be at least 2 characters long");
            editTextName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address");
            editTextEmail.requestFocus();
            return false;
        }

        if (address.isEmpty()) {
            editTextAddress.setError("Address is required");
            editTextAddress.requestFocus();
            return false;
        }

        if (address.length() < 5) {
            editTextAddress.setError("Please enter a complete address");
            editTextAddress.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            editTextPhone.setError("Phone number is required");
            editTextPhone.requestFocus();
            return false;
        }

        if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            editTextPhone.setError("Please enter a valid phone number");
            editTextPhone.requestFocus();
            return false;
        }

        return true;
    }

    // Create a Cashfree order to initiate the payment process
    private void createCashfreeOrder() {
        String apiUrl = "https://api.cashfree.com/pg/orders"; // Use the production URL for live environment
        String clientId = CLIENT_ID;
        String clientSecret = CLIENT_SECRET;

        // Create the order request JSON object with the required fields
        JSONObject orderRequest = new JSONObject();
        try {
            orderRequest.put("order_id", orderID);
            orderRequest.put("order_amount", totalCost);
            orderRequest.put("order_currency", "INR");
            orderRequest.put("customer_details", new JSONObject()
                    .put("customer_id", "customer_" + System.currentTimeMillis())
                    .put("customer_email", editTextEmail.getText().toString())
                    .put("customer_phone", editTextPhone.getText().toString()));
            // Add other required fields as per Cashfree's API documentation
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a new JSON object request to send the order request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, apiUrl, orderRequest,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Get the payment session ID from the response JSON object
                            paymentSessionID = response.getString("payment_session_id");
                            // Now that we have the payment session ID, we can initiate the payment
                            initiatePayment();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(PurchaseActivity.this, "Network Error. Please try again!", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                // Set the headers for the request
                Map<String, String> headers = new HashMap<>();
                headers.put("x-client-id", clientId);
                headers.put("x-client-secret", clientSecret);
                headers.put("Content-Type", "application/json");
                headers.put("x-api-version", "2023-08-01");
                return headers;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest); // Send the request to create the order
    }

    // Initiate the payment process using Cashfree's SDK
    private void initiatePayment() {
        try {
            // Create customer details
            Map<String, Object> customerDetails = new HashMap<>();
            customerDetails.put("order_id", orderID);
            customerDetails.put("order_amount", totalCost);
            customerDetails.put("customer_email", editTextEmail.getText().toString());
            customerDetails.put("customer_phone", editTextPhone.getText().toString());
            customerDetails.put("customer_name", editTextName.getText().toString());

            // Create cart details
            List<Map<String, Object>> cartItems = new ArrayList<>();
            // Add each product to the cart
            for (ProductDomain product : productList) {
                // Create a map for each item
                Map<String, Object> item = new HashMap<>();
                item.put("item_id", product.getId());
                item.put("item_name", product.getName());
                item.put("item_price", product.getPrice());
                item.put("item_quantity", product.getQuantity());
                cartItems.add(item);
            }

            // Create a map for the cart details with the shipping charge and cart name
            Map<String, Object> cartDetails = new HashMap<>();
            cartDetails.put("shipping_charge", 0.0); // Adjust as needed
            cartDetails.put("cart_name", "My Shopping Cart");
            cartDetails.put("cart_items", cartItems);

            // Create order details
            Map<String, Object> orderDetails = new HashMap<>();
            orderDetails.put("order_id", orderID);
            orderDetails.put("order_amount", totalCost);
            orderDetails.put("order_currency", "INR");
            orderDetails.put("cart_details", cartDetails);
            orderDetails.put("customer_details", customerDetails);
            orderDetails.put("order_meta", new HashMap<String, String>()); // Optional

            // Set up CFSession
            CFSession cfSession = new CFSession.CFSessionBuilder()
                    .setEnvironment(cfEnvironment)
                    .setPaymentSessionID(paymentSessionID) // Unique payment session ID
                    .setOrderId(orderID) // Unique order ID
                    .build();

            // Set up CFIntentTheme with custom colors
            CFIntentTheme cfTheme = new CFIntentTheme.CFIntentThemeBuilder()
                    .setBackgroundColor("#FFFFFF")
                    .setPrimaryTextColor("#000000")
                    .build();

            // Set up CFUPIIntentCheckout with the order details
            CFUPIIntentCheckout cfupiIntentCheckout = new CFUPIIntentCheckout.CFUPIIntentBuilder()
                    .setOrder(Arrays.asList(CFUPIIntentCheckout.CFUPIApps.BHIM, CFUPIIntentCheckout.CFUPIApps.PHONEPE))
                    .setOrderUsingPackageName(Arrays.asList("com.dreamplug.androidapp", "in.org.npci.upiapp"))
                    .build();

            // Set up CFUPIIntentCheckoutPayment with the session, order, and theme
            CFUPIIntentCheckoutPayment cfupiIntentCheckoutPayment = new CFUPIIntentCheckoutPayment.CFUPIIntentPaymentBuilder()
                    .setSession(cfSession)
                    .setCfUPIIntentCheckout(cfupiIntentCheckout)
                    .setCfIntentTheme(cfTheme)
                    .build();

            // Initiate the payment process
            Log.d("PurchaseActivity", "Initiating payment for order ID: " + orderID);
            Log.d("PurchaseActivity", "Order details: " + orderDetails);
            Log.d("PurchaseActivity", "Customer details: " + customerDetails);
            Log.d("PurchaseActivity", "Cart details: " + cartDetails);
            // Call the doPayment method to initiate the payment
            CFPaymentGatewayService.getInstance().doPayment(this, cfupiIntentCheckoutPayment);
        } catch (CFException exception) {
            exception.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Generate a unique order ID for each purchase
    private String generateOrderID() {
        // Generate a unique order ID (e.g., using UUID)
        return UUID.randomUUID().toString();
    }

    // Send an email notification to the business
    private void sendOrderNotification(String orderDetails) {
        String businessEmail = BUSINESS_EMAIL;
        String subject = "New Order Recieved!";

        // Send the email notification to the business email address
        SendinblueHelper.sendOrderNotification(businessEmail, subject, orderDetails, this, new SendinblueHelper.EmailCallback() {
            @Override
            public void onResult(boolean success, String message) {
                if (success) {
                    Log.d("Email", "Order notification sent successfully.");
                } else {
                    Log.e("Email", "Failed to send order notification: " + message);
                }
            }
        });
    }

    // Send an order confirmation email to the user
    private void sendOrderConfirmationEmail(String userEmail, String userName, List<String> productNames) {
        String subject = "Order Confirmation - Thank You for Shopping with Amrozia!";

        // Build the product names list
        StringBuilder productListBuilder = new StringBuilder();
        for (String productName : productNames) {
            productListBuilder.append("<li>").append(productName).append("</li>");
        }

        // Email message with the order details to send to the user
        String message = "<h1>Thank You for Your Order!</h1>"
                + "<p>Dear " + userName + ",</p>"
                + "<p>We are thrilled to confirm that your order has been successfully placed with <strong>Amrozia</strong>.</p>"
                + "<p>Order details:</p>"
                + "<ul>" + productListBuilder.toString() + "</ul>"
                + "<p>We are currently processing your order, and you will receive your order tracking link shortly.</p>"
                + "<p>If you have any questions or need assistance, please do not hesitate to reach out to us at <a href='mailto:support@amrozia.in'>support@amrozia.in</a>.</p>"
                + "<p>Thank you for choosing Amrozia! We look forward to serving you again.</p>"
                + "<p>Warm regards,</p>"
                + "<p><strong>Amrozia Team</strong></p>";

        // Send the order confirmation email to the user
        SendinblueHelper.sendOrderNotification(userEmail, subject, message, this, new SendinblueHelper.EmailCallback() {
            @Override
            public void onResult(boolean success, String responseMessage) {
                if (success) {
                    Log.d("Email", "Order confirmation sent to user successfully.");
                } else {
                    Log.e("Email", "Failed to send order confirmation: " + responseMessage);
                }
            }
        });
    }

    // Save the purchase information to Firestore
    private void savePurchaseInfo(ArrayList<String> productNames, ArrayList<String> productPrices, ArrayList<String> productImages, List<ProductDomain> productList, String orderID) {
        // Fetch the last order number from Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Get the reference to the document containing the last order number
        DocumentReference orderNumberRef = db.collection("OrderNumbers").document("LastOrderNumber");

        orderNumberRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String lastOrderNumber = documentSnapshot.getString("lastOrderNumber");

                // Increment the order number (assuming format is like AM240001)
                String newOrderNumber = generateNextOrderNumber(lastOrderNumber);

                // Now store the new order number back to Firestore
                orderNumberRef.update("lastOrderNumber", newOrderNumber);

                // Get user input
                String name = editTextName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();

                // Get the passed product IDs
                Intent intent = getIntent();
                ArrayList<String> productIds = intent.getStringArrayListExtra("productIds");
                ArrayList<String> productCategories = intent.getStringArrayListExtra("productCategories");
                ArrayList<Integer> productQuantities = intent.getIntegerArrayListExtra("productQuantities");

                // Validate the input
                if (name.isEmpty()) {
                    Toast.makeText(PurchaseActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.isEmpty()) {
                    Toast.makeText(PurchaseActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (address.isEmpty()) {
                    Toast.makeText(PurchaseActivity.this, "Please enter your address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the current user's ID
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(PurchaseActivity.this, "User not logged in.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String userId = currentUser.getUid();

                // Create a map to store the data for the purchase
                Map<String, Object> purchase = new HashMap<>();
                purchase.put("orderID", orderID);
                purchase.put("userId", userId);
                purchase.put("name", name);
                purchase.put("email", email);
                purchase.put("address", address);
                purchase.put("phone", phone);
                purchase.put("productNames", productNames);
                purchase.put("productPrices", productPrices);
                purchase.put("productImages", productImages);
                purchase.put("timestamp", FieldValue.serverTimestamp());
                purchase.put("totalCost", totalCost);
                purchase.put("productId", productIds);
                purchase.put("category", productCategories);
                purchase.put("productQuantities", productQuantities);
                purchase.put("orderNumber", newOrderNumber);

                // Save the data to Firestore under the Orders collection with userId
                db.collection("Orders").document(userId).collection("UserOrders")
                        .add(purchase) // Use add to create a new document with a unique ID
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("PurchaseActivity", "DocumentSnapshot successfully written!");
                                updateProductStock(productList, productQuantities);

                                // Retrieve the document to get the timestamp
                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        // Get the server timestamp from the document snapshot
                                        Timestamp timestamp = documentSnapshot.getTimestamp("timestamp");

                                        // Format order details for email
                                        StringBuilder orderDetailsBuilder = new StringBuilder();
                                        orderDetailsBuilder.append("<h1>Order Details</h1>")
                                                .append("<p><strong>User ID:</strong> ").append(userId).append("</p>")
                                                .append("<p><strong>Name:</strong> ").append(name).append("</p>")
                                                .append("<p><strong>Email:</strong> ").append(email).append("</p>")
                                                .append("<p><strong>Address:</strong> ").append(address).append("</p>")
                                                .append("<p><strong>Phone:</strong> ").append(phone).append("</p>")
                                                .append("<p><strong>Order Date:</strong> ").append(timestamp.toDate().toString()).append("</p>")
                                                .append("<p><strong>Order ID:</strong> ").append(orderID).append("</p>")
                                                .append("<p><strong>Order Number:</strong> ").append(newOrderNumber).append("</p>")
                                                .append("<p><strong>Total Cost:</strong> ₹").append(totalCost).append("</p>");

                                        // Add product details to the email body
                                        for (int i = 0; i < productNames.size(); i++) {
                                            orderDetailsBuilder.append("<hr>")
                                                    .append("<h3>Product ").append(i + 1).append("</h3>")
                                                    .append("<p><strong>Product ID:</strong> ").append(productIds.get(i)).append("</p>")
                                                    .append("<p><strong>Product Name:</strong> ").append(productNames.get(i)).append("</p>")
                                                    .append("<p><strong>Price:</strong> ₹").append(productPrices.get(i)).append("</p>")
                                                    .append("<p><strong>Category:</strong> ").append(productCategories.get(i)).append("</p>")
                                                    .append("<p><strong>Quantity:</strong> ").append(productQuantities.get(i)).append("</p>")
                                                    .append("<p><strong>Image URL:</strong> <a href='").append(productImages.get(i)).append("'>View Image</a></p>");
                                        }

                                        // Send email notification to the business
                                        sendOrderNotification(orderDetailsBuilder.toString());

                                        // Send order confirmation email to the user
                                        sendOrderConfirmationEmail(email, name, productNames);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("PurchaseActivity", "Error adding document", e);
                            }
                        });
            }
        });
    }

    // Helper method to generate the next order number
    private String generateNextOrderNumber(String lastOrderNumber) {
        // Assuming the order number format is like AM240001
        String prefix = "AM";
        // Get the last 6 digits of the order number and increment by 1
        int number = Integer.parseInt(lastOrderNumber.substring(2)) + 1;
        // Return the new order number with the prefix
        return prefix + String.format("%06d", number);
    }

    // Update the stock of the purchased products
    private void updateProductStock(List<ProductDomain> productList, ArrayList<Integer> productQuantities) {
        Log.d("PurchaseActivity", "Updating product stock in Firestore");
        for (int i = 0; i < productList.size(); i++) {
            ProductDomain product = productList.get(i);
            int productQuantity = productQuantities.get(i);

            // Fetch category and ID
            String category = product.getCategory();
            String productId = product.getId();

            // Log category and ID for debugging
            Log.d("PurchaseActivity", "Product Category: " + category);
            Log.d("PurchaseActivity", "Product ID: " + productId);


            // Check if category and ID are valid
            if (category != null && !category.isEmpty() && productId != null && !productId.isEmpty()) {
                // Create a reference to the specific product document
                DocumentReference productRef = db.collection("Categories")
                        .document(category) // Use the category to get the collection
                        .collection("products")  // Get the products subcollection
                        .document(productId); // Get the specific product document

                productRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Convert the stock value to int
                            int stock = documentSnapshot.getLong("stock").intValue();
                            int newStock = stock - productQuantity; // Adjust as necessary based on your logic

                            if (newStock <= 0) {
                                // Remove product from Firestore if the stock is 0 or less
                                productRef.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("PurchaseActivity", "Product deleted from Firestore");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("PurchaseActivity", "Error deleting product", e);
                                            }
                                        });
                            } else {
                                // Update product stock in Firestore if the stock is greater than 0
                                productRef.update("stock", newStock)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("PurchaseActivity", "Product stock updated in Firestore");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("PurchaseActivity", "Error updating product stock", e);
                                            }
                                        });
                            }
                        } else {
                            Log.e("PurchaseActivity", "Product document does not exist!");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("PurchaseActivity", "Error getting product document", e);
                    }
                });
            } else {
                Log.e("PurchaseActivity", "Category or Product ID is null or empty");
            }
        }
    }

    // Callback method to handle the payment response from Cashfree
    @Override
    public void onPaymentVerify(String orderID) {
        Log.d("PurchaseActivity", "Payment verified for order ID: " + orderID);

        // Call the method to check payment status
        checkPaymentStatus(orderID, new PaymentStatusCallback() {
            @Override
            public void onResult(String status) {
                if ("SUCCESS".equals(status)) {
                    // Payment is successful, increment the counter and get the final Order ID
                    processSuccessfulPayment(orderID);
                } else {
                    Intent intent = new Intent(PurchaseActivity.this, OrderConfirmationActivity.class);
                    intent.putExtra("status", "failure");
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    // Callback interface to handle payment status
    private void checkPaymentStatus(String orderID, final PaymentStatusCallback callback) {
        String url = "https://api.cashfree.com/pg/orders/" + orderID + "/payments";

        // Create a new JSON array request to get the payment status
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            final String responseBody = response.toString(4); // Pretty print JSON with indentation
                            final String[] paymentStatus = {"UNKNOWN"}; // Use an array to hold the status
                            StringBuilder orderDetails = new StringBuilder();
                            orderDetails.append("Order ID: ").append(orderID).append("\n\n");

                            if (response.length() > 0) {
                                JSONObject paymentInfo = response.getJSONObject(0);

                                // Extract required fields
                                String cfPaymentId = paymentInfo.optString("cf_payment_id", "N/A");
                                String orderAmount = paymentInfo.optString("order_amount", "N/A");
                                String paymentAmount = paymentInfo.optString("payment_amount", "N/A");
                                paymentStatus[0] = paymentInfo.optString("payment_status", "UNKNOWN");
                                String paymentTime = paymentInfo.optString("payment_time", "N/A");

                                // Extract UPI ID if payment method is UPI
                                String upiId = "N/A";
                                JSONObject paymentMethod = paymentInfo.optJSONObject("payment_method");
                                if (paymentMethod != null) {
                                    JSONObject upi = paymentMethod.optJSONObject("upi");
                                    if (upi != null) {
                                        upiId = upi.optString("upi_id", "N/A");
                                    }
                                }

                                orderDetails.append("<html><body>");
                                orderDetails.append("<h2>Payment Details</h2>");
                                orderDetails.append("<table style='border-collapse: collapse; width: 100%; max-width: 600px;'>");
                                orderDetails.append("<tr style='background-color: #f2f2f2;'><th style='padding: 12px; text-align: left; border: 1px solid #ddd;'>Field</th><th style='padding: 12px; text-align: left; border: 1px solid #ddd;'>Value</th></tr>");
                                orderDetails.append("<tr><td style='padding: 12px; border: 1px solid #ddd;'><strong>Order ID</strong></td><td style='padding: 12px; border: 1px solid #ddd;'>").append(orderID).append("</td></tr>");
                                orderDetails.append("<tr><td style='padding: 12px; border: 1px solid #ddd;'><strong>Payment ID</strong></td><td style='padding: 12px; border: 1px solid #ddd;'>").append(cfPaymentId).append("</td></tr>");
                                orderDetails.append("<tr><td style='padding: 12px; border: 1px solid #ddd;'><strong>Order Amount</strong></td><td style='padding: 12px; border: 1px solid #ddd;'>").append(orderAmount).append("</td></tr>");
                                orderDetails.append("<tr><td style='padding: 12px; border: 1px solid #ddd;'><strong>Payment Amount</strong></td><td style='padding: 12px; border: 1px solid #ddd;'>").append(paymentAmount).append("</td></tr>");
                                orderDetails.append("<tr><td style='padding: 12px; border: 1px solid #ddd;'><strong>Payment Status</strong></td><td style='padding: 12px; border: 1px solid #ddd;'>").append(paymentStatus[0]).append("</td></tr>");
                                orderDetails.append("<tr><td style='padding: 12px; border: 1px solid #ddd;'><strong>Payment Method (UPI ID)</strong></td><td style='padding: 12px; border: 1px solid #ddd;'>").append(upiId).append("</td></tr>");
                                orderDetails.append("<tr><td style='padding: 12px; border: 1px solid #ddd;'><strong>Payment Time</strong></td><td style='padding: 12px; border: 1px solid #ddd;'>").append(paymentTime).append("</td></tr>");
                                orderDetails.append("</table>");
                                orderDetails.append("</body></html>");
                            } else {
                                orderDetails.append("No payment information found for this order.\n");
                            }

                            // Prepare email content
                            String subject = "Payment Status for Order " + orderID;

                            // Send email notification
                            SendinblueHelper.sendOrderNotification(BUSINESS_EMAIL, subject, orderDetails.toString(), PurchaseActivity.this, new SendinblueHelper.EmailCallback() {
                                @Override
                                public void onResult(boolean success, String message) {
                                    if (success) {
                                        Log.d("PurchaseActivity", "Email sent successfully");
                                    } else {
                                        Log.e("PurchaseActivity", "Failed to send email: " + message);
                                    }
                                    // Continue with the payment status callback
                                    callback.onResult(paymentStatus[0]);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.onResult("ERROR");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();

                        // Prepare email content for error case
                        String subject = "Payment Status Check Failed for Order " + orderID;
                        String orderDetails = "Order ID: " + orderID + "\nError: " + error.getMessage();

                        // Send email notification for error
                        SendinblueHelper.sendOrderNotification("techv.sahil@gmail.com", subject, orderDetails, PurchaseActivity.this, new SendinblueHelper.EmailCallback() {
                            @Override
                            public void onResult(boolean success, String message) {
                                if (success) {
                                    Log.d("PurchaseActivity", "Error notification email sent successfully");
                                } else {
                                    Log.e("PurchaseActivity", "Failed to send error notification email: " + message);
                                }
                                // Continue with the payment status callback
                                callback.onResult("ERROR");
                            }
                        });
                    }
                }) {
            // Set the headers for the request
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("accept", "application/json");
                headers.put("x-api-version", "2023-08-01");
                headers.put("x-client-id", CLIENT_ID);
                headers.put("x-client-secret", CLIENT_SECRET);
                return headers;
            }
        };

        // Add the request to the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonArrayRequest);
    }

    private void processSuccessfulPayment(String orderID) {
        // Get the product details from the Intent
        Intent intent = getIntent();
        ArrayList<String> productNames = intent.getStringArrayListExtra("productNames");
        ArrayList<String> productPrices = intent.getStringArrayListExtra("productPrices");
        ArrayList<String> productImages = intent.getStringArrayListExtra("productImages");

        // Call savePurchaseInfo with the verified order details
        savePurchaseInfo(productNames, productPrices, productImages, productList, orderID);

        // Remove each item in the order from the cart
        ManagementCart managementCart = ManagementCart.getInstance(PurchaseActivity.this);
        for (ProductDomain product : productList) {
            managementCart.removeItemFromCart(product.getId());
        }

        Intent confirmationIntent = new Intent(PurchaseActivity.this, OrderConfirmationActivity.class);
        confirmationIntent.putExtra("status", "success");
        startActivity(confirmationIntent);
        finish();
    }



    // Callback method to handle payment failure from Cashfree
    @Override
    public void onPaymentFailure(CFErrorResponse cfErrorResponse, String orderID) {
        Log.e("PurchaseActivity", "Payment failed for order ID: " + orderID);
        Intent intent = new Intent(PurchaseActivity.this, OrderConfirmationActivity.class);
        intent.putExtra("status", "failure");
        startActivity(intent);
        finish();
    }

    public interface PaymentStatusCallback {
        void onResult(String status);
    }
}
