package com.wongcoco.thinkwapp;

import android.app.Application;

public class FormActivity extends Application {
    private DoubleLinkedList registrationList = new DoubleLinkedList();

    public DoubleLinkedList getRegistrationList() {
        return registrationList;
    }

    // Tambahkan data baru ke dalam registrationList
    public void addRegistrationData(RegistrationData data) {
        registrationList.add(data);
    }

    // Hapus data terakhir jika perlu
    public void removeLastRegistration() {
        registrationList.removeLast();
    }
}
