package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DoneActivity extends AppCompatActivity {

    private ImageView verifiedImage;
    private TextView successMessage, thankYouMessage;
    private Button buttonFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done); // Pastikan layout ini ada

        // Inisialisasi komponen UI
        verifiedImage = findViewById(R.id.verifiedImage);
        successMessage = findViewById(R.id.successMessage);
        thankYouMessage = findViewById(R.id.thankYouMessage);
        buttonFinish = findViewById(R.id.buttonFinish);

        // Menetapkan pesan sukses
        successMessage.setText("Pendaftaran Berhasil!");
        thankYouMessage.setText("Terima kasih telah mendaftar.");

        // Menangani klik tombol selesai
        buttonFinish.setOnClickListener(v -> {
            // Arahkan kembali ke aktivitas sebelumnya (misalnya, MainActivity)
            Intent intent = new Intent(DoneActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Menutup DoneActivity
        });
    }
}
