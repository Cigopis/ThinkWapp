package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etNik, etNama, etAlamat, etNomorTelepon, etLuasLahan;
    private Button btnNext;
    private DoubleLinkedList registrationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registrationList = new DoubleLinkedList();

        etNik = findViewById(R.id.editTxtNik);
        etNama = findViewById(R.id.editTxtNama);
        etAlamat = findViewById(R.id.editTxtAlamat);
        etNomorTelepon = findViewById(R.id.editTxtNomor);
        etLuasLahan = findViewById(R.id.editTxtLuasTanah);
        btnNext = findViewById(R.id.btnNext);

        btnNext.setOnClickListener(v -> {
            String nik = etNik.getText().toString().trim();
            String nama = etNama.getText().toString().trim();
            String alamat = etAlamat.getText().toString().trim();
            String nomorTelepon = etNomorTelepon.getText().toString().trim();
            String luasLahan = etLuasLahan.getText().toString().trim();

            // Validasi input
            if (nik.isEmpty() || nama.isEmpty() || alamat.isEmpty() || nomorTelepon.isEmpty() || luasLahan.isEmpty()) {
                Toast.makeText(RegistrationActivity.this, "Silakan isi semua field!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Menyimpan data registrasi
            RegistrationData data = new RegistrationData(nik, nama, alamat, nomorTelepon, luasLahan, null, null);
            registrationList.add(data);

            // Menyimpan registrationList di aplikasi agar dapat diakses di UploadActivity
            ((FormActivity) getApplication()).setRegistrationList(registrationList);

            // Berpindah ke UploadActivity
            Intent intent = new Intent(RegistrationActivity.this, UploadActivity.class);
            startActivity(intent);
        });
    }
}
