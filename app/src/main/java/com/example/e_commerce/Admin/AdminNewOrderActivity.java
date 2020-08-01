package com.example.e_commerce.Admin;

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

import com.example.e_commerce.Model.AdminOrders;
import com.example.e_commerce.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminNewOrderActivity extends AppCompatActivity {

    private DatabaseReference orderref;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_order);
        orderref= FirebaseDatabase.getInstance().getReference().child("orders");
        recyclerView=findViewById(R.id.admin_orders_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<AdminOrders>options=new FirebaseRecyclerOptions.Builder<AdminOrders>()
                .setQuery(orderref ,AdminOrders.class)
                .build();
        FirebaseRecyclerAdapter<AdminOrders , AdminOrderViewHolder>adapter=new FirebaseRecyclerAdapter<AdminOrders, AdminOrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AdminOrderViewHolder holder, final int position, @NonNull AdminOrders model) {
                holder.user_name.setText("name: "+model.getName());
                holder.phone_number.setText("phone number: "+model.getPhone());
                holder.total_price.setText("total price: "+model.getTotalPrice());
                holder.address.setText("address: "+model.getAddress()+" "+model.getCity());
                holder.date.setText("Date: "+model.getDate()+", "+model.getTime());
                holder.showOrderProducts.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uId=getRef(position).getKey();
                        Intent intent=new Intent(AdminNewOrderActivity.this ,AdminUserProductsActivity.class);
                        intent.putExtra("uid" , uId);
                        startActivity(intent);
                    }
                });

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence []items=new CharSequence[]{
                          "yes",
                          "no"
                        };
                        AlertDialog.Builder builder=new AlertDialog.Builder(AdminNewOrderActivity.this);
                        builder.setTitle("is this order shipped ? ");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                                    String uId=getRef(position).getKey();
                                    orderref.child(uId).removeValue();
                                }
                                else if (which==1){
                                    finish();
                                }
                            }
                        });
                        builder.show();
                    }
                });

            }

            @NonNull
            @Override
            public AdminOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_layout ,parent ,false);
                return new AdminOrderViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class AdminOrderViewHolder extends RecyclerView.ViewHolder
    {
        TextView user_name , phone_number , total_price , address , date;
        Button showOrderProducts;
        public AdminOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            user_name=itemView.findViewById(R.id.order_user_name);
            phone_number=itemView.findViewById(R.id.order_phone_number);
            total_price=itemView.findViewById(R.id.order_total_price);
            address=itemView.findViewById(R.id.order_address);
            date=itemView.findViewById(R.id.order_date);
            showOrderProducts=itemView.findViewById(R.id.show_order_products);

        }
    }
}
