package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_KTP = 1; // Kode untuk memilih gambar KTP
    private static final int PICK_IMAGE_LAHAN = 2; // Kode untuk memilih gambar lahan

    private ImageView imgKTP, imgLahan;
    private Button btnNext;
    private DoubleLinkedList registrationList;
    private Uri uriKTP, uriLahan; // Menyimpan URI gambar yang dipilih

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        imgKTP = findViewById(R.id.imgKTP);
        imgLahan = findViewById(R.id.imgLahan);
        btnNext = findViewById(R.id.btnNext);

        // Mengambil data dari LinkedList
        registrationList = ((FormActivity) getApplication()).getRegistrationList();

        imgKTP.setOnClickListener(v -> openGallery(PICK_IMAGE_KTP));
        imgLahan.setOnClickListener(v -> openGallery(PICK_IMAGE_LAHAN));

        btnNext.setOnClickListener(v -> {
            if (uriKTP != null && uriLahan != null) {
                // Simpan URI ke dalam LinkedList (atau data lain sesuai kebutuhan)
                RegistrationData registrationData = new RegistrationData(
                        "NIK", // Ganti dengan nilai NIK yang sesuai
                        "Nama", // Ganti dengan nilai nama yang sesuai
                        "Alamat", // Ganti dengan nilai alamat yang sesuai
                        "Nomor Telepon", // Ganti dengan nilai nomor telepon yang sesuai
                        "Luas Lahan", // Ganti dengan nilai luas lahan yang sesuai
                        uriKTP.toString(),
                        uriLahan.toString()
                );
                registrationList.add(registrationData); // Simpan data registrasi
                Intent confirmIntent = new Intent(UploadActivity.this, ConfirmationActivity.class);
                startActivity(confirmIntent);
            } else {
                Toast.makeText(UploadActivity.this, "Silakan unggah semua gambar terlebih dahulu!", Toast.LENGTH_SHORT).show();
            }
        });
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
            if (requestCode == PICK_IMAGE_KTP) {
                uriKTP = imageUri;
                imgKTP.setImageURI(imageUri); // Tampilkan gambar KTP yang dipilih
            } else if (requestCode == PICK_IMAGE_LAHAN) {
                uriLahan = imageUri;
                imgLahan.setImageURI(imageUri); // Tampilkan gambar lahan yang dipilih
            }
        }
    }
}
