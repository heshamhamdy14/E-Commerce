package com.example.e_commerce.Buyer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText phoneNumber , question1 , question2;
    Button verify_btn;
    String check;
    TextView page_title , question_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        check=getIntent().getStringExtra("check");
        phoneNumber=findViewById(R.id.find_phone_number);
        question1=findViewById(R.id.question1);
        question2=findViewById(R.id.question2);
        verify_btn=findViewById(R.id.verify_btn);
        page_title=findViewById(R.id.page_title);
        question_title=findViewById(R.id.ask_for_answer);



    }

    @Override
    protected void onStart() {
        super.onStart();
        if (check.equals("sitting")){
            phoneNumber.setVisibility(View.GONE);
            page_title.setText("Set Questions");
            question_title.setText("Please set answers for  the following security questions");
            verify_btn.setText("Set");
            displayOldAnswers();
            verify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setAnswersToDatabase();
                }
            });

        }else if (check.equals("login")){
            verify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyUser();                }
            });

        }
    }



    //save answers to specific user in database
    private void setAnswersToDatabase() {
        String answer1=question1.getText().toString().toLowerCase();
        String answer2=question2.getText().toString().toLowerCase();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("users").child(Prevalent.currentOnlineUsers.getPhone());
        HashMap<String , Object>securityhash=new HashMap<>();
        securityhash.put("answer1" ,answer1);
        securityhash.put("answer2" ,answer2);
        reference.child("security questions").updateChildren(securityhash)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ResetPasswordActivity.this, "you have set security questions successfully.", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(ResetPasswordActivity.this ,HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void displayOldAnswers(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(Prevalent.currentOnlineUsers.getPhone())
                .child("security questions");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String ans1 = question1.getText().toString();
                    String ans2 = question2.getText().toString();
                    ans1 = snapshot.child("answer1").getValue().toString();
                    ans2 = snapshot.child("answer2").getValue().toString();
                    question1.setText(ans1);
                    question2.setText(ans2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    // check if answers if correct
    //if correct reset user password to new one
    private void verifyUser() {
        final String phone=phoneNumber.getText().toString();
        final String answer1=question1.getText().toString().toLowerCase();
        final String answer2=question2.getText().toString().toLowerCase();
        if (phone.equals("") && answer1.equals("") && answer2.equals("")){
            Toast.makeText(this, "you have to fill the form", Toast.LENGTH_SHORT).show();
        }else {
            final DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("users");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(phone).exists()){
                        if (snapshot.child(phone).hasChild("security questions")){
                            String ans1=snapshot.child(phone).child("security questions").child("answer1").getValue().toString();
                            String ans2=snapshot.child(phone).child("security questions").child("answer2").getValue().toString();
                            if (!ans1.equals(answer1)){
                                Toast.makeText(ResetPasswordActivity.this, "first answer is wrong", Toast.LENGTH_SHORT).show();
                            }else if (!ans2.equals(answer2)){
                                Toast.makeText(ResetPasswordActivity.this, "second answer is wrong", Toast.LENGTH_SHORT).show();
                            }else {
                                final AlertDialog.Builder builder=new AlertDialog.Builder(ResetPasswordActivity.this);
                                builder.setTitle("new password");
                                final EditText newPassword=new EditText(ResetPasswordActivity.this);
                                newPassword.setHint("write new password...");
                                builder.setView(newPassword);
                                builder.setPositiveButton("Verify", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        reference.child(phone).child("password").setValue(newPassword.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(ResetPasswordActivity.this, "your password changed successfully.", Toast.LENGTH_SHORT).show();
                                                    Intent intent=new Intent(ResetPasswordActivity.this , LoginActivity.class);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.show();
                            }
                        }
                    }else{
                        Toast.makeText(ResetPasswordActivity.this, "this phone number is not exists", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }
}
