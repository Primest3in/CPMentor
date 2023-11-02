package com.example.cpyipee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText email, pass;
    private Button login;
    private TextView goSignup, mentorReg;
    private String em, pa;
    private ProgressDialog loader;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mentorReg = findViewById(R.id.textView2);
        email = (EditText) findViewById(R.id.emailId);
        pass = (EditText) findViewById(R.id.passId);
        login = (Button) findViewById(R.id.button);
        goSignup =(TextView) findViewById(R.id.textView1);
        em = email.getText().toString();
        pa = pass.getText().toString();
        mAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null) {
                    startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
                    finish();
                }
            }
        };


        mentorReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MentorRegActivity.class);
                startActivity(intent);
            }
        });

        goSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                em = email.getText().toString();
                pa = pass.getText().toString();
                if(TextUtils.isEmpty(em)) {
                    email.setError("Enter email");
                }
                if(TextUtils.isEmpty(pa)) {
                    pass.setError("Enter password");
                }
                if(!TextUtils.isEmpty(em) && !TextUtils.isEmpty(pa)) goLogin();
            }
        });


    }

    private void goLogin() {

        loader = new ProgressDialog(this);
        loader.setMessage("login in progress");
        loader.show();

        mAuth.signInWithEmailAndPassword(em, pa).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                    loader.dismiss();
                    finish();
                    Intent intent = new Intent(LoginActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Login failed" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    finish();
                    loader.dismiss();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(authStateListener);
    }
}