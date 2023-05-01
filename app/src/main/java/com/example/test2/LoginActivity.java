package com.example.test2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Allow users to sign up using their email address and password.
//Our SDKs also provide email address verification, password recovery and email address change primitives

public class LoginActivity extends AppCompatActivity {

    private ImageView endAct;
    private Button signInBtn;
    private EditText userMail, userPass;
    private FirebaseAuth auth;
    private TextView goToRegAct;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        endAct = findViewById(R.id.endAct);
        signInBtn = findViewById(R.id.loginBtn);
        userMail = findViewById(R.id.etEmail);
        userPass = findViewById(R.id.etPassword);
        goToRegAct = findViewById(R.id.goToSignUp);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setCancelable(false);


        // Cancel login listener
        endAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });


        auth = FirebaseAuth.getInstance();
        // Sign In listener
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                String userEmailId = userMail.getText().toString();
                String userPwd = userPass.getText().toString();
                if (userEmailId.isEmpty() || userPwd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter both login details!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {


                    auth.signInWithEmailAndPassword(userEmailId, userPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (auth.getCurrentUser().isEmailVerified()) {
                                    Toast.makeText(LoginActivity.this, "You are successfully logged in.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Please verify your email id", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        // code to Underline goToRegAct btn

        SpannableString content = new SpannableString(" Sign Up");
        content.setSpan(new UnderlineSpan(), 1, content.length(), 0);
        goToRegAct.setText(content);

        // Go to Create Account Act
        goToRegAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null && user.isEmailVerified()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            this.finish();
        }
    }
}