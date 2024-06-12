package com.example.attendancemanager;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.attendancemanager.adapter.RoomAdapter;
import com.example.attendancemanager.model.Room;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;


public class SubjectFragment extends Fragment {
    EditText etSearch;
    List<Room> roomList;
    ListView subject_listview;
    DatabaseReference reference;

    public SubjectFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = FirebaseDatabase.getInstance().getReference().child("Sensors/");
        roomList = new LinkedList<Room>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // load layout from XML
        etSearch = view.findViewById(R.id.etSearch);
        subject_listview = view.findViewById(R.id.subject_listview);

        RoomAdapter adapter = new RoomAdapter(getContext(), roomList);
        subject_listview.setAdapter(adapter);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey() != null && dataSnapshot.getValue(String.class) != null) {
                        Room room = new Room(dataSnapshot.getValue(String.class), dataSnapshot.getKey());
                        if (room != null)
                            roomList.add(room);
                    }
                }
                adapter.setArraylist(roomList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
                Intent intent = new Intent(getContext(), ListAllClassActivity.class);
                intent.putExtra("roomID", roomList.get(i).getRoomID());
                intent.putExtra("sensorID", roomList.get(i).getSensorID());
                startActivity(intent);
            }
        });

        return view;
    }

    protected String intToDay(int dayOfWeek) {
        switch (dayOfWeek) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return null;
        }
    }
}