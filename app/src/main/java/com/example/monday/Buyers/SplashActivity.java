package com.example.monday.Buyers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monday.Prevalent.Prevalent;
import com.example.monday.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private ImageView logo;
    private static int splashTimeOut=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logo = (ImageView)findViewById(R.id.logo);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser == null) {
                    Intent i = new Intent(SplashActivity.this,PhoneActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Prevalent.currentOnlineUser = currentUser.getPhoneNumber();
                    Intent i = new Intent(SplashActivity.this,HomeActivity.class);
                    i.putExtra("admin","not admin");
                    startActivity(i);
                    finish();
                }

            }
        },splashTimeOut);

        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.mysplashanimation);
        logo.startAnimation(myanim);
    }
}
