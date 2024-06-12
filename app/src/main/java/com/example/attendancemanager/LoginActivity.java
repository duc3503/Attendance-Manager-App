package com.example.attendancemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    TextView logsignup;
    Button button;
    EditText email, password;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    android.app.ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logbutton);
        email = findViewById(R.id.editTexLogEmail);
        password = findViewById(R.id.editTextLogPassword);


        // Set the animation of the icon
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Go icon khi EditText dang focus va nguoc lai
                if (hasFocus) {
                    email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    return;
                }

                email.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_mail_outline_32, 0, 0, 0);
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // Go icon khi EditText dang focus va nguoc lai
                if (hasFocus) {
                    password.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    return;
                }

                password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_password_24, 0, 0, 0);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String pass = password.getText().toString();

                if ((TextUtils.isEmpty(Email))) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Enter The Email", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(pass)) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Enter The Password", Toast.LENGTH_SHORT).show();
                } else if (!Email.matches(emailPattern)) {
                    progressDialog.dismiss();
                    email.setError("Give Proper Email Address");
                } else if (password.length() < 6) {
                    progressDialog.dismiss();
                    password.setError("More Then Six Characters");
                    Toast.makeText(LoginActivity.this, "Password Needs To Be Longer Then Six Characters", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(Email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.show();
                                new CountDownTimer(1000, 1000) {

                                    public void onTick(long millisUntilFinished) {
                                        // You don't need anything here
                                    }

                                    public void onFinish() {
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }.start();
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
