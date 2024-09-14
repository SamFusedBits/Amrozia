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
import com.globalfashion.amrozia.CartActivity;
import com.globalfashion.amrozia.Domain.ItemsDomain;
import com.globalfashion.amrozia.Helper.ManagementCart;
import com.globalfashion.amrozia.R;

import java.util.List;
// Adapter for the cart items in the cart activity
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
        // List of cart items
        private List<ItemsDomain> cartList;
        // Context of the activity
        private Context context;
        // ManagementCart instance to save and retrieve cart items
        private ManagementCart managementCart;

        // Constructor to initialize the context, cart list, and ManagementCart instance
        public CartAdapter(Context context, List<ItemsDomain> cartList, ManagementCart managementCart) {
            this.context = context;
            this.cartList = cartList;
            this.managementCart = managementCart;
        }

    // Create a new view holder
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the viewholder_cart layout
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_cart, parent, false);
        // Return a new CartViewHolder with the inflated view
        return new CartViewHolder(view);
    }

    // Bind the data to the view holder
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        // Bind the item to the view holder
        holder.bind(cartList.get(position));
    }

    // Return the size of the cart list
    @Override
    public int getItemCount() {
        return cartList.size();
    }

    //Display the cart items
    public class CartViewHolder extends RecyclerView.ViewHolder {
        // TextViews and ImageViews for the cart item
        private TextView titleTxt, priceTxt, numberItemTxt, plusCartBtn;
        private ImageView pic, minusCartBtn;

        // Constructor to initialize the TextViews and ImageViews
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            pic = itemView.findViewById(R.id.pic);
            numberItemTxt = itemView.findViewById(R.id.numberItemTxt);
            plusCartBtn = itemView.findViewById(R.id.plusCartBtn);
            minusCartBtn = itemView.findViewById(R.id.minusCartBtn);

            // Check if the TextViews are null
            if (titleTxt == null || priceTxt == null || numberItemTxt == null || plusCartBtn == null || minusCartBtn == null) {
                throw new RuntimeException("Null");
            }
        }

        // Bind the item to the view holder
        public void bind(ItemsDomain item) {
            // Set the text and image of the cart item
            titleTxt.setText(item.getTitle());
            priceTxt.setText(String.format("₹%.2f", item.getPrice()));
            numberItemTxt.setText(String.valueOf(item.getQuantity()));

            // Load the image using Glide
            Glide.with(itemView)
                    .load(item.getPicUrl().get(0)) // Assuming the first URL is the one you want to display
                    .into(pic);

            // Set click listeners for the plus and minus buttons
            plusCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Increase the quantity of the item
                    int quantity = item.getQuantity();
                    quantity++;
                    // Update the quantity in the item
                    item.setQuantity(quantity);
                    // Update the quantity in the TextView
                    numberItemTxt.setText(String.valueOf(quantity));
                    managementCart.saveCart(cartList); // Save the updated cart
                    ((CartActivity) context).calculateCart(); // Update the total cost
                }
            });

            // Set click listener for the minus button
            minusCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Decrease the quantity of the item
                    int quantity = item.getQuantity();
                    if (quantity > 0) {
                        quantity--;
                        item.setQuantity(quantity);
                        // Update the quantity in the TextView
                        numberItemTxt.setText(String.valueOf(quantity));
                        if (quantity == 0) {
                            // Remove the item from the cart if the quantity is 0
                            int position = getAdapterPosition();
                            // Check if the position is valid
                            cartList.remove(position);
                            // Notify the adapter that an item has been removed
                            notifyItemRemoved(position);
                            // Notify the adapter that the data set has changed
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