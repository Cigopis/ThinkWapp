package com.wongcoco.thinkwapp;

import android.app.Application;

public class FormActivity extends Application {
    private DoubleLinkedList<RegistrationData> registrationList;

    @Override
    public void onCreate() {
        super.onCreate();
        registrationList = new DoubleLinkedList<>(); // Inisialisasi linked list saat aplikasi dimulai
    }

    public DoubleLinkedList<RegistrationData> getRegistrationList() {
        return registrationList; // Metode untuk mendapatkan linked list
    }

    public void addRegistrationData(RegistrationData data) {
        registrationList.add(data); // Menambahkan data pendaftaran ke dalam linked list
    }

    public RegistrationData getLast() {
        return registrationList.getLast(); // Mengambil data pendaftaran terakhir
    }
}
