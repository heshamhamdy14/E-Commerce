package com.example.e_commerce.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.e_commerce.Buyer.HomeActivity;
import com.example.e_commerce.Buyer.MainActivity;
import com.example.e_commerce.R;
import com.example.e_commerce.Seller.SellerProductCategoryActivity;

public class AdminHomeActivity extends AppCompatActivity {

       private Button check_new_orders , logout , maintainProducts , checkApproveNewProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        check_new_orders=findViewById(R.id.check_orders_btn);
        logout=findViewById(R.id.logout_btn);
        maintainProducts=findViewById(R.id.maintain_product_btn);
        checkApproveNewProduct=findViewById(R.id.check_approve_products);


        maintainProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomeActivity.this , HomeActivity.class);
                intent.putExtra("admin", "admin");
                startActivity(intent);
            }
        });

        check_new_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomeActivity.this , AdminNewOrderActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomeActivity.this , MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        checkApproveNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AdminHomeActivity.this , AdminCheckApproveNewProductsActivity.class);
                startActivity(intent);
            }
        });
    }
}
