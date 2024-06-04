package com.example.amrozia.Adapter;

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
import com.example.amrozia.DetailActivity;
import com.example.amrozia.Domain.ProductDomain;
import com.example.amrozia.R;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<ProductDomain> productList;
    private Context context;
    private String category;

    public ProductAdapter(Context context, List<ProductDomain> productList, String category) {
        this.context = context;
        this.productList = productList;
        this.category = category;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_pop_list, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductDomain product = productList.get(position);
        holder.name.setText(product.getTitle());
        holder.priceTxt.setText(String.format("\u20B9%s", product.getPrice()));  // \u20B9 is the Unicode for the Indian Rupee symbol

        // Assuming picUrl is a list of strings, showing the first image if available
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    ProductDomain clickedProduct = productList.get(pos);
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("productId", clickedProduct.getId());
                    intent.putExtra("category", clickedProduct.getCategory());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView priceTxt;
        public ImageView pic;

        public ProductViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.nameTxt);
            priceTxt = view.findViewById(R.id.priceTxt);
            pic = view.findViewById(R.id.pic);
        }
    }
}
