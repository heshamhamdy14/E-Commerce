package com.example.e_commerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.e_commerce.Model.Cart;
import com.example.e_commerce.R;
import com.example.e_commerce.ViewHolders.CartviewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminUserProductsActivity extends AppCompatActivity {

    String uid="";
    RecyclerView recyclerView;
    DatabaseReference cartref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_products);

        uid=getIntent().getStringExtra("uid");
        recyclerView=findViewById(R.id.admin_user_products_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        cartref= FirebaseDatabase.getInstance().getReference().child("cart list").child("admin view").child(uid).child("products");
        FirebaseRecyclerOptions<Cart>options=new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartref , Cart.class)
                .build();
        FirebaseRecyclerAdapter<Cart , CartviewHolder>adapter=new FirebaseRecyclerAdapter<Cart, CartviewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartviewHolder holder, int position, @NonNull Cart model) {
                holder.cart_product_name.setText(model.getpName());
                holder.cart_product_quantity.setText("Quantity: "+model.getQuantity());
                holder.cart_product_price.setText("price: "+model.getPrice()+"$");
            }

            @NonNull
            @Override
            public CartviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_list_item ,parent ,false);
                return new CartviewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}
