package com.example.e_commerce.Seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.e_commerce.Buyer.MainActivity;
import com.example.e_commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SellerRegistirationActivity extends AppCompatActivity {

    EditText input_name , input_phone , input_email, input_password, input_address ;
    Button register_btn , login_page_btn;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registiration);

        auth=FirebaseAuth.getInstance();

        input_name=findViewById(R.id.seller_name);
        input_phone=findViewById(R.id.seller_phone_number);
        input_email=findViewById(R.id.seller_email);
        input_password=findViewById(R.id.seller_password);
        input_address=findViewById(R.id.seller_shop_address);
        register_btn=findViewById(R.id.seller_register_btn);
        progressDialog=new ProgressDialog(this);

        login_page_btn=findViewById(R.id.seller_have_account);
        login_page_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SellerRegistirationActivity.this , SellerLoginActivity.class));
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerseller();
            }
        });
    }

    private void registerseller() {
        final String name=input_name.getText().toString();
        final String phone=input_phone.getText().toString();
        final String email=input_email.getText().toString();
        String password=input_password.getText().toString();
        final String address=input_address.getText().toString();

        auth.createUserWithEmailAndPassword(email ,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.setTitle("create seller account");
                    progressDialog.setMessage("please wait while checking credentials ...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    DatabaseReference sellerref= FirebaseDatabase.getInstance().getReference();
                    String sId=auth.getUid();
                    HashMap<String , Object>sellerHash=new HashMap<>();
                    sellerHash.put("sid" , sId);
                    sellerHash.put("name" , name);
                    sellerHash.put("phone" , phone);
                    sellerHash.put("email" , email);
                    sellerHash.put("address" , address);

                    sellerref.child("sellers").child(sId).updateChildren(sellerHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(SellerRegistirationActivity.this, "registration successfully..", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(SellerRegistirationActivity.this , SellerHomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                }
            }
        });
    }
}
