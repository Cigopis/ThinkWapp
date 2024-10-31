package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Temukan TextView registerClick
        TextView registerClick = view.findViewById(R.id.registerClick);

        // Set OnClickListener untuk menangani klik
        registerClick.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegistrationActivity.class); // Buat intent menuju FormActivity
            startActivity(intent); // Mulai aktivitas
        });

        return view;
    }
}
