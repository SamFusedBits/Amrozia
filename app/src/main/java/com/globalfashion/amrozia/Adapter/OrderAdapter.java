package com.globalfashion.amrozia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.globalfashion.amrozia.Domain.OrderDomain;
import com.globalfashion.amrozia.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
// Adapter for the orders in the myorders activity
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    // Context of the activity
    private Context context;
    // List of orders
    private List<OrderDomain> orders;

    // Listener for item click events
    private OnItemClickListener onItemClickListener;

    // Interface for item click events in the order adapter class
    public interface OnItemClickListener {
        void onItemClick(OrderDomain order);
    }

    // Set the listener for item click events
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // Constructor to initialize the context and orders
    public OrderAdapter(Context context, List<OrderDomain> orders) {
        this.context = context;
        this.orders = orders;
    }

    // Create a new view holder
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_order layout and return a new OrderViewHolder with the inflated view
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    // Bind the data to the view holder
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        // Get the order at the given position
        OrderDomain order = orders.get(position);
        // Set the product name, price, and timestamp
        holder.productName.setText(order.getProductName());
        // Format and set the product price
        holder.productPrice.setText(String.format("₹%s", order.getProductPrice()));
        // Format and set timestamp
        if (order.getTimestamp() != null) {
            // Format the timestamp to display in MMM dd, yyyy format
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            // Get the formatted date
            String formattedDate = sdf.format(order.getTimestamp());
            // Set the formatted date to the timestamp text view
            holder.timestamp.setText("Ordered on: " + formattedDate);
        } else {
            holder.timestamp.setText("Ordered on: N/A");
        }

        // Check if trackingLink is not null
        if (order.getTrackingLink() != null && !order.getTrackingLink().isEmpty()) {
            // Set the visibility of the tracking link to visible
            holder.trackingLink.setVisibility(View.VISIBLE);
            // Set the tracking link text
            holder.trackingLink.setText("Track your order >");
            holder.trackingLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open the tracking link in the browser
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(order.getTrackingLink()));
                    context.startActivity(browserIntent);
                }
            });
        } else {
            holder.trackingLink.setVisibility(View.GONE);
        }

        // Load the product image using Glide
        Glide.with(context)
                // Load the product image URL
                .load(order.getProductImage())
                .placeholder(R.drawable.image_unavailable)
                .error(R.drawable.image_unavailable)
                // Set the product image to the image view
                .into(holder.productImage);

        // Set click listener for the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(order); // Pass the order to the listener
                }
            }
        });

        // Set click listener for the tracking link
        holder.trackingLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the tracking link in the browser
                String url = order.getTrackingLink();
                // Check if the URL does not start with http:// or https://
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            }
        });
    }

    // Return the number of orders in the list
    @Override
    public int getItemCount() {
        return orders.size();
    }

    // Update the orders list with new orders
    public void updateOrders(List<OrderDomain> orders) {
        this.orders = orders;
        // Notify the adapter that the data set has changed
        notifyDataSetChanged();
    }

    // View holder for the order items
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, timestamp, trackingLink;
        ImageView productImage;

        // Constructor to initialize the views
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            timestamp = itemView.findViewById(R.id.timestamp);
            productImage = itemView.findViewById(R.id.productImage);
            trackingLink = itemView.findViewById(R.id.trackingLink);
        }
    }
}