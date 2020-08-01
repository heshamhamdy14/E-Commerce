package com.example.e_commerce.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.e_commerce.Model.Product;
import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class Product_DetailsActivity extends AppCompatActivity {

    ImageView details_product_image;
    TextView details_product_name , details_product_description , details_product_price;
    ElegantNumberButton product_number;
    Button add_product_to_cart;
    String productId="";
    String state="normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__details);

        productId=getIntent().getExtras().getString("pid");

        details_product_image=findViewById(R.id.product_image_details);
        details_product_name=findViewById(R.id.product_name_details);
        details_product_description=findViewById(R.id.product_description_details);
        details_product_price=findViewById(R.id.product_price_details);
        product_number=findViewById(R.id.number_btn);
        add_product_to_cart=findViewById(R.id.details_add_to_cart_btn);

        getProductDetailsFromHomeAct();

        add_product_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.equals("order shipped")||state.equals("order placed")){
                    Toast.makeText(Product_DetailsActivity.this, "you can add more purchases once your oder is shipped or confirmed", Toast.LENGTH_SHORT).show();
                }else {
                    addToCartList();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkShippingstate();
    }

    //add product to cartList
    private void addToCartList() {
        String saveCurrentDate , saveCurrentTime;
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("MMM dd yyyy");
        saveCurrentDate=currentdate.format(calendar.getTime());
        SimpleDateFormat currenttime=new SimpleDateFormat("HH:MM:SS a");
        saveCurrentTime=currenttime.format(calendar.getTime());

        final DatabaseReference cartref=FirebaseDatabase.getInstance().getReference().child("cart list");
        final HashMap<String , Object>carthash=new HashMap<>();
        carthash.put("pid", productId);
        carthash.put("pName", details_product_name.getText().toString());
        carthash.put("price", details_product_price.getText().toString());
        carthash.put("quantity", product_number.getNumber());
        carthash.put("currentDate", saveCurrentDate);
        carthash.put("currentTime", saveCurrentTime);
        carthash.put("discount" , "");
        //for user
        cartref.child("user view")
                .child(Prevalent.currentOnlineUsers.getPhone())
                .child("products").child(productId).updateChildren(carthash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //for admin
                    cartref.child("admin view")
                            .child(Prevalent.currentOnlineUsers.getPhone())
                            .child("products").child(productId).updateChildren(carthash).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Product_DetailsActivity.this, "product added to cart list", Toast.LENGTH_LONG).show();
                                Intent intent=new Intent(Product_DetailsActivity.this , HomeActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }

            }
        });

    }

    //get product details from home page
    private void getProductDetailsFromHomeAct() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("products");
        reference.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Product product=snapshot.getValue(Product.class);
                    details_product_name.setText(product.getpName());
                    details_product_description.setText(product.getDescription());
                    details_product_price.setText(product.getPrice());
                    Picasso.get().load(product.getImageUrl()).into(details_product_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //check if products you asked for is shipped or not
    //if not you cannot add more orders now
    private void checkShippingstate(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("orders").child(Prevalent.currentOnlineUsers.getPhone());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String shipping_state=snapshot.child("state").getValue().toString();
                    String name=snapshot.child("name").getValue().toString();
                    if (shipping_state.equals("shipped")){
                       state="order shipped";
                    }else if(shipping_state.equals("not shipped")){
                        state="order placed";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
