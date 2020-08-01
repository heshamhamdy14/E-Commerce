package com.example.e_commerce.Seller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.e_commerce.Admin.AdminNewOrderActivity;
import com.example.e_commerce.Buyer.HomeActivity;
import com.example.e_commerce.Buyer.MainActivity;
import com.example.e_commerce.R;

public class SellerProductCategoryActivity extends AppCompatActivity {
    private ImageView tshirts , sports_tshirts , female_dresses , sweathers;
    private ImageView glasses , purses_bags , hats , shoes;
    private ImageView headphones , laptops , watches , mobiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product_category);




        tshirts=findViewById(R.id.t_shirts);
        sports_tshirts=findViewById(R.id.sports_t_shirts);
        female_dresses=findViewById(R.id.dresses);
        sweathers=findViewById(R.id.sweater);

        glasses=findViewById(R.id.glasses);
        purses_bags=findViewById(R.id.bags);
        hats=findViewById(R.id.hats_cabs);
        shoes=findViewById(R.id.shoess);

        headphones=findViewById(R.id.headphones);
        laptops=findViewById(R.id.laptops);
        watches=findViewById(R.id.watches);
        mobiles=findViewById(R.id.mobiles);

        tshirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerProductCategoryActivity.this , SellerAddNewProductActivity.class);
                intent.putExtra("category" , "tshirts");
                startActivity(intent);
            }
        });

        sports_tshirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerProductCategoryActivity.this , SellerAddNewProductActivity.class);
                intent.putExtra("category" , "sports_tshirts");
                startActivity(intent);
            }
        });
        female_dresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerProductCategoryActivity.this , SellerAddNewProductActivity.class);
                intent.putExtra("category" , "female_dresses");
                startActivity(intent);
            }
        });
        sweathers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerProductCategoryActivity.this , SellerAddNewProductActivity.class);
                intent.putExtra("category" , "sweathers");
                startActivity(intent);
            }
        });


        glasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerProductCategoryActivity.this , SellerAddNewProductActivity.class);
                intent.putExtra("category" , "glasses");
                startActivity(intent);
            }
        });
        purses_bags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerProductCategoryActivity.this , SellerAddNewProductActivity.class);
                intent.putExtra("category" , "purses_bags");
                startActivity(intent);
            }
        });
        hats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerProductCategoryActivity.this , SellerAddNewProductActivity.class);
                intent.putExtra("category" , "hats");
                startActivity(intent);
            }
        });
        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerProductCategoryActivity.this , SellerAddNewProductActivity.class);
                intent.putExtra("category" , "shoes");
                startActivity(intent);
            }
        });
        headphones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerProductCategoryActivity.this , SellerAddNewProductActivity.class);
                intent.putExtra("category" , "headphones");
                startActivity(intent);
            }
        });
        laptops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerProductCategoryActivity.this , SellerAddNewProductActivity.class);
                intent.putExtra("category" , "laptops");
                startActivity(intent);
            }
        });
        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerProductCategoryActivity.this , SellerAddNewProductActivity.class);
                intent.putExtra("category" , "watches");
                startActivity(intent);
            }
        });
        mobiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SellerProductCategoryActivity.this , SellerAddNewProductActivity.class);
                intent.putExtra("category" , "mobiles");
                startActivity(intent);
            }
        });




    }
}
