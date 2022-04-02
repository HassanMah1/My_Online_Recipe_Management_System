package com.example.fitrecipes.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.fitrecipes.Models.UserModel;
import com.example.fitrecipes.R;
import com.example.fitrecipes.Util.DatabaseHelper;
import com.example.fitrecipes.Util.HelperKeys;
import com.example.fitrecipes.Util.SessionManager;
import com.example.fitrecipes.Util.ValidationChecks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    EditText login_email, login_password;
    private FirebaseAuth myauth;
    Button btn_login;
    ValidationChecks validationChecks = new ValidationChecks();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myauth = FirebaseAuth.getInstance();
        final String userId = SessionManager.getStringPref(HelperKeys.USER_ID, getApplicationContext());
        if (!userId.equals("")) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailAddress = login_email.getText().toString().trim();
                String password = login_password.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                    login_email.setError("Email Address is Invalid");
                    login_email.requestFocus();
                    return;
                }

                if (!password.equals(password)) {
                    login_password.setError("Password Does not matches....");
                    login_password.requestFocus();
                    return;
                }
                myauth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(LoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                        } else {
                            Toast.makeText(LoginActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }

        });
        login_email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                login_email.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.edit_text_with_stroke));
                login_password.setBackground(ContextCompat.getDrawable(LoginActivity.this, R.drawable.edit_text_without_stroke));
                return false;
            }
        });
        login_password.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                login_email.setBackground(getDrawable(R.drawable.edit_text_without_stroke));
                login_password.setBackground(getDrawable(R.drawable.edit_text_with_stroke));
                return false;
            }
        });

        TextView tv_signUp = findViewById(R.id.tv_signUp);
        TextView tv_forgot_password = findViewById(R.id.tv_frogot_password);
        tv_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        tv_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_login_guest_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
     //           loginUser(LoginActivity.this, "", "", "");
            }
        });


    }

    public void dashBoardLogin(View view) {
        validationCheck();
    }

    private void validationCheck() {
        if ((validationChecks.validateAnyName(login_email, "Please Enter Email"))
                && (validationChecks.validateEmail(login_email, "Please Enter Valid Email"))
                && (validationChecks.validateAnyName(login_password, "Please Enter Password"))
        ) {

         //   loginUser(this, login_email.getText().toString(), login_password.getText().toString(), "user");
        }
    }

}
