package com.example.attendancemanager;


import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.attendancemanager.model.Subject;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class SubjectAddActivity extends AppCompatActivity {
    DatabaseReference reference;
    EditText etSubject, etClassroom, etStartTime, etEndTime, etID;
    MaterialButton add_button, setStartTime, setEndTime, edit_button, delete_button;
    LinearLayout teacher, student;
    ImageButton back_button;
    TextView title, teacherPreview, studentPreview;
    Spinner spinner;
    String selected;
    ArrayList<String> teacherList, studentList;
    List<String> specialCharactersInSolr = Arrays.asList(new String[]{
            "[", "]", ".", "#", "$"});
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == 1) {
                if (o.getData().getStringArrayListExtra("teacherList") != null)
                    teacherList = o.getData().getStringArrayListExtra("teacherList");
                if (teacherList != null)
                    teacherPreview.setText(teacherList.size() + " selected");
            }
            if (o.getResultCode() == 0) {
                if (o.getData().getStringArrayListExtra("studentList") != null)
                    studentList = o.getData().getStringArrayListExtra("studentList");
                if (studentList != null)
                    studentPreview.setText(studentList.size() + " selected");
            }
        }
    });

    boolean checkForEmpty(EditText etID, EditText etSubject, EditText etClassroom, EditText etStartTime, EditText etEndTime) {
        if (etID.getText().toString().equals("")){
            etID.setError("Please enter subject ID");
            return true;
        }
        if (etSubject.getText().toString().equals("")) {
            etSubject.setError("Please enter subject name");
            return true;
        }
        if (etClassroom.getText().toString().equals("")) {
            etClassroom.setError("Please enter classroom");
            return true;
        }
        if (etStartTime.getText().toString().equals("")) {
            etStartTime.setError("Please enter start time");
            return true;
        }
        if (etEndTime.getText().toString().equals("")) {
            etEndTime.setError("Please enter end time");
            return true;
        }
        for (String str: specialCharactersInSolr) {
            if (etID.getText().toString().contains(str)){
                etID.setError("Subject ID must not contain special characters");
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subject_modification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.modify_subject), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reference = FirebaseDatabase.getInstance().getReference().child("Classes/");

        // Load layout from XML
        etID = findViewById(R.id.etID);
        etSubject = findViewById(R.id.etSubject);
        etClassroom = findViewById(R.id.etClassroom);
        etStartTime = findViewById(R.id.etStartTime);
        etEndTime = findViewById(R.id.etEndTime);
        add_button = findViewById(R.id.add_button);
        edit_button = findViewById(R.id.editbtn);
        delete_button = findViewById(R.id.deletebtn);
        setStartTime = findViewById(R.id.setStartTime);
        setEndTime = findViewById(R.id.setEndTime);
        back_button = findViewById(R.id.back_button);
        title = findViewById(R.id.title);
        spinner = findViewById(R.id.spinner);
        teacher = findViewById(R.id.teacher);
        student = findViewById(R.id.student);
        teacherPreview = findViewById(R.id.teacherPreview);
        studentPreview = findViewById(R.id.studentPreview);

        title.setText("Add new class");
        title.setEnabled(true);
        add_button.setText("ADD");
        add_button.setVisibility(View.VISIBLE);
        setStartTime.setVisibility(View.VISIBLE);
        setEndTime.setVisibility(View.VISIBLE);
        edit_button.setVisibility(View.GONE);
        delete_button.setVisibility(View.GONE);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(SubjectAddActivity.this, R.array.dayOfWeek_array, R.layout.item_spinner);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(adapter);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkForEmpty(etID, etSubject, etClassroom, etStartTime, etEndTime)) {
                    LocalTime time1 = LocalTime.parse(etStartTime.getText().toString());
                    LocalTime time2 = LocalTime.parse(etEndTime.getText().toString());
                    // start time is greater than end time
                    if (time1.compareTo(time2) >= 0) {
                        Toast.makeText(SubjectAddActivity.this, "End time must be greater than Start time", Toast.LENGTH_SHORT).show();
                    } else {
                        reference.child(etID.getText().toString()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChildren()) {
                                    etID.setError("This subject ID exists already, please enter another one");
                                } else {
                                    // upload subject class to Database/Classes/
                                    reference.child(etID.getText().toString() + "/Name").setValue(etSubject.getText().toString());
                                    reference.child(etID.getText().toString() + "/RoomID").setValue(etClassroom.getText().toString());
                                    reference.child(etID.getText().toString() + "/StartTime").setValue(etStartTime.getText().toString());
                                    reference.child(etID.getText().toString() + "/EndTime").setValue(etEndTime.getText().toString());
                                    reference.child(etID.getText().toString() + "/DayOfWeek").setValue(spinner.getSelectedItem().toString());
                                    if (teacherList != null)
                                        reference.child(etID.getText().toString() + "/NumOfTeachers").setValue(String.valueOf(teacherList.size()));
                                    else
                                        reference.child(etID.getText().toString() + "/NumOfTeachers").setValue("0");
                                    if (studentList != null)
                                        reference.child(etID.getText().toString() + "/NumOfStudents").setValue(String.valueOf(studentList.size()));
                                    else
                                        reference.child(etID.getText().toString() + "/NumOfStudents").setValue("0");
                                    if (teacherList != null)
                                        for (String s : teacherList) {
                                            reference.child(etID.getText().toString() + "/Teachers/" + s).setValue("true");

                                            // upload subject class to Database/Users/UserID/Classes/
                                            FirebaseDatabase.getInstance().getReference().child("Users/" + s + "/Classes/" + etID.getText().toString()).setValue("True");
                                        }
                                    if (studentList != null)
                                        for (String s : studentList) {
                                            reference.child(etID.getText().toString() + "/Students/" + s).setValue("true");

                                            // upload subject class to Database/Users/UserID/Classes/
                                            FirebaseDatabase.getInstance().getReference().child("Users/" + s + "/Classes/" + etID.getText().toString()).setValue("True");
                                        }


                                    Toast.makeText(SubjectAddActivity.this, "Add new subject successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    }
                }
            }
        });


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on below line we are getting the
                // instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting our hour, minute.
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // on below line we are initializing our Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(SubjectAddActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // on below line we are setting selected time
                                // in our text view.
                                etStartTime.setText((String.format("%1$02d", hourOfDay) + ":" + String.format("%1$02d", minute)).toString());
                            }
                        }, 7, 30, false);
                // at last we are calling show to
                // display our time picker dialog.
                timePickerDialog.show();
            }
        });

        setEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on below line we are getting the
                // instance of our calendar.
                final Calendar c = Calendar.getInstance();

                // on below line we are getting our hour, minute.
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // on below line we are initializing our Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(SubjectAddActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // on below line we are setting selected time
                                // in our text view.
                                etEndTime.setText((String.format("%1$02d", hourOfDay) + ":" + String.format("%1$02d", minute)).toString());
                            }
                        }, 7, 30, false);
                // at last we are calling show to
                // display our time picker dialog.
                timePickerDialog.show();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selected = parent.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubjectAddActivity.this, ClassParticipantAddActivity.class);
                intent.putExtra("classID", getIntent().getStringExtra("classID"));
                intent.putExtra("type", "Teacher");
                launcher.launch(intent);
            }
        });

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubjectAddActivity.this, ClassParticipantAddActivity.class);
                intent.putExtra("classID", getIntent().getStringExtra("classID"));
                intent.putExtra("type", "Student");
                launcher.launch(intent);
            }
        });
    }

}
