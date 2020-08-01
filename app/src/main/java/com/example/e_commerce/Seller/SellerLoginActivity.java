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

import com.example.e_commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SellerLoginActivity extends AppCompatActivity {


    EditText input_email, input_password ;
    Button login_btn ;
    FirebaseAuth auth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        auth=FirebaseAuth.getInstance();

        input_email=findViewById(R.id.seller_login_email);
        input_password=findViewById(R.id.seller_login_password);
        login_btn=findViewById(R.id.seller_login_btn);
        progressDialog=new ProgressDialog(this);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSeller();
            }
        });
    }

    private void loginSeller() {
        final String email=input_email.getText().toString();
        String password=input_password.getText().toString();
        if (email.equals("")&&password.equals("")){
            Toast.makeText(this, "you have to fill the form", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setTitle("create seller account");
            progressDialog.setMessage("please wait while checking credentials ...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            auth.signInWithEmailAndPassword(email , password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(SellerLoginActivity.this, "registration successfully..", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(SellerLoginActivity.this , SellerHomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }

    }

}
