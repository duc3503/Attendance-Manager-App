package com.example.attendancemanager.model;

public class Room {
    private String roomID, sensorID;

    public Room(String roomID, String sensorID) {
        this.roomID = roomID;
        this.sensorID = sensorID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }
}
