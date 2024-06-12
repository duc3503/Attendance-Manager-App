package com.example.attendancemanager.model;

public class Attendance {
    private String userID, name, id;
    private Integer numOfTime;

    public Attendance(String userID, String id, String name, Integer numOfTime) {
        this.userID = userID;
        this.id = id;
        this.name = name;
        this.numOfTime = numOfTime;
    }

    public String getUserID() {
        return userID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserID(String userID) {
        this.userID = userID;


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumOfTime() {
        return numOfTime;
    }

    public void setNumOfTime(Integer numOfTime) {
        this.numOfTime = numOfTime;
    }
}
