package com.mondaybs.monday.Buyers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.mondaybs.monday.R;

public class PhoneActivity extends AppCompatActivity {

    EditText mobile;
    Button button;
    String no;
    ImageView btnFb,btnGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        mobile = (EditText) findViewById(R.id.pno);

        button = (Button) findViewById(R.id.bt_continue);
        btnFb = findViewById(R.id.btn_fb);
        btnGoogle = findViewById(R.id.btn_google);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                no = mobile.getText().toString();
                validNo(no);

                //Toast.makeText(PhoneActivity.this,no,Toast.LENGTH_LONG).show();
            }
        });


        btnFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PhoneActivity.this, "not available", Toast.LENGTH_SHORT).show();
            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PhoneActivity.this, "not available", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validNo(String no){
        if(no.isEmpty() || no.length() < 10){
            mobile.setError("Valid Number Is Required");
            mobile.requestFocus();
        }
        else{
            Intent intent = new Intent(PhoneActivity.this,OtpActivity.class);
            intent.putExtra("mobile","+91"+no);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
