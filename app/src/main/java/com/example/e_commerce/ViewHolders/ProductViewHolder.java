package com.example.e_commerce.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce.Interfaces.ItemClickListner;
import com.example.e_commerce.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    ItemClickListner listner;
    public void setItemClickListner( ItemClickListner listner){
        this.listner=listner;
    }

   public TextView product_name , product_description , product_price;
   public ImageView product_image;
    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        product_name=itemView.findViewById(R.id.item_product_name);
        product_description=itemView.findViewById(R.id.item_product_description);
        product_price=itemView.findViewById(R.id.item_product_price);
        product_image=itemView.findViewById(R.id.item_product_image);
    }

    @Override
    public void onClick(View v) {
        listner.onclick(itemView ,getAdapterPosition() , false);
    }
}
