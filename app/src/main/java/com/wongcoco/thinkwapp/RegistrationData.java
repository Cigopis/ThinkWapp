package com.wongcoco.thinkwapp;

import java.util.List;

public class RegistrationData {
    private String userId;  // Primary key for the user
    private String nik;
    private String nama;
    private String alamat;
    private String nomorTelepon;
    private String luasLahan;
    private String uriKTP;
    private List<String> uriLahan; // List to store multiple URIs

    // No-argument constructor for Firestore
    public RegistrationData() {
        // Required for Firestore serialization/deserialization
    }

    public RegistrationData(String userId, String nik, String nama, String alamat, String nomorTelepon, String luasLahan, String uriKTP, List<String> uriLahan) {
        this.userId = userId;
        this.nik = nik;
        this.nama = nama;
        this.alamat = alamat;
        this.nomorTelepon = nomorTelepon;
        this.luasLahan = luasLahan;
        this.uriKTP = uriKTP;
        this.uriLahan = uriLahan;
    }

    // Getter and setter for userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter and setter for NIK
    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    // Getter and setter for name
    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    // Getter and setter for address
    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    // Getter and setter for phone number
    public String getNomorTelepon() {
        return nomorTelepon;
    }

    public void setNomorTelepon(String nomorTelepon) {
        this.nomorTelepon = nomorTelepon;
    }

    // Getter and setter for land area
    public String getLuasLahan() {
        return luasLahan;
    }

    public void setLuasLahan(String luasLahan) {
        this.luasLahan = luasLahan;
    }

    // Getter and setter for KTP URI
    public String getUriKTP() {
        return uriKTP;
    }

    public void setUriKTP(String uriKTP) {
        this.uriKTP = uriKTP;
    }

    // Getter and setter for land URI list
    public List<String> getUriLahan() {
        return uriLahan;
    }

    public void setUriLahan(List<String> uriLahan) {
        this.uriLahan = uriLahan;
    }
}
