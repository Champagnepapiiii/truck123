package com.mondaybs.monday.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mondaybs.monday.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class SellerAddNewProductActivity extends AppCompatActivity {

    private String CategoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime;
    private Button AddNewProductButton;
    private ImageView InputProductImage;
    private EditText InputProductName, InputProductDescription, InputProductPrice;
    private String productRandomKey, downloadImageUrl;
    private StorageReference ProductImagesRef;
    private DatabaseReference ProductsRef, sellersRef;
    private Uri ImageUri;
    private ProgressDialog loadingBar;

    private String sName, sAddress, sPhone, sEmail, sID, sBusinessName;
    private Bitmap compressedImageFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_new_product);


            CategoryName = getIntent().getExtras().get("category").toString();
            ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
            ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
            sellersRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

            AddNewProductButton = (Button) findViewById(R.id.add_new_product);
            InputProductImage = (ImageView) findViewById(R.id.select_product_image);
            InputProductName = (EditText) findViewById(R.id.product_name);
            InputProductDescription = (EditText) findViewById(R.id.product_description);
            InputProductPrice = (EditText) findViewById(R.id.product_price);
            loadingBar = new ProgressDialog(this);


            InputProductImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(16, 9)
                            .start(SellerAddNewProductActivity.this);
                }
            });


            AddNewProductButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    ValidateProductData();
                }
            });

            sellersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                sName = dataSnapshot.child("name").getValue().toString();
                                sAddress = dataSnapshot.child("address").getValue().toString();
                                sPhone = dataSnapshot.child("phone").getValue().toString();
                                sEmail = dataSnapshot.child("email").getValue().toString();
                                sID = dataSnapshot.child("sid").getValue().toString();
                                sBusinessName = dataSnapshot.child("business_name").getValue().toString();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }


        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                    ImageUri = result.getUri();
                    InputProductImage.setImageURI(ImageUri);

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }

        private void ValidateProductData()
        {
            Description = InputProductDescription.getText().toString();
            Price = InputProductPrice.getText().toString();
            Pname = InputProductName.getText().toString();


            if (ImageUri == null)
            {
                Toast.makeText(this, "Product image is mandatory...", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(Description))
            {
                Toast.makeText(this, "Please write product description...", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(Price))
            {
                Toast.makeText(this, "Please write product Price...", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(Pname))
            {
                Toast.makeText(this, "Please write product name...", Toast.LENGTH_SHORT).show();
            }
            else
            {
                StoreProductInformation();
            }
        }



        private void StoreProductInformation()
        {
            loadingBar.setTitle("Add New Product");
            loadingBar.setMessage("Dear Admin, please wait while we are adding the new product.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            saveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currentTime.format(calendar.getTime());

            productRandomKey = saveCurrentDate + saveCurrentTime;


            final StorageReference filePath_ref = ProductImagesRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".jpg");



            File newImageFile = new File(ImageUri.getPath());
            try {
                compressedImageFile = new Compressor(SellerAddNewProductActivity.this)
                        .setMaxHeight(1080)
                        .setMaxWidth(1080)
                        .setQuality(75)
                        .compressToBitmap(newImageFile);

            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageData = baos.toByteArray();

            // PHOTO UPLOAD

            final UploadTask filePath = filePath_ref.putBytes(imageData);
            filePath.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    String message = e.toString();
                    Toast.makeText(SellerAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Toast.makeText(SellerAddNewProductActivity.this, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                    Task<Uri> urlTask = filePath.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                        {
                            if (!task.isSuccessful())
                            {
                                throw task.getException();
                            }

                            downloadImageUrl = filePath_ref.getDownloadUrl().toString();
                            return filePath_ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task)
                        {
                            if (task.isSuccessful())
                            {
                                downloadImageUrl = task.getResult().toString();

                                Toast.makeText(SellerAddNewProductActivity.this, "got the Product image Url Successfully...", Toast.LENGTH_SHORT).show();

                                SaveProductInfoToDatabase();
                            }
                        }
                    });
                }
            });
        }



        private void SaveProductInfoToDatabase()
        {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", productRandomKey);
            productMap.put("date", saveCurrentDate);
            productMap.put("time", saveCurrentTime);
            productMap.put("description", Description);
            productMap.put("image", downloadImageUrl);
            productMap.put("category", CategoryName);
            productMap.put("price", Price);
            productMap.put("pname", Pname);


            productMap.put("sellerName", sName);
            productMap.put("sellerAddress", sAddress);
            productMap.put("sellerEmail", sEmail);
            productMap.put("sellerPhone", sPhone);
            productMap.put("sID", sID);
            productMap.put("business_name", sBusinessName);
            //used to check whether the admin has approved has approved the product that the seller wants user to see
            productMap.put("productState", "Not Approved");


            ProductsRef.child(productRandomKey).updateChildren(productMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {

                                Intent intent = new Intent(SellerAddNewProductActivity.this, SellerHomeActivity.class);
                                startActivity(intent);

                                loadingBar.dismiss();
                                Toast.makeText(SellerAddNewProductActivity.this, "Product is added successfully..", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                loadingBar.dismiss();
                                String message = task.getException().toString();
                                Toast.makeText(SellerAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

}
