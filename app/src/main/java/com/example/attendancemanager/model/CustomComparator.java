package com.example.attendancemanager.model;

import java.time.LocalTime;
import java.util.Comparator;

public class CustomComparator implements Comparator<Subject> {
    @Override
    public int compare(Subject obj1, Subject obj2) {
        if (obj1.getStartTime() != null && obj2.getStartTime() != null) {
            LocalTime time1 = LocalTime.parse(obj1.getStartTime());
            LocalTime time2 = LocalTime.parse(obj2.getStartTime());
            if (time1.compareTo(time2) == 0) {
                if (obj1.getEndTime() != null && obj2.getEndTime() != null) {
                    time1 = LocalTime.parse(obj1.getEndTime());
                    time2 = LocalTime.parse(obj2.getEndTime());
                }
            }
            return time1.compareTo(time2);
        }
        return 0;
    }
}
