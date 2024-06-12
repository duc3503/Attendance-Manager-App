package com.example.attendancemanager.model;

import java.util.List;

public class Subject {
    private String classID, name, roomID, startTime, endTime, dayOfWeek, num_of_students, num_of_teachers;

    public Subject(String classID, String name, String roomID, String startTime, String endTime, String dayOfWeek, String num_of_students, String num_of_teachers) {
        this.classID = classID;
        this.name = name;
        this.roomID = roomID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
        this.num_of_students = num_of_students;
        this.num_of_teachers = num_of_teachers;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getNum_of_students() {
        return num_of_students;
    }

    public void setNum_of_students(String num_of_students) {
        this.num_of_students = num_of_students;
    }

    public String getNum_of_teachers() {
        return num_of_teachers;
    }

    public void setNum_of_teachers(String num_of_teachers) {
        this.num_of_teachers = num_of_teachers;
    }
}
