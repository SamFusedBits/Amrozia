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
import com.example.amrozia.CartActivity;
import com.example.amrozia.Domain.ItemsDomain;
import com.example.amrozia.Helper.ManagementCart;
import com.example.amrozia.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

        private List<ItemsDomain> cartList;
        private Context context;
        private ManagementCart managementCart;

        public CartAdapter(Context context, List<ItemsDomain> cartList, ManagementCart managementCart) {
            this.context = context;
            this.cartList = cartList;
            this.managementCart = managementCart;
        }



    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(cartList.get(position));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    //Display the cart items
    public class CartViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTxt, priceTxt, sizeTxt, numberItemTxt, plusCartBtn, minusCartBtn;

        private ImageView pic;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            sizeTxt = itemView.findViewById(R.id.sizeTxt);
            pic = itemView.findViewById(R.id.pic);
            numberItemTxt = itemView.findViewById(R.id.numberItemTxt);
            plusCartBtn = itemView.findViewById(R.id.plusCartBtn);
            minusCartBtn = itemView.findViewById(R.id.minusCartBtn);

            // Check if the TextViews are null
            if (titleTxt == null || priceTxt == null || sizeTxt == null | numberItemTxt == null || plusCartBtn == null || minusCartBtn == null) {
                throw new RuntimeException("Check your viewholder_cart.xml layout");
            }
        }

        public void bind(ItemsDomain item) {
            titleTxt.setText(item.getTitle());
            priceTxt.setText(String.format("₹%.2f", item.getPrice()));
            sizeTxt.setText(item.getSize());

            // Load the image using Glide
            Glide.with(itemView)
                    .load(item.getPicUrl().get(0)) // Assuming the first URL is the one you want to display
                    .into(pic);

            plusCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = item.getQuantity();
                    quantity++;
                    item.setQuantity(quantity);
                    numberItemTxt.setText(String.valueOf(quantity));
                    managementCart.saveCart(cartList); // Save the updated cart
                    ((CartActivity) context).calculateCart(); // Update the total cost
                }
            });

            minusCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int quantity = item.getQuantity();
                    if (quantity > 0) {
                        quantity--;
                        item.setQuantity(quantity);
                        numberItemTxt.setText(String.valueOf(quantity));
                        if (quantity == 0) {
                            // Remove the item from the cart
                            int position = getAdapterPosition();
                            cartList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, cartList.size());
                        }
                        managementCart.saveCart(cartList); // Save the updated cart
                        ((CartActivity) context).calculateCart(); // Update the total cost
                    }
                }
            });
        }
    }
}