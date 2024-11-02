package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import com.bumptech.glide.Glide; // Pastikan Anda sudah menambahkan Glide dependency
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
    private FirebaseFirestore db;
    private DoubleLinkedList<RegistrationData> registrationList;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        tvConfirmation = findViewById(R.id.tvConfirmation);
        imgKTP = findViewById(R.id.imgKTP);
        imgLahan = findViewById(R.id.imgLahan);
        stepFormulir = findViewById(R.id.stepFormulir);
        stepUnggah = findViewById(R.id.stepUnggah);
        stepKonfirmasi = findViewById(R.id.stepKonfirmasi);
        lineFormulirToUnggah = findViewById(R.id.lineFormulirToUnggah);
        lineUnggahToKonfirmasi = findViewById(R.id.lineUnggahToKonfirmasi);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Retrieve data from the registration list
        registrationList = ((FormActivity) getApplication()).getRegistrationList();
        RegistrationData data = registrationList.getLast();

        if (data != null) {
            // Set data to views
            tvConfirmation.setText("Konfirmasi untuk: " + data.getNama());
            loadImages(data);
            saveDataToFirestore(data);
        } else {
            Toast.makeText(this, "Data tidak ditemukan!", Toast.LENGTH_SHORT).show();
        }

        // Set active step
        setStepActive(3);
    }

    private void loadImages(RegistrationData data) {
        if (data.getUriKTP() != null) {
            Glide.with(this).load(data.getUriKTP()).into(imgKTP);
        } else {
            imgKTP.setImageResource(R.drawable.carikontak); // Gambar default jika tidak ada
        }

        if (data.getUriLahan() != null) {
            Glide.with(this).load(data.getUriLahan()).into(imgLahan);
        } else {
            imgLahan.setImageResource(R.drawable.carikontak); // Gambar default jika tidak ada
        }
    }

    private void setStepActive(int step) {
        // Deactivate all steps
        stepFormulir.setBackgroundResource(R.drawable.circle_grey);
        stepUnggah.setBackgroundResource(R.drawable.circle_grey);
        stepKonfirmasi.setBackgroundResource(R.drawable.circle_grey);
        lineFormulirToUnggah.setBackgroundColor(getResources().getColor(R.color.Secondary));
        lineUnggahToKonfirmasi.setBackgroundColor(getResources().getColor(R.color.Secondary));

        // Activate steps based on the provided step number
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

    private void saveDataToFirestore(RegistrationData data) {
        if (data != null) {
            Map<String, Object> registrationMap = new HashMap<>();
            registrationMap.put("nik", data.getNik());
            registrationMap.put("nama", data.getNama());
            registrationMap.put("alamat", data.getAlamat());
            registrationMap.put("nomorTelepon", data.getNomorTelepon());
            registrationMap.put("luasLahan", data.getLuasLahan());
            registrationMap.put("uriKTP", data.getUriKTP());
            registrationMap.put("uriLahan", data.getUriLahan());
            registrationMap.put("userId", data.getUserId());

            db.collection("registrations")
                    .document(userId)
                    .set(registrationMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ConfirmationActivity.this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show();
                        // Arahkan ke DoneActivity
                        Intent doneIntent = new Intent(ConfirmationActivity.this, DoneActivity.class);
                        startActivity(doneIntent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ConfirmationActivity.this, "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

}
