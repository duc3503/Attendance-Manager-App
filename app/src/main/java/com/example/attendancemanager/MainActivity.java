package com.example.attendancemanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import android.view.View;

import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FrameLayout attendance, user, schedule, subject, about_me;
    LinearLayout attendance_selected, user_selected, schedule_selected, subject_selected, about_me_selected;
    MaterialTextView title;
    MaterialButton add_button, log_out_button;
    HorizontalScrollView horizontalScrollView, userHorizontalScrollView;
    MaterialButton monday, tuesday, wednesday, thursday, friday, saturday, sunday, admin, student, teacher;
    String selected = "Monday", user_type_selected = "Student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        reference = database.getReference().child("Users/" + auth.getCurrentUser().getUid() + "/Role");

        attendance_selected = findViewById(R.id.attendance_selected);
        schedule_selected = findViewById(R.id.schedule_selected);
        user_selected = findViewById(R.id.user_selected);
        subject_selected = findViewById(R.id.subject_selected);
        subject = findViewById(R.id.subject);
        about_me_selected = findViewById(R.id.about_me_selected);
        about_me = findViewById(R.id.about_me);
        title = findViewById(R.id.title);
        add_button = findViewById(R.id.add_button);
        log_out_button = findViewById(R.id.log_out_button);
        userHorizontalScrollView = findViewById(R.id.userHorizontalScrollView);
        admin = findViewById(R.id.admin);
        student = findViewById(R.id.student);
        teacher = findViewById(R.id.teacher);
        horizontalScrollView = findViewById(R.id.horizontalScrollView);
        monday = findViewById(R.id.monday);
        tuesday = findViewById(R.id.tuesday);
        wednesday = findViewById(R.id.wednesday);
        thursday = findViewById(R.id.thursday);
        friday = findViewById(R.id.friday);
        saturday = findViewById(R.id.saturday);
        sunday = findViewById(R.id.sunday);
        user = findViewById(R.id.user);
        schedule = findViewById(R.id.schedule);
        attendance = findViewById(R.id.attendance);

        subject.setVisibility(View.GONE);
        schedule.setVisibility(View.GONE);
        user.setVisibility(View.GONE);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(String.class) != null) {
                    if (snapshot.getValue(String.class).equals("Admin")) {
                        subject.setVisibility(View.VISIBLE);
                        schedule.setVisibility(View.VISIBLE);
                        user.setVisibility(View.VISIBLE);
                    } else if (snapshot.getValue(String.class).equals("Teacher")) {
                        subject.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        if (getIntent().getStringExtra("fragment") == null) {
            selectAttendance();
        } else {
            if (getIntent().getStringExtra("fragment").equals("schedule"))
                selectSchedule();
            else if (getIntent().getStringExtra("fragment").equals("user"))
                selectUser();
            else if (getIntent().getStringExtra("fragment").equals("attendance"))
                selectAttendance();
            else
                selectAttendance();
        }

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUser();
            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSchedule();
            }
        });

        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAttendance();
            }
        });
        subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { selectSubject();
            }
        });

        about_me.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAboutMe();
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (title.getText().toString().equals("Users")) {
                    intent = new Intent(MainActivity.this, StudentAddActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, SubjectAddActivity.class);
                    intent.putExtra("dayOfWeek", selected);
                }
                startActivity(intent);

            }
        });

        monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectButton(monday, title.getText().toString());
            }
        });

        tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectButton(tuesday, title.getText().toString());
            }
        });

        wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectButton(wednesday, title.getText().toString());
            }
        });

        thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectButton(thursday, title.getText().toString());
            }
        });

        friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectButton(friday, title.getText().toString());
            }
        });

        saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectButton(saturday, title.getText().toString());
            }
        });

        sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectButton(sunday, title.getText().toString());
            }
        });

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectUserTypeButton(admin);
            }
        });

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectUserTypeButton(student);
            }
        });

        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectUserTypeButton(teacher);
            }
        });

        log_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                auth.signOut();
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
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
    }

    public void selectButton(MaterialButton button, String title) {
        monday.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.secondary1)));
        tuesday.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.secondary1)));
        wednesday.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.secondary1)));
        thursday.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.secondary1)));
        friday.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.secondary1)));
        saturday.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.secondary1)));
        sunday.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.secondary1)));
        button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));
        selected = button.getText().toString();
        if (title.equals("Schedule"))
            loadFragment(new ScheduleFragment(selected));
        else
            loadFragment(new AttendanceFragment(selected));
    }

    public void selectUserTypeButton (MaterialButton button) {
        admin.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.secondary1)));
        student.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.secondary1)));
        teacher.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.secondary1)));
        button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primary)));
        user_type_selected = button.getText().toString();
        loadFragment(new UserFragment(user_type_selected));
    }

    public void selectSubject() {
        loadFragment(new SubjectFragment());
        title.setText("Select subject for sensor");
        add_button.setVisibility(View.GONE);
        log_out_button.setVisibility(View.GONE);
        horizontalScrollView.setVisibility(View.GONE);
        userHorizontalScrollView.setVisibility(View.GONE);
        schedule_selected.setBackground(null);
        user_selected.setBackground(null);
        attendance_selected.setBackground(null);
        about_me_selected.setBackground(null);
        subject_selected.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_nav_item));
    }

    public void selectAttendance() {
        title.setText("Attendance");
        selectButton(monday, title.getText().toString());
        add_button.setVisibility(View.GONE);
        log_out_button.setVisibility(View.GONE);
        horizontalScrollView.setVisibility(View.VISIBLE);
        userHorizontalScrollView.setVisibility(View.GONE);
        schedule_selected.setBackground(null);
        user_selected.setBackground(null);
        subject_selected.setBackground(null);
        about_me_selected.setBackground(null);
        attendance_selected.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_nav_item));
    }

    public void selectSchedule() {
        title.setText("Schedule");
        selectButton(monday, title.getText().toString());
        add_button.setVisibility(View.VISIBLE);
        log_out_button.setVisibility(View.GONE);
        horizontalScrollView.setVisibility(View.VISIBLE);
        userHorizontalScrollView.setVisibility(View.GONE);
        attendance_selected.setBackground(null);
        user_selected.setBackground(null);
        subject_selected.setBackground(null);
        about_me_selected.setBackground(null);
        schedule_selected.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_nav_item));
    }

    public void selectUser() {
        selectUserTypeButton(student);
        title.setText("Users");
        add_button.setVisibility(View.VISIBLE);
        log_out_button.setVisibility(View.GONE);
        horizontalScrollView.setVisibility(View.GONE);
        userHorizontalScrollView.setVisibility(View.VISIBLE);
        attendance_selected.setBackground(null);
        schedule_selected.setBackground(null);
        subject_selected.setBackground(null);
        about_me_selected.setBackground(null);
        user_selected.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_nav_item));
    }

    public void selectAboutMe() {
        loadFragment(new AboutMeFragment());
        title.setText("About me");
        add_button.setVisibility(View.GONE);
        log_out_button.setVisibility(View.VISIBLE);
        horizontalScrollView.setVisibility(View.GONE);
        userHorizontalScrollView.setVisibility(View.GONE);
        schedule_selected.setBackground(null);
        user_selected.setBackground(null);
        subject_selected.setBackground(null);
        attendance_selected.setBackground(null);
        about_me_selected.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.selected_nav_item));
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, fragment);
        transaction.commit();
    };
}