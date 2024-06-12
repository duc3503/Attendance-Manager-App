package com.example.attendancemanager;

import android.annotation.SuppressLint;
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

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.attendancemanager.adapter.UserAdapter;
import com.example.attendancemanager.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class ClassParticipantModificationActivity extends AppCompatActivity {
    ImageButton back_button;
    EditText etSearch;
    ListView participant_listview;
    TextView selected, title;
    List<Pair<User, Boolean>> studentList, teacherList;
    ArrayList<String> selectedStudentArrayList, selectedTeacherArrayList;
    DatabaseReference participant_reference, user_reference;
    UserAdapter adapter;
    int num_of_selected = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_modification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.modify_participant), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        user_reference = FirebaseDatabase.getInstance().getReference().child("Users");
        participant_reference = FirebaseDatabase.getInstance().getReference().child("Classes/" + getIntent().getStringExtra("classID") +
                "/" + getIntent().getStringExtra("type") + "s");
        studentList = new LinkedList<Pair<User, Boolean>>();
        teacherList = new LinkedList<Pair<User, Boolean>>();
        selectedStudentArrayList = new ArrayList<String>();
        selectedTeacherArrayList = new ArrayList<String>();

        // load layout from XML
        back_button = findViewById(R.id.back_button);
        etSearch = findViewById(R.id.etSearch);
        participant_listview = findViewById(R.id.participant_listview);
        selected = findViewById(R.id.selected);
        title = findViewById(R.id.title);

        if (getIntent().getStringExtra("type").equals("Teacher")) {
            adapter = new UserAdapter(this, teacherList);
            title.setText("Teacher List");
        } else {
            adapter = new UserAdapter(this, studentList);
            title.setText("Student List");
        }
        participant_listview.setAdapter(adapter);


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getStringExtra("type").equals("Teacher"))
                    setResult(1);
                else if (getIntent().getStringExtra("type").equals("Student"))
                    setResult(0);
                finish();
            }
        });

        participant_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                selectedStudentArrayList.clear();
                selectedTeacherArrayList.clear();
                num_of_selected = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        if (getIntent().getStringExtra("type").equals("Teacher")) {
                            selectedTeacherArrayList.add(dataSnapshot.getKey());
                            num_of_selected++;
                        } else if (getIntent().getStringExtra("type").equals("Student")) {
                            selectedStudentArrayList.add(dataSnapshot.getKey());
                            num_of_selected++;
                        }
                    }
                }
                selected.setText(num_of_selected + " selected");
                user_reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        teacherList.clear();
                        studentList.clear();
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            User user = new User(dataSnapshot1.getKey(), dataSnapshot1.child("ID").getValue(String.class), dataSnapshot1.child("Role").getValue(String.class),
                                    dataSnapshot1.child("Name").getValue(String.class), dataSnapshot1.child("Gender").getValue(String.class),
                                    dataSnapshot1.child("Dob").getValue(String.class), dataSnapshot1.child("Phone").getValue(String.class),
                                    dataSnapshot1.child("Email").getValue(String.class), dataSnapshot1.child("Password").getValue(String.class),
                                    dataSnapshot1.child("FingerprintID").getValue(String.class));
                            if (user != null) {
                                if (user.getRole().equals("Teacher")) {
                                    if (selectedTeacherArrayList.contains(user.getUserID())) {
                                        teacherList.add(new Pair<User, Boolean>(user, true));
                                    } else {
                                        teacherList.add(new Pair<User, Boolean>(user, false));
                                    }
                                }
                                if (user.getRole().equals("Student")) {
                                    if (selectedStudentArrayList.contains(user.getUserID())) {
                                        studentList.add(new Pair<User, Boolean>(user, true));
                                    } else {
                                        studentList.add(new Pair<User, Boolean>(user, false));
                                    }
                                }
                            }
                        }
                        if (getIntent().getStringExtra("type").equals("Teacher")) {
                            if (teacherList != null) {
                                Collections.sort(teacherList, new Comparator<Pair<User, Boolean>>() {
                                    @Override
                                    public int compare(Pair<User, Boolean> p1, Pair<User, Boolean> p2) {
                                        return Boolean.compare(p2.second, p1.second);
                                    }
                                });
                                adapter.setArraylist(teacherList);
                            }
                        } else if (getIntent().getStringExtra("type").equals("Student")) {
                            if (studentList != null) {
                                Collections.sort(studentList, new Comparator<Pair<User, Boolean>>() {
                                    @Override
                                    public int compare(Pair<User, Boolean> p1, Pair<User, Boolean> p2) {
                                        return Boolean.compare(p2.second, p1.second);
                                    }
                                });
                                adapter.setArraylist(studentList);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        participant_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (getIntent().getStringExtra("type").equals("Teacher")) {
                    if (teacherList.get(i).second == true) {
                        teacherList.set(i, new Pair<User, Boolean>(teacherList.get(i).first, false));
                        selectedTeacherArrayList.remove(teacherList.get(i).first.getUserID());
                        num_of_selected--;
                        participant_reference.child(teacherList.get(i).first.getUserID()).removeValue();
                        user_reference.child(teacherList.get(i).first.getUserID() + "/Classes/" + getIntent().getStringExtra("classID")).removeValue();
                    } else {
                        teacherList.set(i, new Pair<User, Boolean>(teacherList.get(i).first, true));
                        selectedTeacherArrayList.add(teacherList.get(i).first.getUserID());
                        num_of_selected++;
                        participant_reference.child(teacherList.get(i).first.getUserID()).setValue("True");
                        user_reference.child(teacherList.get(i).first.getUserID() + "/Classes/" + getIntent().getStringExtra("classID")).setValue("True");
                    }
                    FirebaseDatabase.getInstance().getReference().child("Classes/" + getIntent().getStringExtra("classID") + "/NumOfTeachers").setValue(String.valueOf(num_of_selected));
                } else if (getIntent().getStringExtra("type").equals("Student")) {
                    if (studentList.get(i).second == true) {
                        studentList.set(i, new Pair<User, Boolean>(studentList.get(i).first, false));
                        selectedStudentArrayList.remove(studentList.get(i).first.getUserID());
                        num_of_selected--;
                        participant_reference.child(studentList.get(i).first.getUserID()).removeValue();
                        user_reference.child(studentList.get(i).first.getUserID() + "/Classes/" + getIntent().getStringExtra("classID")).removeValue();
                    } else {
                        studentList.set(i, new Pair<User, Boolean>(studentList.get(i).first, true));
                        selectedStudentArrayList.add(studentList.get(i).first.getUserID());
                        num_of_selected++;
                        participant_reference.child(studentList.get(i).first.getUserID()).setValue("True");
                        user_reference.child(studentList.get(i).first.getUserID() + "/Classes/" + getIntent().getStringExtra("classID")).setValue("True");
                    }
                    FirebaseDatabase.getInstance().getReference().child("Classes/" + getIntent().getStringExtra("classID") + "/NumOfStudents").setValue(String.valueOf(num_of_selected));
                }
                selected.setText(num_of_selected + " selected");
                if (getIntent().getStringExtra("type").equals("Teacher")) {
                    if (teacherList != null) {
                        Collections.sort(teacherList, new Comparator<Pair<User, Boolean>>() {
                            @Override
                            public int compare(Pair<User, Boolean> p1, Pair<User, Boolean> p2) {
                                return Boolean.compare(p2.second, p1.second);
                            }
                        });
                        adapter.setArraylist(teacherList);
                    }
                } else if (getIntent().getStringExtra("type").equals("Student")) {
                    if (studentList != null) {
                        Collections.sort(studentList, new Comparator<Pair<User, Boolean>>() {
                            @Override
                            public int compare(Pair<User, Boolean> p1, Pair<User, Boolean> p2) {
                                return Boolean.compare(p2.second, p1.second);
                            }
                        });
                        adapter.setArraylist(studentList);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.filter(etSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(ClassParticipantModificationActivity.this, SubjectAddActivity.class);
                if (getIntent().getStringExtra("type").equals("Teacher")) {
                    intent.putStringArrayListExtra("teacherList", selectedTeacherArrayList);
                    setResult(1, intent);
                } else if (getIntent().getStringExtra("type").equals("Student")) {
                    intent.putStringArrayListExtra("studentList", selectedStudentArrayList);
                    setResult(0, intent);
                }
                finish();
            }
        });

    }

}