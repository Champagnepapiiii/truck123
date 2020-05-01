package com.mondaybs.monday.Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.mondaybs.monday.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SellerLoginActivity extends AppCompatActivity {

    private Button loginSellerBtn;
    private ProgressDialog loadingBar;
    private TextInputEditText emailInput, passwordInput;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.seller_login_email);
        passwordInput = findViewById(R.id.seller_login_password);
        loginSellerBtn = findViewById(R.id.seller_login_btn);

        loadingBar = new ProgressDialog(this);

        loginSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSeller();
            }
        });
    }

    private void loginSeller() {
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();

        if (!email.equals("") && !password.equals("")) {
            loadingBar.setTitle("Seller Account Login");
            loadingBar.setMessage("Please wait, while we are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            final DatabaseReference RootRef;
            RootRef = FirebaseDatabase.getInstance().getReference();
            final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("Sellers").child(currentUser.getUid()).exists()) {
                        if (dataSnapshot.child("Sellers").child(currentUser.getUid()).child("password").getValue().toString().equals(password)) {
                            if (dataSnapshot.child("Sellers").child(currentUser.getUid()).child("email").getValue().toString().equals(email)) {
                                Intent intent = new Intent(SellerLoginActivity.this, SellerHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                            }
                            else{
                                loadingBar.dismiss();
                                Toast.makeText(SellerLoginActivity.this, "invalid user", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(SellerLoginActivity.this, "invalid user", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        loadingBar.dismiss();
                        Toast.makeText(SellerLoginActivity.this, "invalid user", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else{
            Toast.makeText(this, "Please write your email and password first", Toast.LENGTH_SHORT).show();
        }
    }

}

