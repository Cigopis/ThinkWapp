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

import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_KTP = 1;
    private static final int PICK_IMAGE_LAHAN = 2;
    private static final int MAX_LAHAN_IMAGES = 4;

    private FirebaseFirestore db;

    private ImageView imgKTP;
    private TextView btnNext, tvPrevious; // Tambahkan TextView untuk Previous
    private List<Uri> lahanUris = new ArrayList<>();
    private LinearLayout layoutFotoLahan;
    private ImageView btnTambahLahan;

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
        tvPrevious = findViewById(R.id.btnPrevious); // Inisialisasi TextView Previous
        btnTambahLahan = findViewById(R.id.tambahFoto);

        // Inisialisasi data registrasi menggunakan DoubleLinkedList
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
                return;
            }

            if (ktpUri != null && !lahanUris.isEmpty()) {
                registrationData.setUriKTP(ktpUri.toString());
                List<String> urisArray = new ArrayList<>();
                for (Uri uri : lahanUris) {
                    urisArray.add(uri.toString());
                }
                registrationData.setUriLahan(urisArray);

                setStepActive(3);
                Intent confirmIntent = new Intent(UploadActivity.this, ConfirmationActivity.class);
                startActivity(confirmIntent);
            } else {
                Toast.makeText(UploadActivity.this, "Silakan unggah semua gambar terlebih dahulu!", Toast.LENGTH_SHORT).show();
            }
        });

        // Implementasi onClickListener untuk TextView Previous
        tvPrevious.setOnClickListener(v -> {
            Intent previousIntent = new Intent(UploadActivity.this, RegistrationActivity.class);
            startActivity(previousIntent);
        });
    }

    private void setStepActive(int step) {
        // Reset semua langkah menjadi tidak aktif
        // ... (kode yang sama seperti sebelumnya)
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
}
