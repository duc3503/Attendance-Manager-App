package com.example.attendancemanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.attendancemanager.adapter.DateAdapter;
import com.example.attendancemanager.adapter.SubjectAdapter;
import com.example.attendancemanager.model.CustomComparator;
import com.example.attendancemanager.model.DateStringComparator;
import com.example.attendancemanager.model.Subject;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class AttendanceFragment extends Fragment {

    EditText etSearch;
    ListView subject_listview;
    DatabaseReference reference;
    FirebaseAuth auth;
    List<Subject> subjectList;
    List<String> subjectIDList;

    String selected, role;

    public AttendanceFragment(String selected) {
        this.selected = selected;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users/" + auth.getCurrentUser().getUid());
        subjectList = new LinkedList<Subject>();
        subjectIDList = new LinkedList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);

        etSearch = view.findViewById(R.id.etSearch);
        subject_listview = view.findViewById(R.id.subject_listview);

        SubjectAdapter subjectAdapter = new SubjectAdapter(getContext(), subjectList);
        subject_listview.setAdapter(subjectAdapter);


        // load date from firebase database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Role").getValue(String.class) != null) {
                    role = snapshot.child("Role").getValue(String.class);
                    if (snapshot.child("Role").getValue(String.class).equals("Admin")) {
                        FirebaseDatabase.getInstance().getReference().child("Classes").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot snapshot1) {
                                subjectList.clear();
                                for (DataSnapshot dataSnapshot : snapshot1.getChildren()) {
                                    if (dataSnapshot.child("DayOfWeek").getValue(String.class) != null) {
                                        if (dataSnapshot.child("DayOfWeek").getValue(String.class).equals(selected)) {
                                            Subject subject = new Subject(dataSnapshot.getKey(), dataSnapshot.child("Name").getValue(String.class),
                                                    dataSnapshot.child("RoomID").getValue(String.class), dataSnapshot.child("StartTime").getValue(String.class),
                                                    dataSnapshot.child("EndTime").getValue(String.class), dataSnapshot.child("DayOfWeek").getValue(String.class),
                                                    dataSnapshot.child("NumOfStudents").getValue(String.class), dataSnapshot.child("NumOfTeachers").getValue(String.class));
                                            if (subject != null)
                                                subjectList.add(subject);
                                        }
                                    }
                                }
                                if (subjectList.size() > 0) {
                                    Collections.sort(subjectList, new CustomComparator());
                                }
                                subjectAdapter.setArraylist(subjectList);
                                subjectAdapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        subjectIDList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.child("Classes/").getChildren()) {
                            if (dataSnapshot.getKey() != null)
                                subjectIDList.add(dataSnapshot.getKey());
                        }
                        FirebaseDatabase.getInstance().getReference().child("Classes").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot snapshot1) {
                                subjectList.clear();
                                for (DataSnapshot dataSnapshot : snapshot1.getChildren()) {
                                    if (dataSnapshot.child("DayOfWeek").getValue(String.class) != null && dataSnapshot.getKey() != null) {
                                        if (dataSnapshot.child("DayOfWeek").getValue(String.class).equals(selected) && subjectIDList.contains(dataSnapshot.getKey())) {
                                            Subject subject = new Subject(dataSnapshot.getKey(), dataSnapshot.child("Name").getValue(String.class),
                                                    dataSnapshot.child("RoomID").getValue(String.class), dataSnapshot.child("StartTime").getValue(String.class),
                                                    dataSnapshot.child("EndTime").getValue(String.class), dataSnapshot.child("DayOfWeek").getValue(String.class),
                                                    dataSnapshot.child("NumOfStudents").getValue(String.class), dataSnapshot.child("NumOfTeachers").getValue(String.class));
                                            if (subject != null)
                                                subjectList.add(subject);
                                        }
                                    }
                                }
                                if (subjectList.size() > 0) {
                                    Collections.sort(subjectList, new CustomComparator());
                                }
                                subjectAdapter.setArraylist(subjectList);
                                subjectAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        subject_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), SubjectDetailActivity.class);
                intent.putExtra("classID", subjectAdapter.getItem(i).getClassID());
                intent.putExtra("className", subjectAdapter.getItem(i).getName());
                intent.putExtra("role", role);
                startActivity(intent);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                subjectAdapter.filter(etSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // do nothing
            }
        });

        return view;
    }


}
