package com.example.e_commerce.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.Model.Cart;
import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.example.e_commerce.ViewHolders.CartviewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView totalPrice;
    Button nextProcess;
    double totalAmountOfProductsPrice=0;
    TextView msg1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView=findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        totalPrice=findViewById(R.id.total_price);
        nextProcess=findViewById(R.id.next_process);
        msg1=findViewById(R.id.msg1);


        nextProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CartActivity.this , ConfirmFinalOrderActivity.class);
                intent.putExtra("total price" , String.valueOf(totalAmountOfProductsPrice));
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkShippingstate();

        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("cart list");
        FirebaseRecyclerOptions<Cart>options=new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(reference.child("user view")
                        .child(Prevalent.currentOnlineUsers.getPhone())
                        .child("products") ,Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart , CartviewHolder>adapter=new FirebaseRecyclerAdapter<Cart, CartviewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartviewHolder holder, int position, @NonNull final Cart model) {
                holder.cart_product_name.setText(model.getpName());
                holder.cart_product_quantity.setText("Quantity: "+model.getQuantity());
                holder.cart_product_price.setText("price: "+model.getPrice()+"$");

                //double variable to hold price of each product
                double priceForOneProduct = (Integer.parseInt(model.getPrice())) * Integer.parseInt(model.getQuantity());
                totalAmountOfProductsPrice = totalAmountOfProductsPrice + priceForOneProduct;
                totalPrice.setText("Total price: "+String.valueOf(totalAmountOfProductsPrice)+"$");

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final CharSequence items[]=new CharSequence[]{
                           "Edit" ,
                           "Remove"
                        } ;

                        //Alert dialog to open dialog that have items like remove , edit ...etc
                        AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0){
                                    Intent intent=new Intent(CartActivity.this ,Product_DetailsActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);
                                }
                                if (which == 1){
                                    reference.child("user view").child(Prevalent.currentOnlineUsers.getPhone())
                                            .child("products").child(model.getPid())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                reference.child("admin view").child(Prevalent.currentOnlineUsers.getPhone())
                                                        .child("products").child(model.getPid())
                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Intent intent=new Intent(CartActivity.this ,HomeActivity.class);
                                                            startActivity(intent);
                                                            Toast.makeText(CartActivity.this, "product removed successfully.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }

                            }
                        });
                        builder.show();
                    }
                });
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

    private void checkShippingstate(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("orders").child(Prevalent.currentOnlineUsers.getPhone());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String shipping_state=snapshot.child("state").getValue().toString();
                    String name=snapshot.child("name").getValue().toString();
                    if (shipping_state.equals("shipped")){
                        totalPrice.setText("Dear "+name+"order is shipped");
                        msg1.setText("congratulation "+name+": your final order has peen shipped successfully.");
                        msg1.setVisibility(View.VISIBLE);
                        nextProcess.setVisibility(View.GONE);
                    }else if(shipping_state.equals("not shipped")){
                        totalPrice.setText("order: not shipped");
                        msg1.setVisibility(View.VISIBLE);
                        nextProcess.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
