package com.example.amrozia.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amrozia.Domain.ProductDomain;
import com.example.amrozia.R;

import java.util.List;

public class PurchaseProductAdapter extends RecyclerView.Adapter<PurchaseProductAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductDomain> productList;

    public PurchaseProductAdapter(Context context, List<ProductDomain> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductDomain product = productList.get(position);
        holder.productNameTxt.setText(product.getTitle());
        holder.productPriceTxt.setText(String.format("Price: ₹" + product.getPrice()));  // \u20B9 is the Unicode for the Indian Rupee symbol
        holder.productQuantityTxt.setText("Qty: " + product.getQuantity());

        // Assuming picUrl is a list of strings, showing the first image if available
        if (!product.getPicUrl().isEmpty()) {
            Glide.with(context).load(product.getPicUrl().get(0)).into(holder.productImageView);
        } else {
            holder.productImageView.setImageResource(R.drawable.image_unavailable); // Placeholder image if picUrl is empty
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView productNameTxt;
        public TextView productPriceTxt;
        public TextView productQuantityTxt;
        public ImageView productImageView;

        public ProductViewHolder(View view) {
            super(view);
            productNameTxt = view.findViewById(R.id.productNameTxt);
            productPriceTxt = view.findViewById(R.id.productPriceTxt);
            productQuantityTxt = view.findViewById(R.id.productQuantityTxt);
            productImageView = view.findViewById(R.id.productImageView);
        }
    }
}