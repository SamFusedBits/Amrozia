package com.example.amrozia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amrozia.Helper.SendinblueHelper;
import com.example.amrozia.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderViewHolder> {

    private List<Order> orders;
    private Context context;

    public OrderDetailAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        // Check if the order is more than 6 months old
        Calendar sixMonthsAgo = Calendar.getInstance();
        sixMonthsAgo.add(Calendar.MONTH, -6);
        if (order.getTimestamp().toDate().before(sixMonthsAgo.getTime())) {
            // If the order is more than 6 months old, hide the view
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        } else {
            holder.textViewName.setText("Name: " + order.getName());
            holder.textViewEmail.setText("Email: " + order.getEmail());
            holder.textViewPhone.setText("Phone no.: " + order.getPhone());
            holder.textViewAddress.setText("Address: " + order.getAddress());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            holder.textViewTimestamp.setText("Ordered on: " + sdf.format(order.getTimestamp().toDate()));

            // Set up product details
            StringBuilder productDetails = new StringBuilder();
            for (int i = 0; i < order.getProductNames().size(); i++) {
                productDetails.append(order.getProductNames().get(i))
                        .append(" x ")
                        .append(order.getProductQuantities().get(i))
                        .append("\n");
            }
            holder.textViewProductDetails.setText(productDetails.toString());

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

            holder.buttonSaveTrackingLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.editTextTrackingLink.getVisibility() == View.VISIBLE) {
                        String trackingLink = holder.editTextTrackingLink.getText().toString();
                        Order currentOrder = orders.get(holder.getBindingAdapterPosition());
                        updateTrackingLink(currentOrder, order.getOrderId(), trackingLink, holder);
                    } else {
                        holder.editTextTrackingLink.setVisibility(View.VISIBLE);
                        holder.textViewTrackingLink.setVisibility(View.GONE);
                        holder.buttonSaveTrackingLink.setText("Save Tracking Link");
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewEmail, textViewPhone, textViewAddress, textViewProductDetails, textViewTrackingLink, textViewTimestamp;
        EditText editTextTrackingLink;
        Button buttonSaveTrackingLink;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewProductDetails = itemView.findViewById(R.id.textViewProductDetails);
            editTextTrackingLink = itemView.findViewById(R.id.editTextTrackingLink);
            textViewTrackingLink = itemView.findViewById(R.id.textViewTrackingLink);
            textViewTimestamp = itemView.findViewById(R.id.textViewTimestamp);
            buttonSaveTrackingLink = itemView.findViewById(R.id.buttonSaveTrackingLink);
        }
    }

    private void updateTrackingLink(Order order, String orderId, String trackingLink, OrderViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Orders")
                .document("userId")
                .collection("UserOrders")
                .document(orderId)
                .update("trackingLink", trackingLink)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
                            SendinblueHelper.sendOrderNotification(to, subject, orderDetails, context, new SendinblueHelper.EmailCallback() {
                                @Override
                                public void onResult(boolean success, String message) {
                                    if (success) {
                                        Toast.makeText(context, "Email sent successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Error sending email: " + message, Toast.LENGTH_SHORT).show();
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
                    }
                });
    }
}