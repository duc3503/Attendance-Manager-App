package com.example.attendancemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.attendancemanager.adapter.SubjectAdapter;
import com.example.attendancemanager.model.Subject;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class ListAllClassActivity extends AppCompatActivity {
    ImageButton back_button;
    EditText etSearch;
    TextView title, selected_class;
    List<Subject> subjectList;
    ListView subject_listview;
    DatabaseReference reference, room_reference;
    ValueEventListener event, event1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_all_class);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Connecting to sensor...");

        reference = FirebaseDatabase.getInstance().getReference().child("Classes/");
        room_reference = FirebaseDatabase.getInstance().getReference().child("Select_subject/" + getIntent().getStringExtra("roomID"));
        subjectList = new LinkedList<Subject>();

        // load layout from XML
        back_button = findViewById(R.id.back_button);
        etSearch = findViewById(R.id.etSearch);
        subject_listview = findViewById(R.id.class_listview);
        title = findViewById(R.id.title);
        selected_class = findViewById(R.id.selected_class);

        title.setText(getIntent().getStringExtra("roomID"));

        SubjectAdapter adapter = new SubjectAdapter(this, subjectList);
        subject_listview.setAdapter(adapter);

        reference.addValueEventListener(event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subjectList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Subject subject = new Subject(dataSnapshot.getKey(), dataSnapshot.child("Name").getValue(String.class),
                            dataSnapshot.child("RoomID").getValue(String.class), dataSnapshot.child("StartTime").getValue(String.class),
                            dataSnapshot.child("EndTime").getValue(String.class), dataSnapshot.child("DayOfWeek").getValue(String.class),
                            dataSnapshot.child("NumOfStudents").getValue(String.class), dataSnapshot.child("NumOfTeachers").getValue(String.class));
                    if (subject != null)
                        subjectList.add(subject);
                }
                adapter.setArraylist(subjectList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        room_reference.addValueEventListener(event1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Status").getValue(String.class) != null)
                    if (snapshot.child("Status").getValue(String.class).equals("using")) {
                        dialog.dismiss();
                        selected_class.setText(snapshot.child("ClassID").getValue(String.class));
                    }
                    else
                        selected_class.setText("null");
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

        subject_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!selected_class.equals(subjectList.get(i).getClassID())) {
                    // Upload class to Database/Select_subject
                    room_reference.child("ClassID").setValue(subjectList.get(i).getClassID());
                    room_reference.child("Status").setValue("pending");
                    
                    dialog.show();
                }
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (reference != null && event != null)
            reference.removeEventListener(event);
        finish();
    }
}