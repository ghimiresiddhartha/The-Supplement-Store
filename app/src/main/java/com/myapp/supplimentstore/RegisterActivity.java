package com.myapp.supplimentstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Range;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import io.alterac.blurkit.BlurLayout;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private AwesomeValidation awesomeValidation;
    private static View systemUIView;
    BlurLayout blurLayout;
    EditText mName, mEmail, mPassword, mPhone, mAge, cEmail, cPassword;
    TextView mUpload, mLogin;
    ImageView mProfile;
    Button mRegister;
    ProgressBar mProgressBar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        systemUIView = getWindow().getDecorView();
        hideSystemUI();
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            if (visibility == 0) {
                                hideSystemUI();
                            }
                        }
                    });
        }
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageUpload();
        initializeComponents();
        validation();
        buttonEvent();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });


    }

    private void ImageUpload(){
        mProfile = findViewById(R.id.registerProfile);
        mUpload = findViewById(R.id.uploadProfile);
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(RegisterActivity.this)
                        .galleryOnly()
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .start();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();
        mProfile.setImageURI(uri);
    }

    public void hideSystemUI() {
        systemUIView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void initializeComponents(){
        blurLayout = findViewById(R.id.blurLayout);
        mName = findViewById(R.id.fullName);
        mEmail = findViewById(R.id.registerEmail);
        cEmail = findViewById(R.id.confirmEmail);
        cPassword = findViewById(R.id.confirmPassword);
        mPassword = findViewById(R.id.registerPassword);
        mPhone = findViewById(R.id.phone);
        mRegister = findViewById(R.id.btnRegister);
        //mAge = findViewById(R.id.age);
        mProgressBar = findViewById(R.id.progressBar2);
        mLogin = findViewById(R.id.directForward);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }

    public void validation(){
        String patternName = "^[A-Za-z\\s]{5,}$";
        String patternPassword = ".{8,}";
        String patternPhone = "^[0-9]{2}[0-9]{8}$";

        awesomeValidation.addValidation(this, R.id.fullName, patternName, R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.registerEmail, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.confirmEmail, R.id.registerEmail, R.string.confirm_email_error);
        awesomeValidation.addValidation(this, R.id.registerPassword, patternPassword, R.string.passworderror);
        awesomeValidation.addValidation(this, R.id.confirmPassword, R.id.registerPassword, R.string.confirm_password_error);
        awesomeValidation.addValidation(this, R.id.phone, patternPhone, R.string.phoneerror);
        //awesomeValidation.addValidation(this, R.id.age, Range.closed(20, 60), R.string.ageerror);

    }

    private void buttonEvent(){
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void submitForm(){
        if (awesomeValidation.validate()){
            final String email = cEmail.getText().toString().trim();
            String password = cPassword.getText().toString().trim();
            final String fullName = mName.getText().toString();
            final String phone = mPhone.getText().toString();

            mProgressBar.setVisibility(View.VISIBLE);

            // register the user in firebase
            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        // send verification link
                        FirebaseUser fuser = fAuth.getCurrentUser();
                        fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RegisterActivity.this, "Please verify your email to continue.", Toast.LENGTH_SHORT).show();
                                mName.setText("");
                                mEmail.setText("");
                                cEmail.setText("");
                                mPassword.setText("");
                                cPassword.setText("");
                                mPhone.setText("");
                                mProgressBar.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                            }
                        });

                        userID = fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = fStore.collection("users").document(userID);
                        Map<String,Object> user = new HashMap<>();
                        user.put("fName",fullName);
                        user.put("email",email);
                        user.put("phone",phone);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " + e.toString());
                            }
                        });
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        mProgressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}