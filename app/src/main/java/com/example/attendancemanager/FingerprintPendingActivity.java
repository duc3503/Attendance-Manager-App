package com.example.attendancemanager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FingerprintPendingActivity extends AppCompatActivity {
    ImageButton back_icon;
    TextView description, fingerprint_upload;
    DatabaseReference reference;
    FirebaseAuth auth;
    ValueEventListener event1;
    String admin_email, admin_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_pending);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        reference = FirebaseDatabase.getInstance().getReference().child("Pending");
        auth = FirebaseAuth.getInstance();

        back_icon = (ImageButton)findViewById(R.id.back_icon);
        description = (TextView)findViewById(R.id.description);
        fingerprint_upload = (TextView)findViewById(R.id.fingerprint_upload);

        FirebaseDatabase.getInstance().getReference().child("Users/" + auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Role").getValue(String.class) != null) {
                    if (snapshot.child("Role").getValue(String.class).equals("Admin")) {
                        admin_email = snapshot.child("Email").getValue(String.class);
                        admin_password = snapshot.child("Password").getValue(String.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        reference.addValueEventListener(event1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Status").getValue(String.class) != null) {
                    if (snapshot.child("Status").getValue(String.class).equals("finish") && snapshot.child("Type").getValue(String.class).equals("add")) {
                        // create new user account with entered email and password
                        auth.createUserWithEmailAndPassword(getIntent().getStringExtra("email"), getIntent().getStringExtra("password")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                String userID = task.getResult().getUser().getUid();
                                // Add current fingerprint id from Pending/Fingerprint_id to Fingerprints/
                                if (snapshot.child("FingerprintID").getValue(String.class) != null)
                                    FirebaseDatabase.getInstance().getReference().child("Fingerprints/" + snapshot.child("FingerprintID").getValue(String.class)).setValue(userID);
                                // Add new user to Users/
                                DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users/" + userID + "/");
                                reference1.child("ID").setValue(getIntent().getStringExtra("id"));
                                reference1.child("Role").setValue(getIntent().getStringExtra("role"));
                                reference1.child("Name").setValue(getIntent().getStringExtra("name"));
                                reference1.child("Gender").setValue(getIntent().getStringExtra("gender"));
                                reference1.child("Dob").setValue(getIntent().getStringExtra("dob"));
                                reference1.child("Phone").setValue(getIntent().getStringExtra("phone"));
                                reference1.child("Email").setValue(getIntent().getStringExtra("email"));
                                reference1.child("Password").setValue(getIntent().getStringExtra("password"));
                                reference1.child("FingerprintID").setValue(getIntent().getStringExtra("fingerprintID"));


                                // Remove value in Pending/
                                reference.removeValue();

                                ProgressDialog dialog =  new ProgressDialog(FingerprintPendingActivity.this);
                                dialog.setMessage("Please wait...");
                                if(!FingerprintPendingActivity.this.isFinishing()) {
                                    dialog.show();
                                    Toast.makeText(FingerprintPendingActivity.this, "Added new user successfully", Toast.LENGTH_LONG).show();
                                }

                                auth.signOut();
                                auth.signInWithEmailAndPassword(admin_email, admin_password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        dialog.dismiss();
                                        Intent intent = new Intent(FingerprintPendingActivity.this, MainActivity.class);
                                        intent.putExtra("fragment", "user");
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        });
                    } else if (snapshot.child("Status").getValue(String.class).equals("finish") && snapshot.child("Type").getValue(String.class).equals("modify")) {
                        // Update user in Users/userID/
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users/" + getIntent().getStringExtra("userID") + "/");
                        reference1.child("ID").setValue(getIntent().getStringExtra("id"));
                        reference1.child("Role").setValue(getIntent().getStringExtra("role"));
                        reference1.child("Name").setValue(getIntent().getStringExtra("name"));
                        reference1.child("Gender").setValue(getIntent().getStringExtra("gender"));
                        reference1.child("Dob").setValue(getIntent().getStringExtra("dob"));
                        reference1.child("Phone").setValue(getIntent().getStringExtra("phone"));
                        reference1.child("Email").setValue(getIntent().getStringExtra("email"));
                        reference1.child("Password").setValue(getIntent().getStringExtra("password"));
                        reference1.child("FingerprintID").setValue(getIntent().getStringExtra("fingerprintID"));

                        // Remove value in Pending/
                        reference.removeValue();

                        finish();
                    } else if (snapshot.child("Status").getValue(String.class).equals("confirm")) {
                        ProgressDialog dialog =  new ProgressDialog(FingerprintPendingActivity.this);
                        dialog.setMessage("Please wait...");
                        if(!FingerprintPendingActivity.this.isFinishing()) {
                            dialog.show();
                            Toast.makeText(FingerprintPendingActivity.this, "Upload successfully!\nPlease confirm your fingerprint", Toast.LENGTH_LONG).show();
                        }

                        new CountDownTimer(1000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                // You don't need anything here
                            }

                            public void onFinish() {
                                dialog.dismiss();
                                fingerprint_upload.setText("Fingerprint Confirm");
                                description.setText("Please put your finger on the fingerprint sensor one more time");
                            }
                        }.start();
                    } else if (snapshot.child("Status").getValue(String.class).equals("unmatch")) {
                        ProgressDialog dialog =  new ProgressDialog(FingerprintPendingActivity.this);
                        dialog.setMessage("Please wait...");
                        if(!FingerprintPendingActivity.this.isFinishing()) {
                            dialog.show();
                            Toast.makeText(FingerprintPendingActivity.this, "Fingerprint confirmation is unmatched, please upload your fingerprint again", Toast.LENGTH_LONG).show();
                        }

                        new CountDownTimer(1000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                // You don't need anything here
                            }

                            public void onFinish() {
                                reference.child("Status").setValue("pending");
                                dialog.dismiss();
                                fingerprint_upload.setText("Fingerprint Upload");
                                description.setText("Please put your finger on the fingerprint sensor");
                            }
                        }.start();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        back_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("Pending").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChildren()) {
                            FirebaseDatabase.getInstance().getReference().child("Pending").removeValue();
                            finish();
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseDatabase.getInstance().getReference().child("Pending").removeValue();
        finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (reference!= null && event1!= null) {
            reference.removeEventListener(event1);
        }
    }
}
