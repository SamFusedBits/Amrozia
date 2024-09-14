package com.globalfashion.amrozia.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.globalfashion.amrozia.Helper.SendinblueHelper;
import com.globalfashion.amrozia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
// Adapter for the order details in the order management detail activity
public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderViewHolder> {
    // List of orders
    private List<Order> orders;
    private Context context;

    // Constructor to initialize the context and orders
    public OrderDetailAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    // Create a new view holder
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new OrderViewHolder(view);
    }

    // Bind the data to the view holder
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        // Check if the order is more than 6 months old
        Calendar sixMonthsAgo = Calendar.getInstance();
        // Subtract 6 months from the current date
        sixMonthsAgo.add(Calendar.MONTH, -6);
        if (order.getTimestamp().toDate().before(sixMonthsAgo.getTime())) {
            // If the order is more than 6 months old, hide the view
            holder.itemView.setVisibility(View.GONE);
            // Set the layout params to 0, 0 to hide the view
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        } else {
            // Bind the order details to the view holder
            holder.orderNumber.setText("Order Number: " + order.getOrderNumber());
            holder.textViewName.setText("Name: " + order.getName());
            holder.textViewEmail.setText("Email: " + order.getEmail());
            holder.textViewPhone.setText("Phone no.: " + order.getPhone());
            holder.textViewAddress.setText("Address: " + order.getAddress());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            holder.textViewTimestamp.setText("Ordered on: " + sdf.format(order.getTimestamp().toDate()));
            holder.textViewTotalCost.setText("Total amount: ₹" + order.getTotalCost());

            // Set up product details
            StringBuilder productDetails = new StringBuilder();
            for (int i = 0; i < order.getProductNames().size(); i++) {
                // Append the product name and quantity to the string builder
                productDetails.append(order.getProductNames().get(i))
                        .append(" x ")
                        .append(order.getProductQuantities().get(i))
                        .append("\n");
            }
            // Set the product details to the text view
            holder.textViewProductDetails.setText(productDetails.toString());

            // Check if the tracking link is available
            if (order.getTrackingLink() != null && !order.getTrackingLink().isEmpty()) {
                holder.editTextTrackingLink.setVisibility(View.GONE);
                holder.buttonSaveTrackingLink.setText("Edit Tracking Link");
                holder.textViewTrackingLink.setVisibility(View.VISIBLE);
                holder.textViewTrackingLink.setText(order.getTrackingLink());
            } else {
                holder.editTextTrackingLink.setVisibility(View.VISIBLE);
                holder.buttonSaveTrackingLink.setText("Save Tracking Link");
                holder.textViewTrackingLink.setVisibility(View.GONE);
            }

            // Set click listener for the save tracking link button
            holder.buttonSaveTrackingLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.editTextTrackingLink.getVisibility() == View.VISIBLE) {
                        // Get the tracking link from the edit text
                        String trackingLink = holder.editTextTrackingLink.getText().toString();
                        // Get the current order
                        Order currentOrder = orders.get(holder.getBindingAdapterPosition());
                        // Update the tracking link
                        updateTrackingLink(currentOrder, order.getOrderId(), trackingLink, holder);
                    } else {
                        // Show the edit text and hide the text view and change the button text
                        holder.editTextTrackingLink.setVisibility(View.VISIBLE);
                        holder.textViewTrackingLink.setVisibility(View.GONE);
                        holder.buttonSaveTrackingLink.setText("Save Tracking Link");
                    }
                }
            });
        }
    }

    // Return the size of the orders
    @Override
    public int getItemCount() {
        return orders.size();
    }

    // View holder for the order details
    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, textViewName, textViewEmail, textViewPhone, textViewAddress, textViewTotalCost, textViewProductDetails, textViewTrackingLink, textViewTimestamp;
        EditText editTextTrackingLink;
        Button buttonSaveTrackingLink;

        // Constructor to initialize the views
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.orderNumber);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewProductDetails = itemView.findViewById(R.id.textViewProductDetails);
            textViewTotalCost = itemView.findViewById(R.id.textViewTotalCost);
            editTextTrackingLink = itemView.findViewById(R.id.editTextTrackingLink);
            textViewTrackingLink = itemView.findViewById(R.id.textViewTrackingLink);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);

            buttonSaveTrackingLink = itemView.findViewById(R.id.buttonSaveTrackingLink);
        }
    }

    // Update the tracking link in the Firestore database
    private void updateTrackingLink(Order order, String orderId, String trackingLink, OrderViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Orders")
                .document(order.getUserId())
                .collection("UserOrders")
                .document(orderId)
                .update("trackingLink", trackingLink)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Show a success message
                        Toast.makeText(context, "Tracking link updated successfully!", Toast.LENGTH_SHORT).show();
                        holder.editTextTrackingLink.setVisibility(View.GONE);
                        holder.textViewTrackingLink.setText(trackingLink);
                        holder.textViewTrackingLink.setVisibility(View.VISIBLE);
                        holder.buttonSaveTrackingLink.setText("Edit Tracking Link");

                        // Send email notification only if tracking link is not null and not empty
                        if (trackingLink != null && !trackingLink.isEmpty()) {
                            String to = order.getEmail(); // Get the user's email from the Order object
                            String subject = "Your Order Tracking Link";
                            String orderDetails = "<h2>Dear " + order.getName() + ",</h2>"
                                    + "<p>Thank you for your order from Amrozia. We are pleased to inform you that your order has been processed and is ready for shipment.</p>"
                                    + "<p>Your tracking link is: " + trackingLink + "</p>"
                                    + "<p>We hope you enjoy your purchase!</p>"
                                    + "<p>Best Regards,<br>Amrozia Team</p>";
                            // Send the order notification email
                            SendinblueHelper.sendOrderNotification(to, subject, orderDetails, context, new SendinblueHelper.EmailCallback() {
                                @Override
                                public void onResult(boolean success, String message) {
                                    if (success) {
                                        Log.d("OrderDetailAdapter", "Email sent successfully.");
                                    } else {
                                        Log.e("OrderDetailAdapter", "Error sending email: " + message);
                                    }
                                }
                            });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Error updating tracking link.", Toast.LENGTH_SHORT).show();
                        Log.e("OrderDetailAdapter", "Error updating tracking link.", e);
                    }
                });
    }
}