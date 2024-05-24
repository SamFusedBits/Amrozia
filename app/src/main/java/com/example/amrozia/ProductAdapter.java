package com.example.amrozia;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
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
        Product product = productList.get(position);
        holder.name.setText(product.getName());
        holder.priceTxt.setText(String.format("$%s", product.getPrice()));
        Glide.with(holder.itemView.getContext()).load(product.getImageUrl()).into(holder.pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    Product clickedProduct = productList.get(pos);
                    Intent intent = new Intent(v.getContext(), DetailActivity.class);
                    intent.putExtra("Product", clickedProduct); // Make sure Product implements Serializable or Parcelable
                    v.getContext().startActivity(intent);
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