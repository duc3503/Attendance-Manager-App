package com.example.attendancemanager;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.attendancemanager.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;


public class StudentModificationActivity extends AppCompatActivity {
    DatabaseReference reference, reference1;
    FirebaseAuth auth;
    EditText id, name, etBirthday, phone_number, etEmail;
    TextInputEditText etPassword;
    TextInputLayout editPassword;
    RadioGroup gender, role;
    RadioButton gender_male, gender_female, teacher, student;
    MaterialButton setBirthday, addStudent, deleteStudent, editStudent, fingerprint_button;
    ImageButton back_button;
    TextView title;
    String Fingerprint_id;
    ProgressDialog progressDialog;
    ValueEventListener event, event1;
    String admin_email, admin_password;

    boolean checkForEmpty(RadioGroup gender, EditText birthday, EditText phone_number) {
        if (gender.getCheckedRadioButtonId() == -1) {
            Toast toast = Toast.makeText(this, "Please choose user's gender", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }
        if (birthday.getText().toString().equals("")) {
            Toast toast = Toast.makeText(this, "Please enter user's birthday", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }
        if (phone_number.getText().toString().equals("")) {
            phone_number.setError("Please enter user's phone number");
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_modification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.modify_student), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        reference = FirebaseDatabase.getInstance().getReference().child("Users/" + getIntent().getStringExtra("userID"));
        reference1 = FirebaseDatabase.getInstance().getReference().child("Pending");
        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        id = (EditText) findViewById(R.id.etID);
        name = (EditText) findViewById(R.id.etName);
        gender = (RadioGroup) findViewById(R.id.gender_radio_group);
        etBirthday = (EditText) findViewById(R.id.etBirthday);
        phone_number = (EditText) findViewById(R.id.etPhone);
        setBirthday = findViewById(R.id.setBirthday);
        addStudent = findViewById(R.id.addStudentbtn);
        deleteStudent = findViewById(R.id.deletebtn);
        editStudent = findViewById(R.id.editbtn);
        gender_male = (RadioButton) findViewById(R.id.gender_male);
        gender_female = (RadioButton) findViewById(R.id.gender_female);
        title = (TextView) findViewById(R.id.title);
        back_button = findViewById(R.id.back_button);
        fingerprint_button = findViewById(R.id.fingerprint_button);
        role = findViewById(R.id.role_radio_group);
        teacher = findViewById(R.id.teacher);
        student = findViewById(R.id.student);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        editPassword = findViewById(R.id.editPassword);


        title.setText("User's detail");
        title.setEnabled(true);
        addStudent.setText("SAVE");
        addStudent.setVisibility(View.GONE);
        setBirthday.setVisibility(View.GONE);
        fingerprint_button.setVisibility(View.GONE);

        id.setEnabled(false);
        name.setEnabled(false);
        gender.clearCheck();
        gender_male.setEnabled(false);
        gender_female.setEnabled(false);
        role.clearCheck();
        teacher.setEnabled(false);
        student.setEnabled(false);
        etEmail.setEnabled(false);
        etPassword.setEnabled(false);
        etBirthday.setEnabled(false);
        phone_number.setEnabled(false);

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

        reference.addValueEventListener(event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("ID").getValue(String.class) != null)
                    id.setText(snapshot.child("ID").getValue(String.class));

                if (snapshot.child("Role").getValue(String.class) != null) {
                    if (snapshot.child("Role").getValue(String.class).equals("Teacher"))
                        teacher.setChecked(true);
                    else if (snapshot.child("Role").getValue(String.class).equals("Student"))
                        student.setChecked(true);
                    else {
                        teacher.setChecked(false);
                        student.setChecked(false);
                    }
                }

                if (snapshot.child("Name").getValue(String.class) != null)
                    name.setText(snapshot.child("Name").getValue(String.class));

                if (snapshot.child("Gender").getValue(String.class) != null) {
                    if (snapshot.child("Gender").getValue(String.class).equals("Male"))
                        gender_male.setChecked(true);
                    else if (snapshot.child("Gender").getValue(String.class).equals("Female"))
                        gender_female.setChecked(true);
                    else {
                        gender_male.setChecked(false);
                        gender_female.setChecked(false);
                    }
                }

                if (snapshot.child("Dob").getValue(String.class) != null)
                    etBirthday.setText(snapshot.child("Dob").getValue(String.class));

                if (snapshot.child("Phone").getValue(String.class) != null)
                    phone_number.setText(snapshot.child("Phone").getValue(String.class));

                if (snapshot.child("Email").getValue(String.class) != null)
                    etEmail.setText(snapshot.child("Email").getValue(String.class));

                if (snapshot.child("Password").getValue(String.class) != null)
                    etPassword.setText(snapshot.child("Password").getValue(String.class));

                if (snapshot.child("FingerprintID").getValue(String.class) != null)
                    Fingerprint_id = snapshot.child("FingerprintID").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are getting
                // the instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting
                // our day, month and year.
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // on below line we are creating a variable for date picker dialog.
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // on below line we are passing context.
                        StudentModificationActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                etBirthday.setText(String.format("%1$02d", dayOfMonth) + "/" + String.format("%1$02d", (monthOfYear + 1)) + "/" + String.format("%1$04d", year));
                            }
                        },
                        // on below line we are passing year,
                        // month and day for selected date in our date picker.
                        year, month, day);
                // at last we are calling show to
                // display our date picker dialog.
                datePickerDialog.show();
            }
        });

        addStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkForEmpty(gender, etBirthday, phone_number)) {
                    String gender_text;
                    String role_text;
                    if (gender_male.isChecked())
                        gender_text = "Male";
                    else
                        gender_text = "Female";
                    if (teacher.isChecked())
                        role_text = "Teacher";
                    else
                        role_text = "Student";

                    User user = new User(getIntent().getStringExtra("userID"), id.getText().toString(), role_text, name.getText().toString(),
                            gender_text, etBirthday.getText().toString(), phone_number.getText().toString(), etEmail.getText().toString(),
                            etPassword.getText().toString(), Fingerprint_id);
                    updateUser(user);

                    Intent intent = new Intent(StudentModificationActivity.this, StudentModificationActivity.class);
                    intent.putExtra("userID", user.getUserID());
                    startActivity(intent);
                    finish();
                }
            }

        });

        fingerprint_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkForEmpty(gender, etBirthday, phone_number)) {
                    String gender_text;
                    String role_text;
                    if (gender_male.isChecked())
                        gender_text = "Male";
                    else
                        gender_text = "Female";
                    if (teacher.isChecked())
                        role_text = "Teacher";
                    else
                        role_text = "Student";
                    User user = new User(getIntent().getStringExtra("userID"), id.getText().toString(), role_text, name.getText().toString(),
                            gender_text, etBirthday.getText().toString(), phone_number.getText().toString(), etEmail.getText().toString(),
                            etPassword.getText().toString(), Fingerprint_id);

                    // upload student to Database/Pending for adding fingerprint
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Pending");
                    reference1.child("Type").setValue("modify");
                    reference1.child("FingerprintID").setValue(Fingerprint_id);
                    reference1.child("Status").setValue("pending");

                    Intent intent = StudentModificationActivity.this.getIntent(user);
                    ProgressDialog dialog = ProgressDialog.show(StudentModificationActivity.this, "Please wait...", "Connecting to database...",
                            true);
                    dialog.show();
                    new CountDownTimer(3000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            // You don't need anything here
                        }

                        public void onFinish() {
                            dialog.dismiss();
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                }
            }
        });

        deleteStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StudentModificationActivity.this);
                builder.setMessage("Are you sure you want to delete this student from the database?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteUser(getIntent().getStringExtra("userID"), id.getText().toString(), etEmail.getText().toString(), etPassword.getText().toString());
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //
                            }
                        });
                AlertDialog mDialog = builder.create();
                mDialog.show();
            }
        });

        editStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addStudent.setVisibility(View.VISIBLE);
                editStudent.setVisibility(View.GONE);
                deleteStudent.setVisibility(View.GONE);
                setBirthday.setVisibility(View.VISIBLE);
                fingerprint_button.setVisibility(View.VISIBLE);
                gender_male.setEnabled(true);
                gender_female.setEnabled(true);
                etBirthday.setEnabled(true);
                phone_number.setEnabled(true);
            }
        });


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @NonNull
    private Intent getIntent(User user) {
        Intent intent = new Intent(StudentModificationActivity.this, FingerprintPendingActivity.class);
        intent.putExtra("userID", user.getUserID());
        intent.putExtra("id", user.getID());
        intent.putExtra("role", user.getRole());
        intent.putExtra("name", user.getName());
        intent.putExtra("gender", user.getGender());
        intent.putExtra("dob", user.getDob());
        intent.putExtra("phone", user.getPhone());
        intent.putExtra("email", user.getEmail());
        intent.putExtra("password", user.getPassword());
        intent.putExtra("fingerprintID", user.getFingerprintID());
        return intent;
    }

    public void updateUser(User user) {
        reference.child("Gender").setValue(user.getGender());
        reference.child("Dob").setValue(user.getDob());
        reference.child("Phone").setValue(user.getPhone());
    }

    public void deleteUser(String userID, String id, String email, String password) {
        progressDialog.show();
        reference1.child("Type").setValue("delete");
        reference1.child("FingerprintID").setValue(Fingerprint_id);
        reference1.child("Status").setValue("pending");
        reference1.addValueEventListener(event1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Status").getValue(String.class) != null) {
                    if (snapshot.child("Status").getValue(String.class).equals("finish")) {
                        List<String> classList = new LinkedList<String>();
                        FirebaseDatabase.getInstance().getReference().child("Users/" + userID).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                // get List of Classes of UserID
                                if (dataSnapshot.child("Classes").hasChildren()) {
                                    for (DataSnapshot snapshot1 : dataSnapshot.child("Classes").getChildren()) {
                                        if (snapshot1.getKey() != null)
                                            classList.add(snapshot1.getKey());
                                    }
                                }
                                // delete User from Classes/
                                if (classList.size() > 0) {
                                    FirebaseDatabase.getInstance().getReference().child("Classes/").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                        @Override
                                        public void onSuccess(DataSnapshot dataSnapshot) {
                                            if (getIntent().getStringExtra("role").equals("Teacher")) {
                                                for (String s : classList) {
                                                    int num = Integer.valueOf(dataSnapshot.child(s + "/NumOfTeachers").getValue(String.class));
                                                    FirebaseDatabase.getInstance().getReference().child("Classes/" + s + "/Teachers/" + userID).removeValue();
                                                    num--;
                                                    FirebaseDatabase.getInstance().getReference().child("Classes/" + s + "/NumOfTeachers").setValue(String.valueOf(num));
                                                }
                                            } else if (getIntent().getStringExtra("role").equals("Student")) {
                                                for (String s : classList) {
                                                    int num = Integer.valueOf(dataSnapshot.child(s + "/NumOfStudents").getValue(String.class));
                                                    FirebaseDatabase.getInstance().getReference().child("Classes/" + s + "/Students/" + userID).removeValue();
                                                    num--;
                                                    FirebaseDatabase.getInstance().getReference().child("Classes/" + s + "/NumOfStudents").setValue(String.valueOf(num));
                                                }
                                            }
                                        }
                                    });
                                }
                                // delete User fingerprint from Fingerprints/
                                FirebaseDatabase.getInstance().getReference().child("Fingerprints/" + dataSnapshot.child("FingerprintID").getValue(String.class)).removeValue();
                                // delete User from Users/
                                FirebaseDatabase.getInstance().getReference().child("Users/" + userID).removeValue();
                                // delete User from FirebaseAuth
                                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        authResult.getUser().delete();
                                        auth.signInWithEmailAndPassword(admin_email, admin_password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                // Remove Pending/
                                                reference1.removeValue();

                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (reference != null && event != null) {
            reference.removeEventListener(event);
        }
        if (reference1 != null && event1 != null) {
            reference1.removeEventListener(event1);
        }
    }

}

