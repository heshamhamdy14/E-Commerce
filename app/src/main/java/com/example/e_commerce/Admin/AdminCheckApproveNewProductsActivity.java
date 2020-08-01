package com.example.e_commerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.e_commerce.Model.Product;
import com.example.e_commerce.R;
import com.example.e_commerce.ViewHolders.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class AdminCheckApproveNewProductsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference notApprovedProducts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_check_approve_new_products);

        notApprovedProducts= FirebaseDatabase.getInstance().getReference().child("products");

        recyclerView=findViewById(R.id.admin_check_products_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Product>options=new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(notApprovedProducts.orderByChild("product_state").equalTo("not approved") , Product.class)
                .build();
        FirebaseRecyclerAdapter<Product , ProductViewHolder> adapter=new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Product model) {
                holder.product_name.setText(model.getpName());
                holder.product_description.setText(model.getDescription());
                holder.product_price.setText("price: "+model.getPrice()+"$");
                Picasso.get().load(model.getImageUrl()).into(holder.product_image);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence []options=new CharSequence[]{
                           "Yes",
                           "No"
                        } ;
                        AlertDialog.Builder builder=new AlertDialog.Builder(AdminCheckApproveNewProductsActivity.this);
                        builder.setTitle("you want to approve this product , you sure ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                                    ApproveProduct(model.getPid());
                                    dialog.cancel();
                                }else if(which==1){

                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_view , parent ,false);
                return new ProductViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void ApproveProduct(String id) {
        notApprovedProducts.child(id).child("product_state").setValue("approved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AdminCheckApproveNewProductsActivity.this, "product approved successfully , it is available now for selling.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
