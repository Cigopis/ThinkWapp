package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MessageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        // Tombol untuk navigasi ke halaman kontak
        ImageView btnCariKontak = view.findViewById(R.id.carikontak);
        btnCariKontak.setOnClickListener(v -> {
            // Navigasi ke halaman kontak
            Intent intent = new Intent(getActivity(), MessageActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
