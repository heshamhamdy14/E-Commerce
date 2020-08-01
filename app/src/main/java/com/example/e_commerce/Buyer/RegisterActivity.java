package com.example.e_commerce.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.e_commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    Button register;
    EditText input_username , input_phone , input_password ;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        input_username=findViewById(R.id.register_user_name);
        input_phone=findViewById(R.id.register_phone_number);
        input_password=findViewById(R.id.register_password);
        register=findViewById(R.id.register_btn);
        progressDialog=new ProgressDialog(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_account();
            }
        });
    }

    private void create_account() {
        String name=input_username.getText().toString();
        String phone=input_phone.getText().toString();
        String password=input_password.getText().toString();
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "please write your name ....", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "please write your phone number ....", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "please write your password ....", Toast.LENGTH_SHORT).show();
        }else
        {
            progressDialog.setTitle("create account");
            progressDialog.setMessage("please wait while checking credentials ...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            validate_phone_number(name , phone, password);
        }

    }

    private void validate_phone_number(final String name , final String phone , final String password) {
        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!(snapshot.child("users").child(phone).exists())){
                    HashMap<String , Object> hashusers=new HashMap<>();
                    hashusers.put("phone", phone);
                    hashusers.put("name", name);
                    hashusers.put("password", password);
                    reference.child("users").child(phone).updateChildren(hashusers).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "congratulation , your account is already created", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
                            }
                            else {
                                Toast.makeText(RegisterActivity.this, "Error! : Network Error please try again another time", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "phone number is already existed please try again with another phone number", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
