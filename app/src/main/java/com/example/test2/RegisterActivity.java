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

public class RegisterActivity extends AppCompatActivity {

    private ImageView endAct;
    private TextView goToLogInAct;
    private Button createAccBtn;
    private EditText userName, userMail, userPass, cnfrmPass;
    private FirebaseAuth auth;
    ProgressDialog dialog;
    Dialog customDialog;
    private TextView mailId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        endAct = findViewById(R.id.endActImg);
        goToLogInAct = findViewById(R.id.goToLogIn);
        createAccBtn = findViewById(R.id.registerBtn);
        userName = findViewById(R.id.etRegName);
        userMail = findViewById(R.id.etRegEmail);
        userPass = findViewById(R.id.etRegPassword);
        cnfrmPass = findViewById(R.id.etCnfrmPassword);


        // Progress dialog
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading");
        dialog.setCancelable(false);

        // Custom Dialog
        customDialog = new Dialog(RegisterActivity.this);
        customDialog.setContentView(R.layout.register_dialog_layout);
        customDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.custom_dialog_background));
        customDialog.getWindow().setLayout(750, 650);
        customDialog.setCancelable(false);
        Button okay = customDialog.findViewById(R.id.okayBtn);
        mailId = customDialog.findViewById(R.id.mailId);

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        // Cancel login listener
        endAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });


        SpannableString content = new SpannableString(" Log In");
        content.setSpan(new UnderlineSpan(), 1, content.length(), 0);
        goToLogInAct.setText(content);


        //goToLogInAct
        goToLogInAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });


        auth = FirebaseAuth.getInstance();
        // Create Account Button
        createAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = userName.getText().toString();
                String mail = userMail.getText().toString();
                String pwd = userPass.getText().toString();
                String cnfrmPwd = cnfrmPass.getText().toString();

                if (name.isEmpty() || mail.isEmpty() || pwd.isEmpty() || cnfrmPwd.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Please enter every Field!", Toast.LENGTH_SHORT).show();
                } else if (pwd.length() < 8) {
                    userPass.setError("Password must be at least 8 characters! ");
                } else if (!pwd.equals(cnfrmPwd)) {
                    cnfrmPass.setError("Both passwords are not matching");
                } else {
                    dialog.show();


                    auth.createUserWithEmailAndPassword(mail, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mailId.setText(mail);
                                            dialog.dismiss();
                                            customDialog.show();
                                        } else {
                                            dialog.dismiss();
                                            Toast.makeText(RegisterActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            } else {
                                dialog.dismiss();
                                Toast.makeText(RegisterActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }
}