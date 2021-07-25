package com.myapp.supplimentstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


//class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
//
//    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
//        Toast.makeText(parent.getContext(),
//                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
//                Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> arg0) {
//        TODO Auto-generated method stub
//    }
//
//}

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    private static View systemUIView;
    private AwesomeValidation awesomeValidation;
    EditText hEmail, hPassword, mAge, mHeight, mWeight;
    TextView forgetPwd, cAccount, mSetting, mResultUnit, mActivity, textGoal;
    Button loginBtn, mCalculate;
    ProgressBar progressBar;
    CheckBox chkBox;
    RadioButton mMale, mFemale, mCalories, mKilojoules, mGain, mLose, mMaintain;
    RadioGroup mGender, mResult, mGoal;
    FirebaseAuth fAuth;
    private Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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


        initializeComponent();
        fAuth = FirebaseAuth.getInstance();
        eventLoginButton();
        eventForgetPassword();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        addValidationToViews();
        eventOnCheckBox();
        eventOnSetting();
        eventOnCalculate();

    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUI();
    }

    public void hideSystemUI() {
        systemUIView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public void eventForgetPassword(){
        forgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetEmail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email To Receive Verification Link.");
                passwordResetDialog.setView(resetEmail);
                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                            // extract the email and send reset line
                            resetEmail.getText().toString();
                            String mail = resetEmail.getText().toString();
                            fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(LoginActivity.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });

                passwordResetDialog.create().show();
            }
        });
    }
    public void eventOnCalculate(){
        mCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Your Diet Plan is ready. Please Sign In to view the result.", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void addValidationToViews(){
        String patternPassword = ".{8,}";

        awesomeValidation.addValidation(this, R.id.loginEmail, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        awesomeValidation.addValidation(this, R.id.loginPassword, patternPassword, R.string.passworderror);

    }
    private void initializeComponent(){
        hEmail = findViewById(R.id.loginEmail);
        hPassword = findViewById(R.id.loginPassword);
        forgetPwd = findViewById(R.id.forgetPassword);
        progressBar=findViewById(R.id.progressBar);
        loginBtn=findViewById(R.id.btnLog);
        chkBox = findViewById(R.id.dietCheckbox);
        mMale = findViewById(R.id.male);
        mFemale = findViewById(R.id.female);
        mAge = findViewById(R.id.age);
        mHeight = findViewById(R.id.height);
        mWeight = findViewById(R.id.weight);
        mCalories = findViewById(R.id.calories);
        mKilojoules = findViewById(R.id.kilojoules);
        mCalculate = findViewById(R.id.calculate);
        mSpinner = findViewById(R.id.spinner);
        mSetting = findViewById(R.id.dietSetting);
        mResultUnit=findViewById(R.id.textView4);
        mGender=findViewById(R.id.Gender);
        mResult = findViewById(R.id.result);
        mActivity=findViewById(R.id.acitivity);
        mGoal=findViewById(R.id.goal);
        mGain=findViewById(R.id.gain);
        mLose = findViewById(R.id.loss);
        mMaintain=findViewById(R.id.maintain);
        textGoal=findViewById(R.id.myGoal);

    }

    private void eventOnCheckBox(){
        chkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chkBox.isChecked()){
//                    mSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());
                    mGender.setVisibility(View.VISIBLE);
                    mMale.setVisibility(View.VISIBLE);
                    mFemale.setVisibility(View.VISIBLE);
                    mAge.setVisibility(View.VISIBLE);
                    mHeight.setVisibility(View.VISIBLE);
                    mWeight.setVisibility(View.VISIBLE);
                    mSpinner.setVisibility(View.VISIBLE);
                    mSetting.setVisibility(View.VISIBLE);
                    mCalculate.setVisibility(View.VISIBLE);
                    mActivity.setVisibility(View.VISIBLE);


                }else{
                    mGender.setVisibility(View.GONE);
                    mMale.setVisibility(View.GONE);
                    mFemale.setVisibility(View.GONE);
                    mAge.setVisibility(View.GONE);
                    mHeight.setVisibility(View.GONE);
                    mWeight.setVisibility(View.GONE);
                    mSpinner.setVisibility(View.GONE);
                    mSetting.setVisibility(View.GONE);
                    mResult.setVisibility(View.GONE);
                    mCalculate.setVisibility(View.GONE);
                    mResultUnit.setVisibility(View.GONE);
                    mGoal.setVisibility(View.GONE);
                    mActivity.setVisibility(View.GONE);
                    textGoal.setVisibility(View.GONE);
                    mGender.clearCheck();
                    mAge.setText("");
                    mHeight.setText("");
                    mWeight.setText("");
                    mResult.clearCheck();

                }
            }
        });
    }

    private void eventOnSetting(){
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResult.setVisibility(View.VISIBLE);
                mResultUnit.setVisibility(View.VISIBLE);
                mCalories.setVisibility(View.VISIBLE);
                mKilojoules.setVisibility(View.VISIBLE);
                mGoal.setVisibility(View.VISIBLE);
                mGain.setVisibility(View.VISIBLE);
                mLose.setVisibility(View.VISIBLE);
                mMaintain.setVisibility(View.VISIBLE);
                textGoal.setVisibility(View.VISIBLE);
            }
        });
    }

    private void eventLoginButton(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginForm();
            }
        });
    }
    private void loginForm(){
        if (awesomeValidation.validate()){
            String email = hEmail.getText().toString().trim();
            String password = hPassword.getText().toString().trim();
            progressBar.setVisibility(View.VISIBLE);
            fAuth = FirebaseAuth.getInstance();

            // authenticate the user
            fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser fuser = fAuth.getCurrentUser();
                        if(fuser.isEmailVerified()){
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            hEmail.setText("");
                            hPassword.setText("");
                            progressBar.setVisibility(View.GONE);
                        }else{
                            Toast.makeText(LoginActivity.this, "Please verify your email address.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }else {
                        Toast.makeText(LoginActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }

                }
            });

        }

    }

}