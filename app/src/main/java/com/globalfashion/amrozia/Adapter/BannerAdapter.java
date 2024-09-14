package com.globalfashion.amrozia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.globalfashion.amrozia.Activity.SalesProductActivity;
import com.globalfashion.amrozia.R;

import java.util.List;

// Adapter for the sale banner images on the home screen
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    // List of banner image URLs
    private List<String> bannerUrls;
    private Context context;

    // Constructor to initialize the context and banner URLs
    public BannerAdapter(Context context, List<String> bannerUrls) {
        this.context = context;
        this.bannerUrls = bannerUrls;
    }

    // Create a new view holder
    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_banner layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        // Return a new BannerViewHolder with the inflated view
        return new BannerViewHolder(view);
    }

    // Bind the data to the view holder
    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        // Load the image using Glide
        String imageUrl = bannerUrls.get(position);
        Glide.with(context)
                .load(bannerUrls.get(position))
                .into(holder.bannerImage);

        // Set click listener to navigate to SalesActivity
        holder.bannerImage.setOnClickListener(v -> {
            Intent intent = new Intent(context, SalesProductActivity.class);
            context.startActivity(intent);
        });
    }

    // Return the size of the banner URLs
    @Override
    public int getItemCount() {
        return bannerUrls.size();
    }

    // View holder for the banner images
    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;

        // Constructor to initialize the banner image view
        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.banner_image);
        }
    }
}