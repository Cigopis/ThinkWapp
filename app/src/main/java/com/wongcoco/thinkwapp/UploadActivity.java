package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_KTP = 1;
    private static final int PICK_IMAGE_LAHAN = 2;
    private static final int MAX_LAHAN_IMAGES = 4;
    private static final String FIRESTORE_COLLECTION_REGISTRATIONS = "registrations";

    private FirebaseFirestore db;

    private ImageView imgKTP;
    private TextView btnNext;
    private List<Uri> lahanUris = new ArrayList<>();
    private LinearLayout layoutFotoLahan;
    private ImageView btnTambahLahan, stepFormulir, stepUnggah, stepKonfirmasi;
    private View lineFormulirToUnggah, lineUnggahToKonfirmasi;

    private RegistrationData registrationData;
    private Uri ktpUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        db = FirebaseFirestore.getInstance();

        imgKTP = findViewById(R.id.imgKTP);
        layoutFotoLahan = findViewById(R.id.layoutFotoLahan);
        btnNext = findViewById(R.id.btnNext);
        btnTambahLahan = findViewById(R.id.tambahFoto);

        // Inisialisasi views untuk langkah
        stepFormulir = findViewById(R.id.stepFormulir);
        stepUnggah = findViewById(R.id.stepUnggah);
        stepKonfirmasi = findViewById(R.id.stepKonfirmasi);
        lineFormulirToUnggah = findViewById(R.id.lineFormulirToUnggah);
        lineUnggahToKonfirmasi = findViewById(R.id.lineUnggahToKonfirmasi);

        // Menandai langkah yang aktif
        setStepActive(2);

        // Mengambil data registrasi dari FormActivity
        registrationData = ((FormActivity) getApplication()).getRegistrationList().getLast();

        imgKTP.setOnClickListener(v -> openGallery(PICK_IMAGE_KTP));

        btnTambahLahan.setOnClickListener(v -> {
            if (lahanUris.size() < MAX_LAHAN_IMAGES) {
                openGallery(PICK_IMAGE_LAHAN);
            } else {
                Toast.makeText(UploadActivity.this, "Maksimal 4 foto lahan", Toast.LENGTH_SHORT).show();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (registrationData == null) {
                Toast.makeText(UploadActivity.this, "Data registrasi tidak ditemukan", Toast.LENGTH_SHORT).show();
                return; // Exit early if registrationData is null
            }

            if (ktpUri != null && !lahanUris.isEmpty()) {
                // Set URI KTP dan URI lahan ke data registrasi
                registrationData.setUriKTP(ktpUri.toString());
                List<String> urisArray = new ArrayList<>();
                for (Uri uri : lahanUris) {
                    urisArray.add(uri.toString());
                }
                registrationData.setUriLahan(urisArray);

                // Menyimpan data ke Firestore
                saveRegistrationDataToFirestore(registrationData);

                // Melanjutkan ke aktivitas konfirmasi
                Intent confirmIntent = new Intent(UploadActivity.this, ConfirmationActivity.class);
                startActivity(confirmIntent);
            } else {
                Toast.makeText(UploadActivity.this, "Silakan unggah semua gambar terlebih dahulu!", Toast.LENGTH_SHORT).show();
            }
        });

        // Tombol Kembali
        TextView btnPrevious = findViewById(R.id.btnPrevious);
        btnPrevious.setOnClickListener(v -> {
            Intent intent = new Intent(UploadActivity.this, RegistrationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish(); // Menutup UploadActivity agar tidak kembali ke aktivitas ini saat menekan tombol kembali
        });
    }

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

    private void openGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) { // Pastikan imageUri tidak null
                if (requestCode == PICK_IMAGE_KTP) {
                    ktpUri = imageUri; // Simpan URI KTP
                    imgKTP.setImageURI(ktpUri);
                } else if (requestCode == PICK_IMAGE_LAHAN) {
                    addLahanImage(imageUri);
                }
            }
        }
    }

    private void addLahanImage(Uri uri) {
        if (lahanUris.size() < MAX_LAHAN_IMAGES) {
            lahanUris.add(uri);
            ImageView newImage = new ImageView(this);
            newImage.setLayoutParams(new LinearLayout.LayoutParams(180, 180));
            newImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            newImage.setImageURI(uri);
            newImage.setPadding(8, 8, 8, 8);
            layoutFotoLahan.addView(newImage);

            // Sembunyikan tombol jika mencapai batas maksimal
            if (lahanUris.size() >= MAX_LAHAN_IMAGES) {
                btnTambahLahan.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(this, "Maksimal 4 foto lahan sudah diunggah", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveRegistrationDataToFirestore(RegistrationData data) {
        DocumentReference newRegistrationRef = db.collection(FIRESTORE_COLLECTION_REGISTRATIONS).document(); // Ganti dengan koleksi yang sesuai
        newRegistrationRef.set(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(UploadActivity.this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(UploadActivity.this, "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
