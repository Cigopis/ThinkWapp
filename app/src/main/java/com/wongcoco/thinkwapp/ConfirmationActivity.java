package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide; // Pastikan Anda sudah menambahkan Glide dependency
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ConfirmationActivity extends AppCompatActivity {

    private TextView tvConfirmation;
    private ImageView imgKTP;
    private ImageView imgLahan;
    private ImageView stepFormulir;
    private ImageView stepUnggah;
    private ImageView stepKonfirmasi;
    private View lineFormulirToUnggah;
    private View lineUnggahToKonfirmasi;
    private TextView btnSave; // Tombol Save baru
    private FirebaseFirestore db;
    private DoubleLinkedList registrationList; // Deklarasi DoubleLinkedList
    private String userId;
    private TextView textNik; // NIK TextView
    private TextView textNama; // Nama TextView
    private TextView textAlamat; // Alamat TextView
    private TextView textNomor; // No. Telepon TextView
    private TextView textLuasTanah; // Luas Lahan TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        // Inisialisasi view
        tvConfirmation = findViewById(R.id.tvConfirmation);
        imgKTP = findViewById(R.id.imgKTP);
        imgLahan = findViewById(R.id.imgLahan);
        stepFormulir = findViewById(R.id.stepFormulir);
        stepUnggah = findViewById(R.id.stepUnggah);
        stepKonfirmasi = findViewById(R.id.stepKonfirmasi);
        lineFormulirToUnggah = findViewById(R.id.lineFormulirToUnggah);
        lineUnggahToKonfirmasi = findViewById(R.id.lineUnggahToKonfirmasi);
        btnSave = findViewById(R.id.btnSave); // Inisialisasi tombol Save

        // Inisialisasi TextView untuk data
        textNik = findViewById(R.id.textNik);
        textNama = findViewById(R.id.textNama);
        textAlamat = findViewById(R.id.textAlamat);
        textNomor = findViewById(R.id.textNomor);
        textLuasTanah = findViewById(R.id.textLuasTanah);

        // Inisialisasi Firestore dan FirebaseAuth
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Ambil data dari DoubleLinkedList
        registrationList = ((FormActivity) getApplication()).getRegistrationList();
        RegistrationData data = registrationList.getLast();

        if (data != null) {
            tvConfirmation.setText("Konfirmasi untuk: " + data.getNama());
            loadImages(data);
            displayRegistrationData(data); // Menampilkan data
        } else {
            Toast.makeText(this, "Data tidak ditemukan!", Toast.LENGTH_SHORT).show();
        }

        // Set active step
        setStepActive(3);

        // Set onClickListener untuk tombol Save
        btnSave.setOnClickListener(v -> onSaveButtonClick(data));
    }

    private void displayRegistrationData(RegistrationData data) {
        textNik.setText("NIK: " + data.getNik());
        textNama.setText("Nama: " + data.getNama());
        textAlamat.setText("Alamat: " + data.getAlamat());
        textNomor.setText("No. Telepon: " + data.getNomorTelepon());
        textLuasTanah.setText("Luas Lahan: " + data.getLuasLahan());
    }

    private void loadImages(RegistrationData data) {
        if (data.getUriKTP() != null) {
            Glide.with(this).load(data.getUriKTP()).into(imgKTP);
        } else {
            imgKTP.setImageResource(R.drawable.carikontak); // Gambar default jika tidak ada
        }

        if (data.getUriLahan() != null && !data.getUriLahan().isEmpty()) {
            Glide.with(this).load(data.getUriLahan().get(0)).into(imgLahan); // Mengambil gambar lahan pertama
        } else {
            imgLahan.setImageResource(R.drawable.carikontak); // Gambar default jika tidak ada
        }
    }

    private void setStepActive(int step) {
        stepFormulir.setBackgroundResource(R.drawable.circle_grey);
        stepUnggah.setBackgroundResource(R.drawable.circle_grey);
        stepKonfirmasi.setBackgroundResource(R.drawable.circle_grey);
        lineFormulirToUnggah.setBackgroundColor(getResources().getColor(R.color.Secondary));
        lineUnggahToKonfirmasi.setBackgroundColor(getResources().getColor(R.color.Secondary));

        if (step == 1) {
            stepFormulir.setBackgroundResource(R.drawable.circle_green);
        } else if (step == 2) {
            stepFormulir.setBackgroundResource(R.drawable.circle_green);
            stepUnggah.setBackgroundResource(R.drawable.circle_green);
            lineFormulirToUnggah.setBackgroundColor(getResources().getColor(R.color.Primary));
        } else if (step == 3) {
            stepFormulir.setBackgroundResource(R.drawable.circle_green);
            stepUnggah.setBackgroundResource(R.drawable.circle_green);
            stepKonfirmasi.setBackgroundResource(R.drawable.circle_green);
            lineFormulirToUnggah.setBackgroundColor(getResources().getColor(R.color.Primary));
            lineUnggahToKonfirmasi.setBackgroundColor(getResources().getColor(R.color.Primary));
        }
    }

    private void onSaveButtonClick(RegistrationData data) {
        if (data != null) {
            saveDataToFirestore(data);
        } else {
            Toast.makeText(this, "Data tidak tersedia untuk disimpan.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveDataToFirestore(RegistrationData data) {
        Map<String, Object> registrationMap = new HashMap<>();
        registrationMap.put("nik", data.getNik());
        registrationMap.put("nama", data.getNama());
        registrationMap.put("alamat", data.getAlamat());
        registrationMap.put("nomorTelepon", data.getNomorTelepon());
        registrationMap.put("luasLahan", data.getLuasLahan());
        registrationMap.put("uriSurat", data.getUriKTP());
        registrationMap.put("uriLahan", data.getUriLahan());
        registrationMap.put("userId", data.getUserId());

        // Tambahkan registrationDate dengan Timestamp
        registrationMap.put("registrationDate", com.google.firebase.Timestamp.now());

        db.collection("registrations")
                .document(data.getUserId())
                .set(registrationMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ConfirmationActivity.this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show();
                    Intent doneIntent = new Intent(ConfirmationActivity.this, DoneActivity.class);
                    startActivity(doneIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ConfirmationActivity.this, "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}
