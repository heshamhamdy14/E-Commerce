package com.example.e_commerce.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.e_commerce.R;
import com.example.e_commerce.Seller.SellerProductCategoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductActivity extends AppCompatActivity {

    ImageView imageView;
    EditText name , description , price;
    Button applyChanges_btn , delete_product_btn;
    String productId="";
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);

        productId=getIntent().getExtras().getString("pid");
        reference= FirebaseDatabase.getInstance().getReference().child("products").child(productId);

        imageView=findViewById(R.id.maintain_product_image);
        name=findViewById(R.id.maintain_product_name);
        description=findViewById(R.id.maintain_product_description);
        price=findViewById(R.id.maintain_product_price);
        applyChanges_btn=findViewById(R.id.apply_changes_to_product);
        delete_product_btn=findViewById(R.id.delete_this_product);

        displayDataOfTheSpecificProduct();

        applyChanges_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyChanges();
            }
        });

        delete_product_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_this_product();
            }
        });
    }

    private void delete_this_product() {
        reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AdminMaintainProductActivity.this, "Product Deleted Successfully.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminMaintainProductActivity.this , SellerProductCategoryActivity.class));
                    finish();
                }
            }
        });
    }

    private void applyChanges() {

        String productName=name.getText().toString();
        String productDescription=description.getText().toString();
        String productPrice=price.getText().toString();
        if (productName.equals("")){
            Toast.makeText(this, "Product Name Is Wanted", Toast.LENGTH_SHORT).show();
        }else  if (productDescription.equals("")){
            Toast.makeText(this, "Product Description Is Wanted", Toast.LENGTH_SHORT).show();
        }else  if (productPrice.equals("")){
            Toast.makeText(this, "Product Price Is Wanted", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String , Object> producthash=new HashMap<>();
            producthash.put("pid" , productId);
            producthash.put("pName" , productName);
            producthash.put("description" , productDescription);
            producthash.put("price" , productPrice);
            reference.updateChildren(producthash).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AdminMaintainProductActivity.this, "Product Updated Successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(AdminMaintainProductActivity.this , SellerProductCategoryActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }
    }

    private void displayDataOfTheSpecificProduct() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String pname=snapshot.child("pName").getValue().toString();
                    String pdescriptoin=snapshot.child("description").getValue().toString();
                    String pprice=snapshot.child("price").getValue().toString();
                    String pimage=snapshot.child("imageUrl").getValue().toString();

                    name.setText(pname);
                    description.setText(pdescriptoin);
                    price.setText(pprice);
                    Picasso.get().load(pimage).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
