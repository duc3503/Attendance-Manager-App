package com.example.attendancemanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AboutMeFragment extends Fragment {
    CircleImageView profile_pic;
    TextView name, id, role, email, gender, dob, phone;
    DatabaseReference reference;
    FirebaseAuth auth;


    public AboutMeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users/" + auth.getCurrentUser().getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_me, container, false);

        // Load layout from XML
        name = view.findViewById(R.id.name);
        id = view.findViewById(R.id.id);
        role = view.findViewById(R.id.role);
        email = view.findViewById(R.id.email);
        gender = view.findViewById(R.id.gender);
        dob = view.findViewById(R.id.dob);
        phone = view.findViewById(R.id.phone);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("ID").getValue(String.class) != null)
                    id.setText(snapshot.child("ID").getValue(String.class));
                if (snapshot.child("Name").getValue(String.class) != null)
                    name.setText(snapshot.child("Name").getValue(String.class));
                if (snapshot.child("Role").getValue(String.class) != null)
                    role.setText(snapshot.child("Role").getValue(String.class));
                if (snapshot.child("Email").getValue(String.class) != null)
                    email.setText(snapshot.child("Email").getValue(String.class));
                if (snapshot.child("Gender").getValue(String.class) != null)
                    gender.setText(snapshot.child("Gender").getValue(String.class));
                if (snapshot.child("Dob").getValue(String.class) != null)
                    dob.setText(snapshot.child("Dob").getValue(String.class));
                if (snapshot.child("Phone").getValue(String.class) != null)
                    phone.setText(snapshot.child("Phone").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return view;
    }
}