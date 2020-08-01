package com.example.e_commerce.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.Model.Users;
import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.example.e_commerce.Seller.SellerHomeActivity;
import com.example.e_commerce.Seller.SellerRegistirationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button join , login;
    ProgressDialog progressDialog;
    String ParentDbName="users";
    TextView beSeller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        join=findViewById(R.id.main_join_btn);
        login=findViewById(R.id.main_login_btn);
        progressDialog=new ProgressDialog(this);
        beSeller=findViewById(R.id.want_to_be_seller);

        beSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , SellerRegistirationActivity.class));
            }
        });

        Paper.init(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , LoginActivity.class));
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this , RegisterActivity.class));
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null){
            Intent intent=new Intent(MainActivity.this , SellerHomeActivity.class);
            startActivity(intent);
            finish();
        }
        String UserPhoneKey=Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey=Paper.book().read(Prevalent.UserPasswordKey);
        if (UserPhoneKey != "" && UserPasswordKey != ""){
            if (!(TextUtils.isEmpty(UserPhoneKey)&&TextUtils.isEmpty(UserPasswordKey))){
                AllowAccessToUser(UserPhoneKey , UserPasswordKey);
            }
        }
    }

    private void AllowAccessToUser(final String phone , final String password){

        progressDialog.setTitle("Already logged");
        progressDialog.setMessage("please wait ....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(ParentDbName).child(phone).exists()){
                    Users users=snapshot.child(ParentDbName).child(phone).getValue(Users.class);
                    if (users.getPhone().equals(phone)){
                        if (users.getPassword().equals(password)){
                                progressDialog.dismiss();
                                Intent intent=new Intent(MainActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUsers=users;
                                startActivity(intent);
                                finish();
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Password is incorrect....", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Account with this "+phone+"number do not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
