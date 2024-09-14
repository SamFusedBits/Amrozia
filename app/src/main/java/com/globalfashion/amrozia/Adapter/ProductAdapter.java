package com.globalfashion.amrozia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.globalfashion.amrozia.DetailActivity;
import com.globalfashion.amrozia.Domain.ProductDomain;
import com.globalfashion.amrozia.R;

import java.util.List;
// This adapter class is used to bind the product data to the views in the RecyclerView
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    // List of products
    private List<ProductDomain> productList;
    private Context context;
    private String category;

    // Constructor to initialize the context and product list
    public ProductAdapter(Context context, List<ProductDomain> productList, String category) {
        this.context = context;
        this.productList = productList;
        this.category = category;
    }

    // Create a new view holder for the product items
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the view for the product item
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_pop_list, parent, false);
        // Return the view holder
        return new ProductViewHolder(itemView);
    }

    // Bind the product data to the views
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Get the product at the current position
        ProductDomain product = productList.get(position);
        // Set the product name and price
        holder.name.setText(product.getTitle());
        holder.priceTxt.setText(String.format("\u20B9%s", product.getPrice()));  // \u20B9 is the Unicode for the Indian Rupee symbol

        // picUrl is a list of strings, showing the first image if available
        if (!product.getPicUrl().isEmpty()) {
            Glide.with(context).load(product.getPicUrl().get(0)).into(holder.pic);
        } else if(product.getPicUrl().size() > 1) {
            Glide.with(context).load(product.getPicUrl().get(0)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(1)).into(holder.pic);
        } else if(product.getPicUrl().size() > 2) {
            Glide.with(context).load(product.getPicUrl().get(0)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(1)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(2)).into(holder.pic);
        } else if(product.getPicUrl().size() > 3) {
            Glide.with(context).load(product.getPicUrl().get(0)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(1)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(2)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(3)).into(holder.pic);
        } else if(product.getPicUrl().size() > 4) {
            Glide.with(context).load(product.getPicUrl().get(0)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(1)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(2)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(3)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(4)).into(holder.pic);
        }  else if(product.getPicUrl().size() > 5) {
            Glide.with(context).load(product.getPicUrl().get(0)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(1)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(2)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(3)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(4)).into(holder.pic);
            Glide.with(context).load(product.getPicUrl().get(5)).into(holder.pic);
        }
        else {
            holder.pic.setImageResource(R.drawable.image_unavailable); // Placeholder image if picUrl is empty
        }

        // Set click listener to open detail activity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the position of the clicked item
                int pos = holder.getBindingAdapterPosition();
                // Check if the position is valid
                if (pos != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    // Pass the product details to the detail activity
                    intent.putExtra("productId", productList.get(holder.getBindingAdapterPosition()).getId());
                    intent.putExtra("category", productList.get(holder.getBindingAdapterPosition()).getCategory());
                    intent.putExtra("price", productList.get(holder.getBindingAdapterPosition()).getPrice());
                    // Start the detail activity with the product details passed as extras in the intent
                    intent.putExtra("fromSalesProductActivity", true);
                    context.startActivity(intent);
                }
            }
        });
    }

    // Update the product list with new products
    public void updateProducts(List<ProductDomain> newProducts) {
        this.productList = newProducts;
        // Notify the adapter that the data set has changed
        notifyDataSetChanged();
    }

    // Get the number of items in the product list
    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder class for the product items
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView priceTxt;
        public ImageView pic;

        // Constructor to initialize the views
        public ProductViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nameTxt);
            priceTxt = view.findViewById(R.id.priceTxt);
            pic = view.findViewById(R.id.pic);
        }
    }

    // Update the product list with new products
    public void updateProductList(List<ProductDomain> newProductList) {
        this.productList = newProductList;
        // Notify the adapter that the data set has changed
        notifyDataSetChanged();
    }
}
