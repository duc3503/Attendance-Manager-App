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

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import com.example.attendancemanager.adapter.UserAdapter;
import com.example.attendancemanager.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;


public class UserFragment extends Fragment {
    FirebaseDatabase database;
    ListView user_listview;
    EditText etUser;
    List<Pair<User, Boolean>> userList;
    MaterialButton admin, student, teacher;
    String selected;

    public UserFragment(String selected) {
        this.selected = selected;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // load layout from XML
        user_listview = view.findViewById(R.id.user_listview);
        etUser = view.findViewById(R.id.etUser);

        userList = new LinkedList<Pair<User, Boolean>>();

        UserAdapter userAdapter = new UserAdapter(getActivity(), userList);
        user_listview.setAdapter(userAdapter);
        user_listview.setTextFilterEnabled(true);

        DatabaseReference reference = database.getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("Role").getValue(String.class) != null) {
                        if (dataSnapshot.child("Role").getValue(String.class).equals(selected)) {
                            User user = new User(dataSnapshot.getKey(), dataSnapshot.child("ID").getValue(String.class), dataSnapshot.child("Role").getValue(String.class),
                                    dataSnapshot.child("Name").getValue(String.class), dataSnapshot.child("Gender").getValue(String.class),
                                    dataSnapshot.child("Dob").getValue(String.class), dataSnapshot.child("Phone").getValue(String.class),
                                    dataSnapshot.child("Email").getValue(String.class), dataSnapshot.child("Password").getValue(String.class),
                                    dataSnapshot.child("FingerprintID").getValue(String.class));
                            if (user != null)
                                userList.add(new Pair<>(user, false));
                        }
                    }
                }
                userAdapter.setArraylist(userList);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        user_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), StudentModificationActivity.class);
                intent.putExtra("userID", userList.get(i).first.getUserID());
                intent.putExtra("role", userList.get(i).first.getRole());
                startActivity(intent);
            }
        });

        etUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userAdapter.filter(etUser.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // do nothing
            }
        });


        return view;
    }


}
