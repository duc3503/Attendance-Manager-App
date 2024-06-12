package com.example.attendancemanager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class StudentAddActivity extends AppCompatActivity {
    DatabaseReference reference;
    EditText id, name, etBirthday, phone_number, etEmail;
    TextInputEditText etPassword;
    RadioGroup gender, role;
    RadioButton gender_male, gender_female, teacher, student;
    MaterialButton setBirthday, addStudent, deleteStudent, editStudent, fingerprint_button;
    ImageButton back_button;
    TextView title;
    List<String> specialCharactersInSolr = Arrays.asList(new String[]{
            "[", "]", ".", "#", "$"});
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    String fingerprint_id;
    ProgressDialog dialog;
    boolean checkForEmpty(EditText id, EditText name, RadioGroup gender, RadioGroup role, EditText birthday, EditText phone_number, EditText etEmail, TextInputEditText etPassword) {
        if (id.getText().toString().equals("")) {
            id.setError("Please enter ID");
            return true;
        }
        if (name.getText().toString().equals("")) {
            name.setError("Please enter user's name");
            return true;
        }
        if (gender.getCheckedRadioButtonId() == -1) {
            Toast toast = Toast.makeText(this, "Please choose user's gender", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }
        if (role.getCheckedRadioButtonId() == -1) {
            Toast toast = Toast.makeText(this, "Please choose user's role", Toast.LENGTH_SHORT);
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
        if (etEmail.getText().toString().equals("")) {
            etEmail.setError("Please enter user's email");
            return true;
        }
        if (etPassword.getText().toString().equals("")) {
            etPassword.setError("Please enter user's password");
            return true;
        }
        for (String str: specialCharactersInSolr) {
            if (id.getText().toString().contains(str)) {
                id.setError("ID must not contain special characters");
                return true;
            }
        }
        if (etPassword.getText().length() < 6) {
            etPassword.setError("Password must have at least 6 characters");
            return true;
        }
        if (!etEmail.getText().toString().matches(emailPattern)) {
            etEmail.setError("Please enter a valid email");
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
        reference = FirebaseDatabase.getInstance().getReference().child("Pending");

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

        title.setText("Add new user");
        title.setEnabled(true);
        addStudent.setText("ADD");
        editStudent.setVisibility(View.GONE);
        deleteStudent.setVisibility(View.GONE);
        fingerprint_button.setVisibility(View.GONE);

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
                        StudentAddActivity.this,
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
                FirebaseDatabase.getInstance().getReference().get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("Pending").hasChildren()) {
                            Toast.makeText(StudentAddActivity.this, "Please finish uploading fingerprint for previous student first", Toast.LENGTH_SHORT).show();
                        } else {
                            if (!checkForEmpty(id, name, gender, role, etBirthday, phone_number, etEmail, etPassword)) {
                                if (check_If_ID_And_Email_Is_Not_Existed(etEmail, id, dataSnapshot)) {
                                    for (int i = 1; i < 128; i++) {
                                        if (!dataSnapshot.child("Fingerprints").hasChild(String.valueOf(i))) {
                                            fingerprint_id = String.valueOf(i);
                                            break;
                                        }
                                    }
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


                                    // upload student to Database/Pending for adding fingerprint
                                    reference.child("Type").setValue("add");
                                    reference.child("FingerprintID").setValue(fingerprint_id);
                                    reference.child("Status").setValue("pending");

                                    dialog = ProgressDialog.show(StudentAddActivity.this, "Please wait...", "Connecting to database...",
                                            true);
                                    dialog.show();
                                    new CountDownTimer(3000, 1000) {

                                        public void onTick(long millisUntilFinished) {
                                            // You don't need anything here
                                        }

                                        public void onFinish() {
                                            dialog.dismiss();
                                            Intent intent = StudentAddActivity.this.getIntent(gender_text, role_text, fingerprint_id);
                                            startActivity(intent);
                                            //finish();
                                        }
                                    }.start();
                                }
                            }
                        }
                    }
                });
            }
        });


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public boolean check_If_ID_And_Email_Is_Not_Existed(EditText etEmail, EditText etID, DataSnapshot dataSnapshot) {
        String id = etID.getText().toString();
        String email = etEmail.getText().toString();
        final boolean[] existedEmail = {true};
        final boolean[] existedID = {true};
        for (DataSnapshot snapshot : dataSnapshot.child("Users").getChildren()) {
            if (snapshot.child("ID").getValue(String.class).equals(id)) {
                existedID[0] = false;
                Toast.makeText(StudentAddActivity.this, "This ID is already existed", Toast.LENGTH_SHORT).show();
                break;
            }
            if (snapshot.child("Email").getValue(String.class).equals(email)) {
                existedEmail[0] = false;
                Toast.makeText(StudentAddActivity.this, "This Email is already existed", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return (existedEmail[0] && existedID[0]);
    }

    @NonNull
    private Intent getIntent(String gender_text, String role_text, String fingerprint_id) {
        Intent intent = new Intent(StudentAddActivity.this, FingerprintPendingActivity.class);
        intent.putExtra("id", id.getText().toString());
        intent.putExtra("role", role_text);
        intent.putExtra("name", name.getText().toString());
        intent.putExtra("gender", gender_text);
        intent.putExtra("dob", etBirthday.getText().toString());
        intent.putExtra("phone", phone_number.getText().toString());
        intent.putExtra("email", etEmail.getText().toString());
        intent.putExtra("password", etPassword.getText().toString());
        intent.putExtra("fingerprintID", fingerprint_id);
        return intent;
    }


}


