package com.example.attendancemanager;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

public class SubjectModificationActivity extends AppCompatActivity {
    DatabaseReference reference;
    EditText etSubject, etClassroom, etStartTime, etEndTime, etID;
    MaterialButton add_button, setStartTime, setEndTime, edit_button, delete_button;
    ImageButton back_button;
    LinearLayout teacher, student;
    TextView title, studentPreview, teacherPreview;
    Spinner spinner;
    ArrayList<String> teacherList, studentList;
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == 1)
                Toast.makeText(SubjectModificationActivity.this, "Update teacher list successfully", Toast.LENGTH_SHORT).show();
            if (o.getResultCode() == 0)
                Toast.makeText(SubjectModificationActivity.this, "Update student list successfully", Toast.LENGTH_SHORT).show();
        }
    });

    boolean checkForEmpty(EditText etSubject, EditText etClassroom, EditText etStartTime, EditText etEndTime) {
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
        return false;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_modification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.modify_subject), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reference = FirebaseDatabase.getInstance().getReference().child("Classes/" + getIntent().getStringExtra("classID"));

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
        studentPreview = findViewById(R.id.studentPreview);
        teacherPreview = findViewById(R.id.teacherPreview);

        title.setText("Class detail");
        title.setEnabled(true);
        add_button.setText("Save");
        add_button.setVisibility(View.GONE);
        setStartTime.setVisibility(View.GONE);
        setEndTime.setVisibility(View.GONE);

        etID.setEnabled(false);
        etSubject.setEnabled(false);
        etClassroom.setEnabled(false);
        spinner.setEnabled(false);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(SubjectModificationActivity.this, R.array.dayOfWeek_array, R.layout.item_spinner);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(adapter);

        // load subject data from Firebase Database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getKey() != null)
                    etID.setText(snapshot.getKey());
                if (snapshot.child("Name").getValue(String.class) != null)
                    etSubject.setText(snapshot.child("Name").getValue(String.class));
                if (snapshot.child("RoomID").getValue(String.class) != null)
                    etClassroom.setText(snapshot.child("RoomID").getValue(String.class));
                if (snapshot.child("StartTime").getValue(String.class) != null)
                    etStartTime.setText(snapshot.child("StartTime").getValue(String.class));
                if (snapshot.child("EndTime").getValue(String.class) != null)
                    etEndTime.setText(snapshot.child("EndTime").getValue(String.class));
                if (snapshot.child("DayOfWeek").getValue(String.class) != null)
                    if ((adapter.getPosition(snapshot.child("DayOfWeek").getValue(String.class))) != -1)
                        spinner.setSelection(adapter.getPosition(snapshot.child("DayOfWeek").getValue(String.class)));
                if (snapshot.child("NumOfTeachers").getValue(String.class) != null)
                    teacherPreview.setText(snapshot.child("NumOfTeachers").getValue(String.class));
                if (snapshot.child("NumOfStudents").getValue(String.class) != null)
                    studentPreview.setText(snapshot.child("NumOfStudents").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_button.setVisibility(View.GONE);
                edit_button.setVisibility(View.GONE);
                add_button.setVisibility(View.VISIBLE);
                etSubject.setEnabled(true);
                etClassroom.setEnabled(true);
                spinner.setEnabled(true);
                setStartTime.setVisibility(View.VISIBLE);
                setEndTime.setVisibility(View.VISIBLE);
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkForEmpty(etSubject, etClassroom, etStartTime, etEndTime)) {
                    LocalTime time1 = LocalTime.parse(etStartTime.getText().toString());
                    LocalTime time2 = LocalTime.parse(etEndTime.getText().toString());
                    // start time is greater than end time
                    if (time1.compareTo(time2) > 0) {
                        Toast.makeText(SubjectModificationActivity.this, "End time must be greater than Start time", Toast.LENGTH_SHORT).show();
                    } else {
                        reference.child("Name").setValue(etSubject.getText().toString());
                        reference.child("RoomID").setValue(etClassroom.getText().toString());
                        reference.child("StartTime").setValue(etStartTime.getText().toString());
                        reference.child("EndTime").setValue(etEndTime.getText().toString());
                        reference.child("DayOfWeek").setValue(spinner.getSelectedItem().toString());

                        Toast.makeText(SubjectModificationActivity.this, "Update successfully", Toast.LENGTH_SHORT).show();
                        add_button.setVisibility(View.GONE);
                        setStartTime.setVisibility(View.GONE);
                        setEndTime.setVisibility(View.GONE);
                        delete_button.setVisibility(View.VISIBLE);
                        edit_button.setVisibility(View.VISIBLE);
                        etSubject.setEnabled(false);
                        etClassroom.setEnabled(false);
                        spinner.setEnabled(false);

                    }
                }
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SubjectModificationActivity.this);
                builder.setMessage("Are you sure you want to delete this subject from the database?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(SubjectModificationActivity.this, "Delete successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(SubjectModificationActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // on below line we are setting selected time
                                // in our text view.
                                etStartTime.setText(String.format("%1$02d", hourOfDay) + ":" + String.format("%1$02d", minute));
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(SubjectModificationActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                // on below line we are setting selected time
                                // in our text view.
                                etEndTime.setText(String.format("%1$02d", hourOfDay) + ":" + String.format("%1$02d", minute));
                            }
                        }, 7, 30, false);
                // at last we are calling show to
                // display our time picker dialog.
                timePickerDialog.show();
            }
        });

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubjectModificationActivity.this, ClassParticipantModificationActivity.class);
                intent.putExtra("classID", etID.getText().toString());
                intent.putExtra("type", "Teacher");
                launcher.launch(intent);
            }
        });

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubjectModificationActivity.this, ClassParticipantModificationActivity.class);
                intent.putExtra("classID", etID.getText().toString());
                intent.putExtra("type", "Student");
                launcher.launch(intent);
            }
        });
    }
}
