package com.example.e_commerce.Seller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.e_commerce.Admin.AdminCheckApproveNewProductsActivity;
import com.example.e_commerce.Buyer.MainActivity;
import com.example.e_commerce.Model.Product;
import com.example.e_commerce.R;
import com.example.e_commerce.ViewHolders.ProductViewHolder;
import com.example.e_commerce.ViewHolders.SellerProductsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SellerHomeActivity extends AppCompatActivity {

    BottomNavigationView navView;
    RecyclerView recyclerView;
    DatabaseReference allProductsref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

         navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        allProductsref= FirebaseDatabase.getInstance().getReference().child("products");

        recyclerView=findViewById(R.id.seller_home_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Product> options=new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(allProductsref.orderByChild("seller_id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()) , Product.class)
                .build();
        FirebaseRecyclerAdapter<Product , SellerProductsViewHolder> adapter=new FirebaseRecyclerAdapter<Product, SellerProductsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SellerProductsViewHolder holder, int position, @NonNull final Product model) {
                holder.product_name.setText(model.getpName());
                holder.product_state.setText("State:  "+model.getProduct_state());
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
                        AlertDialog.Builder builder=new AlertDialog.Builder(SellerHomeActivity.this);
                        builder.setTitle("you want to delete this product , you sure ?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0){
                                    deleteProduct(model.getPid());
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
            public SellerProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_products_view_item , parent ,false);
                return new SellerProductsViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }


    private void deleteProduct(String pid) {
        allProductsref.child(pid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SellerHomeActivity.this, "product deleted successfully.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_add:
                    Intent categoryIntent=new Intent(SellerHomeActivity.this , SellerProductCategoryActivity.class);
                    startActivity(categoryIntent);
                    return true;
                case R.id.navigation_logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent intent=new Intent(SellerHomeActivity.this , MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

            }
            return false;
        }
    };

}
