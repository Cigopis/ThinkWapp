package com.wongcoco.thinkwapp;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ConfirmationActivity extends AppCompatActivity {

    private TextView tvConfirmation;
    private Button btnConfirm;
    private FirebaseFirestore db;
    private ImageView imgKTP, imgLahan;
    private DoubleLinkedList registrationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        tvConfirmation = findViewById(R.id.tvConfirmation);
        btnConfirm = findViewById(R.id.btnConfirm);
        imgKTP = findViewById(R.id.imgKTP);
        imgLahan = findViewById(R.id.imgLahan);

        db = FirebaseFirestore.getInstance();

        // Mengambil data dari LinkedList
        registrationList = ((FormActivity) getApplication()).getRegistrationList();
        RegistrationData data = registrationList.getLast();

        if (data != null) {
            // Menampilkan informasi registrasi
            tvConfirmation.setText("NIK: " + data.getNik() +
                    "\nNama: " + data.getNama() +
                    "\nAlamat: " + data.getAlamat() +
                    "\nNomor Telepon: " + data.getNomorTelepon() +
                    "\nLuas Lahan: " + data.getLuasLahan());

            // Menampilkan gambar KTP dan lahan jika ada
            imgKTP.setImageURI(Uri.parse(data.getUriKTP()));
            imgLahan.setImageURI(Uri.parse(data.getUriLahan()));
        }

        btnConfirm.setOnClickListener(v -> {
            saveDataToFirestore(data);
        });
    }

    private void saveDataToFirestore(RegistrationData data) {
        // Menyimpan data registrasi ke Firestore
        if (data != null) {
            // Membuat map untuk menyimpan data
            Map<String, Object> registrationMap = new HashMap<>();
            registrationMap.put("nik", data.getNik());
            registrationMap.put("nama", data.getNama());
            registrationMap.put("alamat", data.getAlamat());
            registrationMap.put("nomorTelepon", data.getNomorTelepon());
            registrationMap.put("luasLahan", data.getLuasLahan());
            registrationMap.put("uriKTP", data.getUriKTP());
            registrationMap.put("uriLahan", data.getUriLahan());

            db.collection("registrations")
                    .add(registrationMap) // Menyimpan map
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(ConfirmationActivity.this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show();
                        finish(); // Kembali ke aktivitas sebelumnya
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ConfirmationActivity.this, "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(ConfirmationActivity.this, "Data tidak tersedia untuk disimpan!", Toast.LENGTH_SHORT).show();
        }
    }
}
