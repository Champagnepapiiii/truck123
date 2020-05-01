package com.mondaybs.monday.Buyers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mondaybs.monday.Prevalent.Prevalent;
import com.mondaybs.monday.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;


public class OtpActivity extends AppCompatActivity implements TextWatcher {
    private static final String VERIFICATION_IN_PROCESS = "verification_process_key";
    private static final String PHONE_NUMBER = "phone_number";
    TextInputEditText otpEt1,otpEt2,otpEt3,otpEt4,otpEt5,otpEt6;
    Button loginBtn;


    String mainOtpText;
    private String parentDbName = "Users";
    FirebaseUser currentUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dr;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String TAG = OtpActivity.class.getSimpleName();
    private Handler mHandler;
    private Runnable autoDetectionModeDisableRunnable = new Runnable() {
        @Override
        public void run() {

        }
    };


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            verificationInProgress = false;
            Log.d(TAG, "onVerificationCompleted:" + credential);
            mHandler.removeCallbacks(autoDetectionModeDisableRunnable);
            signInWithPhoneAuthCredential(credential);

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            verificationInProgress = false;
            Log.w(TAG, "onVerificationFailed", e);
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
            } else if (e instanceof FirebaseTooManyRequestsException) {

            }

        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {

            hideSendingOtpProgressDialog();
            Log.d(TAG, "onCodeSent:" + verificationId);

            if (mHandler != null) {
                mHandler.postDelayed(autoDetectionModeDisableRunnable, 5000);
            }

            mVerificationId = verificationId;
            mResendToken = token;

        }
    };
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private boolean verificationInProgress;
    private String phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_activity);
        otpEt1 = findViewById(R.id.et_otp_1);
        otpEt2 = findViewById(R.id.et_otp_2);
        otpEt3 = findViewById(R.id.et_otp_3);
        otpEt4 = findViewById(R.id.et_otp_4);
        otpEt5 = findViewById(R.id.et_otp_5);
        otpEt6 = findViewById(R.id.et_otp_6);
        loginBtn = findViewById(R.id.vbtn);

        Paper.init(this);
        firebaseDatabase  = FirebaseDatabase.getInstance();
        dr = firebaseDatabase.getReference().child("profile_all_details");


        init();
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                attemptLogin();

            }
        });



        otpEt1.addTextChangedListener(this);
        otpEt2.addTextChangedListener(this);
        otpEt3.addTextChangedListener(this);
        otpEt4.addTextChangedListener(this);
        otpEt5.addTextChangedListener(this);
        otpEt6.addTextChangedListener(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.getBoolean(VERIFICATION_IN_PROCESS, verificationInProgress);
        outState.putString(PHONE_NUMBER, phone_number);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        verificationInProgress = savedInstanceState.getBoolean(VERIFICATION_IN_PROCESS);
        phone_number = savedInstanceState.getString(PHONE_NUMBER);

    }

    private void init() {


        mHandler = new Handler();

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }

        mAuth = FirebaseAuth.getInstance();
        phone_number = getIntent().getStringExtra("mobile");



    }

    private void attemptLogin() {
        if(otpEt1.getText().toString().isEmpty() || otpEt2.getText().toString().isEmpty()
                || otpEt3.getText().toString().isEmpty() || otpEt4.getText().toString().isEmpty()
                || otpEt5.getText().toString().isEmpty() || otpEt6.getText().toString().isEmpty()) {
            Toast.makeText(this, "enter the code", Toast.LENGTH_SHORT).show();
        }else{
            mainOtpText = otpEt1.getText().toString()+otpEt2.getText().toString()+otpEt3.getText().toString()+otpEt4.getText().toString()+otpEt5.getText().toString()+otpEt6.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, mainOtpText);
            signInWithPhoneAuthCredential(credential);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!verificationInProgress) {
            verifyPhoneNumber(phone_number);
        }
    }

    private void showSendingOtpProgressDialog(String msg) {
        if (progressDialog != null) {
            progressDialog.setCancelable(false);
            progressDialog.setMessage(msg);
            progressDialog.show();
        }
    }

    private void hideSendingOtpProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void verifyPhoneNumber(String phone) {

        verificationInProgress = true;


        showSendingOtpProgressDialog("Sending OTP to your number...");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {

        showSendingOtpProgressDialog("Logging You in...");

        if (mAuth != null) {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");

                                FirebaseUser user = task.getResult().getUser();
                                //save user phone to pref (keeping before important for any sync operation)
                                Log.d(TAG, "saving user phone " + user.getPhoneNumber());
                                //PreferenceManager.getInstance().setCurrentUserPhone(user.getPhoneNumber());
                                //PreferenceManager.getInstance().setLoggedIn(true);
                                currentUser = FirebaseAuth.getInstance().getCurrentUser();

                                saveDataToDatabase(phone_number);

                            } else {
                                // Sign in failed, display a message and update the UI
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    // The verification code entered was invalid
                                    hideSendingOtpProgressDialog();
                                    Toast.makeText(OtpActivity.this, "Invalid OTP", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }
    }



    private void navigateToHomePage() {
            hideSendingOtpProgressDialog();
            Intent intent = new Intent(OtpActivity.this,HomeActivity.class);
            intent.putExtra("admin","not admin");
            startActivity(intent);
            finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 1) {
            if (otpEt1.length() == 1) {
                otpEt2.requestFocus();
            }

            if (otpEt2.length() == 1) {
                otpEt3.requestFocus();
            }
            if (otpEt3.length() == 1) {
                otpEt4.requestFocus();
            }
            if (otpEt4.length() == 1) {
                otpEt5.requestFocus();
            }
            if (otpEt5.length() == 1) {
                otpEt6.requestFocus();
            }
        } else if (s.length() == 0) {
            if (otpEt6.length() == 0) {
                otpEt5.requestFocus();
            }
            if (otpEt5.length() == 0) {
                otpEt4.requestFocus();
            }
            if (otpEt4.length() == 0) {
                otpEt3.requestFocus();
            }
            if (otpEt3.length() == 0) {
                otpEt2.requestFocus();
            }
            if (otpEt2.length() == 0) {
                otpEt1.requestFocus();
            }
        }
    }


    private void saveDataToDatabase(final String phone)
    {

            Paper.book().write(Prevalent.UserPhoneKey, phone);


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();


        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                Prevalent.currentOnlineUser = phone;
                if (!dataSnapshot.child(parentDbName).child(phone).exists())
                {

                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone", phone);
                    userdataMap.put("password", " ");
                    userdataMap.put("name", " ");
                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                      navigateToHomePage();

                                    }
                                    else
                                    {
                                        Toast.makeText(OtpActivity.this, "Network Error: Please try again after some time...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else{
                    navigateToHomePage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

