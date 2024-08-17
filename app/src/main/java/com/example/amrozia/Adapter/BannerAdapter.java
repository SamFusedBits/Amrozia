package com.example.amrozia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amrozia.Activity.SalesProductActivity;
import com.example.amrozia.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private List<String> bannerUrls;
    private Context context;

    public BannerAdapter(Context context, List<String> bannerUrls) {
        this.context = context;
        this.bannerUrls = bannerUrls;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
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

    @Override
    public int getItemCount() {
        return bannerUrls.size();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.banner_image);
        }
    }
}