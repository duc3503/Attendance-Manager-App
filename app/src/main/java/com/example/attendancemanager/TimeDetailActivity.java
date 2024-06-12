package com.example.attendancemanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.attendancemanager.adapter.TimeAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;
import java.util.List;

public class TimeDetailActivity extends AppCompatActivity {
    ImageButton back_button;
    TextView title;
    ListView time_listview;
    List<String> timeList;
    DatabaseReference reference;
    ValueEventListener event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_time_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reference = FirebaseDatabase.getInstance().getReference().child("Attendance/" + getIntent().getStringExtra("classID") +
                "/" + getIntent().getStringExtra("date") + "/" + getIntent().getStringExtra("userID"));
        timeList = new LinkedList<String>();

        // load layout from XML
        back_button = findViewById(R.id.back_button);
        time_listview = findViewById(R.id.time_listview);
        title = findViewById(R.id.title);

        title.setText(getIntent().getStringExtra("ID") + " - " + getIntent().getStringExtra("Name"));

        TimeAdapter adapter = new TimeAdapter(this, timeList);
        time_listview.setAdapter(adapter);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        reference.addValueEventListener(event = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                timeList.clear();
                if (snapshot.child("NumOfTime").getValue(String.class) != null) {
                    if (Integer.valueOf(snapshot.child("NumOfTime").getValue(String.class)) > 0) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                            if (dataSnapshot.getValue(String.class) != null && !dataSnapshot.getKey().equals("NumOfTime"))
                                timeList.add(dataSnapshot.getValue(String.class));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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