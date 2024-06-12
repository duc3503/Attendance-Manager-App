package com.example.attendancemanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.attendancemanager.adapter.AttendanceAdapter;
import com.example.attendancemanager.adapter.SubjectAdapter;
import com.example.attendancemanager.model.Attendance;
import com.example.attendancemanager.model.CustomComparator;
import com.example.attendancemanager.model.Subject;
import com.example.attendancemanager.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DateDetailActivity extends AppCompatActivity {
    ImageButton back_button;
    TextView title;
    PieChart pieChart;
    ListView attendance_listview;
    EditText etSearch;
    List<Attendance> teacherList, studentList, attendanceList;

    DatabaseReference class_reference, attendance_reference, user_reference;
    ValueEventListener event;
    Integer num_of_presence, num_of_absent;
    Boolean teacherList_wait, studentList_wait;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_date_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        class_reference = FirebaseDatabase.getInstance().getReference().child("Classes/" + getIntent().getStringExtra("classID"));
        attendance_reference = FirebaseDatabase.getInstance().getReference().child("Attendance/" + getIntent().getStringExtra("classID") +
                "/" + getIntent().getStringExtra("date"));
        user_reference = FirebaseDatabase.getInstance().getReference().child("Users/");

        // load layout from XML
        back_button = findViewById(R.id.back_button);
        title = findViewById(R.id.title);
        pieChart = findViewById(R.id.pieChart);
        attendance_listview = findViewById(R.id.attendance_listview);
        etSearch = findViewById(R.id.etSearch);

        teacherList = new LinkedList<Attendance>();
        studentList = new LinkedList<Attendance>();
        attendanceList = new LinkedList<Attendance>();

        title.setText(getIntent().getStringExtra("classID") + " (" + getIntent().getStringExtra("date") + ")");

        AttendanceAdapter adapter = new AttendanceAdapter(this, attendanceList);
        attendance_listview.setAdapter(adapter);

        attendance_reference.addValueEventListener(event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot attendance_snapshot) {
                class_reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot class_snapshot) {
                        user_reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot user_snapshot) {
                                teacherList.clear();
                                studentList.clear();
                                attendanceList.clear();
                                for (DataSnapshot teacher_snapshot : class_snapshot.child("Teachers").getChildren()) {
                                    if (attendance_snapshot.child(teacher_snapshot.getKey() + "/NumOfTime").getValue(String.class) != null) {
                                        Attendance attendance = new Attendance(teacher_snapshot.getKey(), user_snapshot.child(teacher_snapshot.getKey() + "/ID").getValue(String.class),
                                                user_snapshot.child(teacher_snapshot.getKey() + "/Name").getValue(String.class), Integer.valueOf(attendance_snapshot.child(teacher_snapshot.getKey() + "/NumOfTime").getValue(String.class)));
                                        if (attendance != null)
                                            teacherList.add(attendance);
                                    } else {
                                        Attendance attendance = new Attendance(teacher_snapshot.getKey(), user_snapshot.child(teacher_snapshot.getKey() + "/ID").getValue(String.class),
                                                user_snapshot.child(teacher_snapshot.getKey() + "/Name").getValue(String.class), 0);
                                        if (attendance != null)
                                            teacherList.add(attendance);
                                    }
                                }
                                for (DataSnapshot student_snapshot : class_snapshot.child("Students").getChildren()) {
                                    if (attendance_snapshot.child(student_snapshot.getKey() + "/NumOfTime").getValue(String.class) != null) {
                                        Attendance attendance = new Attendance(student_snapshot.getKey(), user_snapshot.child(student_snapshot.getKey() + "/ID").getValue(String.class),
                                                user_snapshot.child(student_snapshot.getKey() + "/Name").getValue(String.class), Integer.valueOf(attendance_snapshot.child(student_snapshot.getKey() + "/NumOfTime").getValue(String.class)));
                                        if (attendance != null)
                                            teacherList.add(attendance);
                                    } else {
                                        Attendance attendance = new Attendance(student_snapshot.getKey(), user_snapshot.child(student_snapshot.getKey() + "/ID").getValue(String.class),
                                                user_snapshot.child(student_snapshot.getKey() + "/Name").getValue(String.class), 0);
                                        if (attendance != null)
                                            teacherList.add(attendance);
                                    }
                                }
                                attendanceList.addAll(teacherList);
                                attendanceList.addAll(studentList);
                                num_of_presence = 0;
                                num_of_absent = 0;
                                for (Attendance attendance : attendanceList) {
                                    if (attendance.getNumOfTime() <= 0) {
                                        num_of_absent++;
                                    } else {
                                        num_of_presence++;
                                    }
                                }
                                adapter.setArraylist(attendanceList);
                                adapter.notifyDataSetChanged();
                                pieChart.invalidate();
                                pieChart.clearChart();
                                pieChart.addPieSlice(new PieModel("Presence",num_of_presence,
                                        Color.parseColor("#00FF00")));
                                pieChart.addPieSlice(new PieModel("Absence",num_of_absent,
                                        Color.parseColor("#FF0000")));
                                pieChart.startAnimation();
                            }
                        });
                    }
                });
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

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filter(etSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // do nothing
            }
        });

        attendance_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(DateDetailActivity.this, TimeDetailActivity.class);
                intent.putExtra("classID", getIntent().getStringExtra("classID"));
                intent.putExtra("date", getIntent().getStringExtra("date"));
                intent.putExtra("userID", attendanceList.get(i).getUserID());
                intent.putExtra("ID", attendanceList.get(i).getId());
                intent.putExtra("Name", attendanceList.get(i).getName());
                startActivity(intent);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (attendance_reference != null && event != null) {
            attendance_reference.removeEventListener(event);
        }
    }
}
