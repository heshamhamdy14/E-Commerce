package com.example.e_commerce.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.Admin.AdminHomeActivity;
import com.example.e_commerce.Seller.SellerProductCategoryActivity;
import com.example.e_commerce.Model.Users;
import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    EditText input_phone , input_password;
    Button login_btn;
    ProgressDialog progressDialog;
    CheckBox checkBoxRememberMe;
    TextView adminLink , notAdminLink , forgetPassword;
    String ParentDbname="users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        input_phone=findViewById(R.id.login_phone_number);
        input_password=findViewById(R.id.login_password);
        login_btn=findViewById(R.id.login_btn);
        progressDialog=new ProgressDialog(this);
        checkBoxRememberMe=findViewById(R.id.remember_me);
        adminLink=findViewById(R.id.iam_admin);
        notAdminLink=findViewById(R.id.iam_not_admin);
        forgetPassword=findViewById(R.id.forget_password);

        Paper.init(this);

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this , ResetPasswordActivity.class);
                intent.putExtra("check" , "login");
                startActivity(intent);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_user();
            }
        });

        adminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_btn.setText("Login Admin");
                adminLink.setVisibility(View.GONE);
                notAdminLink.setVisibility(View.VISIBLE);
                ParentDbname="admins";
            }
        });
        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_btn.setText("Login");
                adminLink.setVisibility(View.VISIBLE);
                notAdminLink.setVisibility(View.GONE);
                ParentDbname="users";
            }
        });

    }

    private void login_user() {
        String phone=input_phone.getText().toString();
        String password=input_password.getText().toString();
         if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "please write your phone number ....", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "please write your password ....", Toast.LENGTH_SHORT).show();
        }else{
             progressDialog.setTitle("login account");
             progressDialog.setMessage("please wait while checking credentials ....");
             progressDialog.setCanceledOnTouchOutside(false);
             progressDialog.show();
            allow_access_to_account(phone , password);
         }
    }

    private void allow_access_to_account(final String phone , final String password){

        if(checkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey , phone);
            Paper.book().write(Prevalent.UserPasswordKey , password);
        }

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(ParentDbname).child(phone).exists()){
                    Users users=snapshot.child(ParentDbname).child(phone).getValue(Users.class);
                    if (users.getPhone().equals(phone)){
                        if (users.getPassword().equals(password)){
                            if (ParentDbname=="users") {
                                Toast.makeText(LoginActivity.this, "logged in successfully....", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUsers=users;
                                startActivity(intent);
                                finish();
                            }else if (ParentDbname=="admins"){
                                Toast.makeText(LoginActivity.this, "Welcome Admin : logged in successfully....", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                                finish();
                            }
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Password is incorrect....", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Account with this "+phone+"number do not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
