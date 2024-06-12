package com.example.attendancemanager.model;

public class User {
    private String userID, ID, role, name, gender, dob, phone, email, password, fingerprintID;

    public User(String userID, String ID, String role, String name, String gender, String dob, String phone, String email, String password, String fingerprintID) {
        this.userID = userID;
        this.ID = ID;
        this.role = role;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.fingerprintID = fingerprintID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFingerprintID() {
        return fingerprintID;
    }

    public void setFingerprintID(String fingerprintID) {
        this.fingerprintID = fingerprintID;
    }
}
