package com.example.e_commerce.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce.Interfaces.ItemClickListner;
import com.example.e_commerce.R;

public class CartviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    ItemClickListner itemClickListner;
    public TextView cart_product_name , cart_product_quantity , cart_product_price;
    public CartviewHolder(@NonNull View itemView) {
        super(itemView);
        cart_product_name=itemView.findViewById(R.id.cart_list_product_name);
        cart_product_quantity=itemView.findViewById(R.id.cart_list_product_quantity);
        cart_product_price=itemView.findViewById(R.id.cart_list_product_price);

    }

    @Override
    public void onClick(View v) {
        itemClickListner.onclick(itemView , getAdapterPosition() ,false);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }
}
