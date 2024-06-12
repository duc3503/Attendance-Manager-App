package com.example.attendancemanager.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public class DateStringComparator implements Comparator<String> {

    private SimpleDateFormat formatter;

    public DateStringComparator(String dateFormat) {
        formatter = new SimpleDateFormat(dateFormat);
    }

    @Override
    public int compare(String s1, String s2) {
        try {
            return formatter.parse(s1).compareTo(formatter.parse(s2));
        } catch (ParseException exception) {
            throw new RuntimeException(exception);
        }
    }
}