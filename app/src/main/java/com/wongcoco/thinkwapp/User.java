package com.wongcoco.thinkwapp;

public class User {
    private String uid;
    private String name;
    private String email;
    private String password;

    public User() {
        // Required for Firestore
    }

    public User(String uid, String email, String password) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password; // Initialize password
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
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
}
