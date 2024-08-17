package com.example.amrozia.Adapter;

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
import com.example.amrozia.Domain.OrderDomain;
import com.example.amrozia.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<OrderDomain> orders;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(OrderDomain order);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OrderAdapter(Context context, List<OrderDomain> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        OrderDomain order = orders.get(position);
        holder.productName.setText(order.getProductName());
        holder.productPrice.setText(String.format("₹%s", order.getProductPrice()));
        // Format and set timestamp
        if (order.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedDate = sdf.format(order.getTimestamp());
            holder.timestamp.setText("Ordered on: " + formattedDate);
        } else {
            holder.timestamp.setText("Ordered on: N/A");
        }

        // Check if trackingLink is not null
        if (order.getTrackingLink() != null && !order.getTrackingLink().isEmpty()) {
            holder.trackingLink.setVisibility(View.VISIBLE);
            holder.trackingLink.setText("Track your order >");
            holder.trackingLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(order.getTrackingLink()));
                    context.startActivity(browserIntent);
                }
            });
        } else {
            holder.trackingLink.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(order.getProductImage())
                .placeholder(R.drawable.image_unavailable)
                .error(R.drawable.image_unavailable)
                .into(holder.productImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(order); // Pass the order to the listener
                }
            }
        });

        holder.trackingLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = order.getTrackingLink();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<OrderDomain> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, timestamp, trackingLink;
        ImageView productImage;

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