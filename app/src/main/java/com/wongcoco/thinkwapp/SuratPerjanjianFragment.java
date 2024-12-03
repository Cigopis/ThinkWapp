package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class SuratPerjanjianFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_surat_perjanjian, container, false);

        Button openPdfButton = view.findViewById(R.id.openPdfButton);
        ImageView gifImageView = view.findViewById(R.id.PdfGif);

        Glide.with(this)
                .asGif()
                .load(R.drawable.pdf1) // Ganti dengan nama GIF di folder `res/drawable`
                .into(gifImageView);


        // Button untuk membuka PDF dari res/raw
        openPdfButton.setOnClickListener(v -> {
            openPdfFromRaw();
        });

        return view;
    }

    private void openPdfFromRaw() {
        try {
            // Mengakses file PDF dari direktori raw
            InputStream inputStream = getResources().openRawResource(R.raw.surat_perjanjian);

            // Menyimpan file PDF sementara di memori penyimpanan aplikasi
            File file = new File(requireContext().getCacheDir(), "surat_perjanjian.pdf");

            // Menyalin InputStream ke file sementara di cache
            Utils.copyInputStreamToFile(inputStream, file);

            // Membuka PDF menggunakan aplikasi pembaca PDF
            Uri uri = FileProvider.getUriForFile(getContext(), "com.wongcoco.thinkwapp.fileprovider", file);

            // Membuat Intent untuk membuka PDF
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            // Memberikan izin kepada aplikasi lain untuk mengakses file
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
