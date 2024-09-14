package com.globalfashion.amrozia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.globalfashion.amrozia.Domain.ProductDomain;
import com.globalfashion.amrozia.R;

import java.util.List;
// This adapter class is used to bind the purchased product data to the views in the RecyclerView
public class PurchaseProductAdapter extends RecyclerView.Adapter<PurchaseProductAdapter.ProductViewHolder> {

    // Context of the activity
    private Context context;
    private List<ProductDomain> productList;

    // Constructor to initialize the context and product list
    public PurchaseProductAdapter(Context context, List<ProductDomain> productList) {
        this.context = context;
        this.productList = productList;
    }

    // Create a new view holder for the product items
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view for the product item
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    // Bind the product data to the views
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Get the product at the current position
        ProductDomain product = productList.get(position);
        // Set the product name, price, and quantity
        holder.productNameTxt.setText(product.getTitle());

        // Calculate total price for the product (price * quantity)
        double totalPrice = product.getPrice() * product.getQuantity();
        holder.productPriceTxt.setText(String.format("Price: ₹%.2f", totalPrice));  // Displaying price with 2 decimal places

        // Set the product quantity
        holder.productQuantityTxt.setText("Qty: " + product.getQuantity());

        // Assuming picUrl is a list of strings, showing the first image if available
        if (!product.getPicUrl().isEmpty()) {
            // Load the image using Glide
            Glide.with(context).load(product.getPicUrl().get(0)).into(holder.productImageView);
        } else {
            holder.productImageView.setImageResource(R.drawable.image_unavailable); // Placeholder image if picUrl is empty
        }
    }

    // Return the size of the product list
    @Override
    public int getItemCount() {
        return productList.size();
    }

    // View holder for the product items
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView productNameTxt;
        public TextView productPriceTxt;
        public TextView productQuantityTxt;
        public ImageView productImageView;

        // Constructor to initialize the TextViews and ImageView
        public ProductViewHolder(View view) {
            super(view);
            productNameTxt = view.findViewById(R.id.productNameTxt);
            productPriceTxt = view.findViewById(R.id.productPriceTxt);
            productQuantityTxt = view.findViewById(R.id.productQuantityTxt);
            productImageView = view.findViewById(R.id.productImageView);
        }
    }
}