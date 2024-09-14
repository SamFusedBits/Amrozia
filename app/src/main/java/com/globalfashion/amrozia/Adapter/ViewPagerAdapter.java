package com.globalfashion.amrozia.Adapter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.globalfashion.amrozia.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;
// This adapter class is used to bind the image URLs to the views in the ViewPager
public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {

    // List of image URLs
    private List<String> imageUrl;

    // Constructor to initialize the image URLs
    public ViewPagerAdapter(List<String> imageUrls) {
        this.imageUrl = imageUrls;
        Log.d("ViewPagerAdapter", "ViewPagerAdapter: " + imageUrl);
    }

    // Create a new view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_pager_item, parent, false);
        return new ViewHolder(view);
    }

    // Bind the data to the view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Load the image using Glide
        if (position < imageUrl.size()) {
            Glide.with(holder.itemView)
                    .load(imageUrl.get(position))
                    // Cache the image to reduce loading time
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    // Don't transform the image to avoid distortion or cropping
                    .dontTransform()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("Glide", "Load failed", e);
                            return false; // return false so the error placeholder can be placed
                        }

                        // Placeholder image if the image fails to load
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.photoView);
        }
    }

    // Return the size of the image URLs
    @Override
    public int getItemCount() {
        return imageUrl.size();
    }

    // View holder for the view pager items (images)
    public static class ViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;

        // Constructor to initialize the image view
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.imageView);
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER); // or ImageView.ScaleType.CENTER_CROP OR ImageView.ScaleType.CENTER_INSIDE OR ImageView.ScaleType.FIT_CENTER
        }
    }
}