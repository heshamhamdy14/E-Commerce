package com.example.e_commerce.Buyer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.Prevalent.Prevalent;
import com.example.e_commerce.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SittingActivity extends AppCompatActivity {

    TextView close , save_profile_changes , change_profile_image;
    EditText fullname , phone_number , address;
    CircleImageView profile_picture;
    Uri imageUri ;
    String checker;
    StorageReference imageref= FirebaseStorage.getInstance().getReference().child("profile pictures");
    StorageTask uploadtask;
    ProgressDialog progressDialog;
    Button setSecurityQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitting);

        fullname=findViewById(R.id.sitting_update_name);
        phone_number=findViewById(R.id.sitting_update_phone_number);
        address=findViewById(R.id.sitting_update_address);
        close=findViewById(R.id.close_sitting);
        save_profile_changes=findViewById(R.id.update_profile);
        change_profile_image=findViewById(R.id.change_image);
        profile_picture=findViewById(R.id.sitting_profile_image);
        setSecurityQuestions=findViewById(R.id.set_security_question_btn);
        progressDialog = new ProgressDialog(this);

        displayuserinfo(profile_picture , fullname , phone_number , address);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save_profile_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker=="clicked"){
                    saveAllInfoToDatabase();
                }else{
                    saveDataInfo();
                }
            }
        });

        change_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1 ,1)
                        .start(SittingActivity.this);

            }
        });

        setSecurityQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SittingActivity.this , ResetPasswordActivity.class);
                intent.putExtra("check" , "sitting");
                startActivity(intent);
            }
        });
    }

    private void saveDataInfo() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
        HashMap<String, Object> userhash = new HashMap<>();
        userhash.put("name", fullname.getText().toString());
        userhash.put("phone", phone_number.getText().toString());
        userhash.put("address", address.getText().toString());
        reference.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(userhash);
        Toast.makeText(SittingActivity.this, "information updated successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(SittingActivity.this , HomeActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data != null){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            profile_picture.setImageURI(imageUri);
        }else{
            Toast.makeText(this, "Error: Try Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SittingActivity.this , SittingActivity.class));
            finish();
        }
    }

    private void saveAllInfoToDatabase() {

        if (TextUtils.isEmpty(fullname.getText().toString())) {
            Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone_number.getText().toString())) {
            Toast.makeText(this, "phone number is mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(address.getText().toString())) {
            Toast.makeText(this, "address is mandatory", Toast.LENGTH_SHORT).show();
        } else {

            progressDialog.setTitle("update");
            progressDialog.setMessage("please wait while we are updating your account information");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            if (imageUri != null) {
                final StorageReference fileref = imageref.child(Prevalent.currentOnlineUsers.getPhone() + ".jpg");
                uploadtask = fileref.putFile(imageUri);
                uploadtask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return fileref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadurl = task.getResult();
                            String imageurl = downloadurl.toString();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
                            HashMap<String, Object> userhash = new HashMap<>();
                            userhash.put("name", fullname.getText().toString());
                            userhash.put("phone", phone_number.getText().toString());
                            userhash.put("address", address.getText().toString());
                            userhash.put("image", imageurl);
                            reference.child(Prevalent.currentOnlineUsers.getPhone()).updateChildren(userhash);
                            progressDialog.dismiss();
                            Toast.makeText(SittingActivity.this, "information updated successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SittingActivity.this , HomeActivity.class));
                            finish();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(SittingActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                Toast.makeText(this, "image is not selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void displayuserinfo(final CircleImageView circleImageView , final EditText editTextfullname , final EditText editTextohonenumber , final EditText editTextaddress) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("users").child(Prevalent.currentOnlineUsers.getPhone());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.child("image").exists()){
                        String image=snapshot.child("image").getValue().toString();
                        String name=snapshot.child("name").getValue().toString();
                        String phone=snapshot.child("phone").getValue().toString();
                        String address=snapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(circleImageView);
                        editTextfullname.setText(name);
                        editTextohonenumber.setText(phone);
                        editTextaddress.setText(address);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
