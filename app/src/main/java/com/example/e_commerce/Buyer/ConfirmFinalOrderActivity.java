package com.example.e_commerce.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    EditText name_edittxt , phone_number_edittxt , address_edittxt , city_edittxt ;
    Button confirm_btn;
    String totalPrice="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalPrice=getIntent().getStringExtra("total price");
        Toast.makeText(this, "total price = "+totalPrice, Toast.LENGTH_SHORT).show();

        name_edittxt=findViewById(R.id.shipment_name);
        phone_number_edittxt=findViewById(R.id.shipment_phone_number);
        address_edittxt=findViewById(R.id.shipment_address);
        city_edittxt=findViewById(R.id.shipment_city);
        confirm_btn=findViewById(R.id.confirm_order_btn);

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();
            }
        });

    }

    private void checkFields() {
        if (TextUtils.isEmpty(name_edittxt.getText().toString())){
            Toast.makeText(this, "please provide your name", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(phone_number_edittxt.getText().toString())){
            Toast.makeText(this, "please provide your phone number", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(address_edittxt.getText().toString())){
            Toast.makeText(this, "please provide your address", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(city_edittxt.getText().toString())){
            Toast.makeText(this, "please provide your city", Toast.LENGTH_SHORT).show();
        }else {
            confirmOrder();
        }
    }

    private void confirmOrder() {
        String saveCurrentDate , saveCurrentTime;
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:MM:SS a");
        saveCurrentTime=currentTime.format(calendar.getTime());
        DatabaseReference orderref= FirebaseDatabase.getInstance().getReference().child("orders").child(Prevalent.currentOnlineUsers.getPhone());
        HashMap<String , Object>orderhash=new HashMap<>();
        orderhash.put("totalPrice",totalPrice);
        orderhash.put("name" ,name_edittxt.getText().toString());
        orderhash.put("phone" ,phone_number_edittxt.getText().toString());
        orderhash.put("address" ,address_edittxt.getText().toString());
        orderhash.put("city" ,city_edittxt.getText().toString());
        orderhash.put("date" ,saveCurrentDate);
        orderhash.put("time" ,saveCurrentTime);
        orderhash.put("state" ,"not shipped");

        orderref.updateChildren(orderhash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //remove products from cart activity
                    FirebaseDatabase.getInstance().getReference().child("cart list").child("user view")
                            .child(Prevalent.currentOnlineUsers.getPhone())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ConfirmFinalOrderActivity.this, "your order is successfully confirmed", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(ConfirmFinalOrderActivity.this , HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
