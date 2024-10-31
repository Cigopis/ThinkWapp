package com.wongcoco.thinkwapp;

import android.app.Application;

public class FormActivity extends Application {
    private DoubleLinkedList registrationList;

    public void setRegistrationList(DoubleLinkedList registrationList) {
        this.registrationList = registrationList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registrationList = new DoubleLinkedList(); // Inisialisasi linked list saat aplikasi dimulai
    }

    public DoubleLinkedList getRegistrationList() {
        return registrationList; // Metode untuk mendapatkan linked list
    }
}
