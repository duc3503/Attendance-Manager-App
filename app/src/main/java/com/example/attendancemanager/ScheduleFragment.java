package com.example.attendancemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.attendancemanager.adapter.SubjectAdapter;
import com.example.attendancemanager.model.CustomComparator;
import com.example.attendancemanager.model.Subject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ScheduleFragment extends Fragment {
    FirebaseDatabase database;
    ListView subject_listview;
    List<Subject> subjectList;
    DatabaseReference reference;
    EditText etSearch;
    String selected;


    public ScheduleFragment(String selected) {
        this.selected = selected;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child("Classes");
        subjectList = new LinkedList<Subject>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // load layout from XML
        subject_listview = view.findViewById(R.id.subject_listview);
        etSearch = view.findViewById(R.id.etSearch);

        SubjectAdapter subjectAdapter = new SubjectAdapter(getContext(), subjectList);
        subject_listview.setAdapter(subjectAdapter);
        subject_listview.setTextFilterEnabled(true);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subjectList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        subject_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), SubjectModificationActivity.class);
                intent.putExtra("classID", subjectList.get(i).getClassID());
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
