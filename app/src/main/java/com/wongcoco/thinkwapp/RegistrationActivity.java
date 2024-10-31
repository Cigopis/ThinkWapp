package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etNik, etNama, etAlamat, etNomorTelepon, etLuasLahan;
    private Button btnNext;
    private DoubleLinkedList registrationList;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        registrationList = new DoubleLinkedList();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Dapatkan userId dari FirebaseAuth

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

            // Menyimpan data registrasi dengan userId
            RegistrationData data = new RegistrationData(userId, nik, nama, alamat, nomorTelepon, luasLahan, null, null);
            registrationList.add(data);

            // Menyimpan registrationList di aplikasi agar dapat diakses di UploadActivity
            ((FormActivity) getApplication()).setRegistrationList(registrationList);

            // Berpindah ke UploadActivity
            Intent intent = new Intent(RegistrationActivity.this, UploadActivity.class);
            startActivity(intent);
        });
    }
}
