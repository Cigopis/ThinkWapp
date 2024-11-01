package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etNik, etNama, etAlamat, etNomorTelepon, etLuasLahan;
    private TextView btnNext;
    private String userId;
    private int currentStep = 1;  // Menambahkan currentStep untuk melacak langkah saat ini

    private ImageView stepFormulir, stepUnggah, stepKonfirmasi;
    private View lineFormulirToUnggah, lineUnggahToKonfirmasi;

    private FirebaseFirestore db; // Inisialisasi Firestore
    private RegistrationData registrationData; // Untuk menyimpan data registrasi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        db = FirebaseFirestore.getInstance(); // Dapatkan instance Firestore
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Dapatkan userId dari FirebaseAuth

        etNik = findViewById(R.id.editTxtNik);
        etNama = findViewById(R.id.editTxtNama);
        etAlamat = findViewById(R.id.editTxtAlamat);
        etNomorTelepon = findViewById(R.id.editTxtNomor);
        etLuasLahan = findViewById(R.id.editTxtLuasTanah);
        btnNext = findViewById(R.id.btnNextUp);

        // Inisialisasi views
        stepFormulir = findViewById(R.id.stepFormulir);
        stepUnggah = findViewById(R.id.stepUnggah);
        stepKonfirmasi = findViewById(R.id.stepKonfirmasi);
        lineFormulirToUnggah = findViewById(R.id.lineFormulirToUnggah);
        lineUnggahToKonfirmasi = findViewById(R.id.lineUnggahToKonfirmasi);

        // Set warna awal untuk langkah pertama
        setStepActive(currentStep);

        btnNext.setOnClickListener(v -> {
            // Memeriksa apakah semua field telah diisi sebelum melanjutkan
            String nik = etNik.getText().toString().trim();
            String nama = etNama.getText().toString().trim();
            String alamat = etAlamat.getText().toString().trim();
            String nomorTelepon = etNomorTelepon.getText().toString().trim();
            String luasLahan = etLuasLahan.getText().toString().trim();

            if (nik.isEmpty() || nama.isEmpty() || alamat.isEmpty() || nomorTelepon.isEmpty() || luasLahan.isEmpty()) {
                Toast.makeText(RegistrationActivity.this, "Silakan isi semua field!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Membuat objek RegistrationData untuk menyimpan data
            RegistrationData data = new RegistrationData(userId, nik, nama, alamat, nomorTelepon, luasLahan, null, null);

            // Simpan data ke FormActivity
            FormActivity formActivity = (FormActivity) getApplicationContext();
            formActivity.addRegistrationData(data);

            // Berpindah ke langkah berikutnya
            Intent intent = new Intent(RegistrationActivity.this, UploadActivity.class);
            startActivity(intent);
            finish(); // Menutup RegistrationActivity
        });
    }

    // Method untuk mengatur tampilan langkah yang aktif
    private void setStepActive(int step) {
        // Reset semua langkah menjadi tidak aktif
        stepFormulir.setBackgroundResource(R.drawable.circle_grey);
        stepUnggah.setBackgroundResource(R.drawable.circle_grey);
        stepKonfirmasi.setBackgroundResource(R.drawable.circle_grey);
        lineFormulirToUnggah.setBackgroundColor(getResources().getColor(R.color.Secondary));
        lineUnggahToKonfirmasi.setBackgroundColor(getResources().getColor(R.color.Secondary));

        // Aktifkan langkah sesuai dengan step yang diberikan
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

    @Override
    protected void onResume() {
        super.onResume();
        // Mengambil data registrasi dari FormActivity jika ada
        fetchDataFromFormActivity();
    }

    private void fetchDataFromFormActivity() {
        FormActivity formActivity = (FormActivity) getApplicationContext();
        registrationData = formActivity.getRegistrationList().getLast(); // Mengambil data terakhir dari linked list

        if (registrationData != null) {
            // Memasukkan data ke EditText
            etNik.setText(registrationData.getNik());
            etNama.setText(registrationData.getNama());
            etAlamat.setText(registrationData.getAlamat());
            etNomorTelepon.setText(registrationData.getNomorTelepon());
            etLuasLahan.setText(registrationData.getLuasLahan());
        }
    }
}
