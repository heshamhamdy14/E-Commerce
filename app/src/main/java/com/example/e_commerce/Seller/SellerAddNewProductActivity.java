package com.example.e_commerce.Seller;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.example.e_commerce.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SellerAddNewProductActivity extends AppCompatActivity {
    private ImageView newProductImage;
    private EditText productName , productDescription , productPrice;
    private Button addNewProduct;
    String category;
    String pName  , pDescription , pPrice ;
    String saveCurrentDate , saveCurrentTime , productRandomKey;
    String getDownloadUrl;
    Uri imageUri;
    public static final int ImagePick=1;
    private StorageReference imageref;
    private DatabaseReference reference ;
    ProgressDialog progressDialog;
    String sname ,sphone,saddress,semail,sid;
    DatabaseReference selref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_new_product);

        category=getIntent().getExtras().get("category").toString();
        imageref= FirebaseStorage.getInstance().getReference().child("product images");
        reference=FirebaseDatabase.getInstance().getReference().child("products");
        selref=FirebaseDatabase.getInstance().getReference().child("sellers");


        newProductImage=findViewById(R.id.product_image);
        productName=findViewById(R.id.product_name);
        productDescription=findViewById(R.id.product_description);
        productPrice=findViewById(R.id.product_price);
        addNewProduct=findViewById(R.id.add_new_product);
        progressDialog=new ProgressDialog(this);

        newProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opengallery();
            }
        });

        addNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();
            }
        });

        sid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        selref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(sid).exists()){
                    sname=snapshot.child(sid).child("name").getValue().toString();
                    semail=snapshot.child(sid).child("email").getValue().toString();
                    sphone=snapshot.child(sid).child("phone").getValue().toString();
                    saddress=snapshot.child(sid).child("address").getValue().toString();
                    sid=snapshot.child(sid).child("sid").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void opengallery() {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent , ImagePick );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==ImagePick && resultCode==RESULT_OK && data!=null &&data.getData()!=null){
            imageUri=data.getData();
            newProductImage.setImageURI(imageUri);
        }
    }

    private void validateProductData() {
        pName=productName.getText().toString();
        pDescription=productDescription.getText().toString();
        pPrice=productPrice.getText().toString();
        if (imageUri==null){
            Toast.makeText(this, "product image is mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(pName)){
            Toast.makeText(this, "please add product name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pDescription)){
            Toast.makeText(this, "please add product Description", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(pPrice)){
            Toast.makeText(this, "please add product price", Toast.LENGTH_SHORT).show();
        }else{
            storeProductInformation();
        }
    }

    private void storeProductInformation(){
        progressDialog.setTitle("uploading Product");
        progressDialog.setMessage("Dear Admin : Please wait while uploading product data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        //create calender to recognize product uploading date and time
        Calendar  calendar=Calendar.getInstance();
        SimpleDateFormat currentDate =new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime =new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());
        productRandomKey=saveCurrentDate + saveCurrentTime;

        final StorageReference filepath=imageref.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
        UploadTask uploadTask=filepath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String error=e.toString();
                Toast.makeText(SellerAddNewProductActivity.this, "Error: "+error, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

        //convert imageUrl into string to save it in database
        Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw  task.getException();
                }
                getDownloadUrl=filepath.getDownloadUrl().toString();
                return filepath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    getDownloadUrl = task.getResult().toString();

                    Toast.makeText(SellerAddNewProductActivity.this, "got the Product image Url Successfully...", Toast.LENGTH_SHORT).show();
                    saveProductInfoToDatabase();
                }
            }
        });

    }

    private void saveProductInfoToDatabase(){
        HashMap<String , Object> producthash=new HashMap<>();
        producthash.put("pid" , productRandomKey);
        producthash.put("date" , saveCurrentDate);
        producthash.put("time" , saveCurrentTime);
        producthash.put("category" , category);
        producthash.put("imageUrl" , getDownloadUrl);
        producthash.put("pName" , pName);
        producthash.put("description" , pDescription);
        producthash.put("price" , pPrice);

        producthash.put("product_state" , "not approved");
        producthash.put("seller_name" , sname);
        producthash.put("seller_phone" , sphone);
        producthash.put("seller_email" ,semail );
        producthash.put("seller_id" , sid);
        producthash.put("seller_address" ,saddress );

        reference.child(productRandomKey).updateChildren(producthash)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(SellerAddNewProductActivity.this , SellerProductCategoryActivity.class));
                    progressDialog.dismiss();
                    Toast.makeText(SellerAddNewProductActivity.this, "Product is added successfully...", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.dismiss();
                    String exception=task.getException().toString();
                    Toast.makeText(SellerAddNewProductActivity.this, "Error: "+exception, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
