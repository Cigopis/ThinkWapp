package com.wongcoco.thinkwapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore db;
    private TextView bantuanTxt, namaTv, emailTxt;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Inisialisasi FirebaseAuth dan Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observasi perubahan data
        sharedViewModel.getUserData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                TextView nameTextView = view.findViewById(R.id.namaTv);
                nameTextView.setText((String) data.get("nama"));
            }
        });

        setNotchColor();

        // Inisialisasi View lainnya
        bantuanTxt = view.findViewById(R.id.bantuanTxt);
        emailTxt = view.findViewById(R.id.emailTxt);
        namaTv = view.findViewById(R.id.namaTv);
        ImageView gifImageView = view.findViewById(R.id.gifImageView);

        Glide.with(this)
                .asGif()
                .load(R.drawable.pohon) // Ganti dengan nama GIF di folder `res/drawable`
                .into(gifImageView);

        // Konfigurasi GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Periksa User Login
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            emailTxt.setText("   " + user.getEmail());
            getUserNameFromFirestore(userId, namaTv);
        } else {
            emailTxt.setText("Email: Belum login");
            namaTv.setText("Nama: Belum login");
        }

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

// Ambil status checkbox dan tanggal aktivasi
        boolean isCheckboxChecked = preferences.getBoolean("checkbox_status", false);
        long activationDate = preferences.getLong("checkbox_activation_date", 0L);

// Tambahkan log untuk status checkbox saat ini
        Log.d("CheckboxStatus", "Status Checkbox: " + (isCheckboxChecked ? "Aktif" : "Tidak Aktif"));
        if (isCheckboxChecked) {
            Log.d("CheckboxStatus", "Tanggal aktivasi: " + activationDate);
        }

// Periksa apakah 7 hari telah berlalu
        if (isCheckboxChecked && System.currentTimeMillis() - activationDate > 7 * 24 * 60 * 60 * 1000) {
            // Reset status checkbox
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("checkbox_status", false);
            editor.putLong("checkbox_activation_date", 0L);
            editor.apply();

            // Tambahkan log untuk perubahan status checkbox
            Log.d("CheckboxStatus", "Checkbox di-reset ke: Tidak Aktif");

            // Paksa logout dan arahkan ke halaman login
            forceLogout();
        }


        // Ambil data bantuan dari Firestore
        getJenisBantuanFromFirestore();

        // Logout Button
        LinearLayout btnLogout = view.findViewById(R.id.logout);
        btnLogout.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in));
        });

        // Panduan Button
        LinearLayout btnPanduan = view.findViewById(R.id.panduan);
        btnPanduan.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PanduanActivity.class);
            startActivity(intent);
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in));
        });

        // Edit Button
        LinearLayout btnEdit = view.findViewById(R.id.edit);
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in));
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        boolean isCheckboxChecked = preferences.getBoolean("checkbox_status", false);
        long activationDate = preferences.getLong("checkbox_activation_date", 0L);

//        // Log status checkbox saat resume
//        logCheckboxStatus(isCheckboxChecked);

        // Periksa apakah 7 hari telah berlalu
        if (isCheckboxChecked && System.currentTimeMillis() - activationDate > 7 * 24 * 60 * 60 * 1000) {
            // Reset status checkbox
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("checkbox_status", false);
            editor.putLong("checkbox_activation_date", 0L);
            editor.apply();

//            logCheckboxStatus(false); // Log perubahan status checkbox

            // Paksa logout
            forceLogout();
        }
    }

    private void loadData() {
        db.collection("registrations").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("nama");
                        namaTv.setText(name);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show());
    }

    private void setNotchColor() {
        if (getActivity() != null) {
            Window window = getActivity().getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsetsController controller = window.getInsetsController();
                if (controller != null) {
                    controller.setSystemBarsAppearance(
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    );
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    private void getJenisBantuanFromFirestore() {
        db.collection("bantuan")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        String jenis = document.getString("jenisBantuan");
                        bantuanTxt.setText(jenis != null ? " " + jenis : "Jenis bantuan tidak tersedia.");
                    } else {
                        bantuanTxt.setText("Tidak ada data bantuan.");
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error fetching bantuan: ", e));
    }

    private void getUserNameFromFirestore(String userId, TextView namaTv) {
        db.collection("registrations").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("nama");
                        namaTv.setText(name != null ? name : "Daftar sebagai mitra terlebih dahulu!");
                    } else {
                        namaTv.setText("Daftar sebagai mitra terlebih dahulu!");
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreError", "Error fetching user name: ", e));
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireActivity())
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah Anda yakin ingin logout?")
                .setPositiveButton("Ya", (dialog, which) -> logout())
                .setNegativeButton("Tidak", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void logout() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            SharedPreferences preferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            preferences.edit().putBoolean("is_logged_in", false).apply();

            Intent intent = new Intent(getActivity(), StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void forceLogout() {
        Toast.makeText(requireContext(), "Login Anda telah kedaluwarsa. Silakan login kembali.", Toast.LENGTH_SHORT).show();
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent intent = new Intent(getActivity(), StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void checkLoginExpiration(SharedPreferences preferences) {
        boolean isCheckboxChecked = preferences.getBoolean("checkbox_status", false);
        long activationDate = preferences.getLong("checkbox_activation_date", 0L);

        // Log status checkbox saat ini
        Log.d("CheckboxStatus", "Status Checkbox: " + (isCheckboxChecked ? "Aktif" : "Tidak Aktif"));
        if (isCheckboxChecked) {
            Log.d("CheckboxStatus", "Tanggal aktivasi: " + activationDate);
        }

        // Periksa apakah 7 hari telah berlalu
        if (isCheckboxChecked && System.currentTimeMillis() - activationDate > 7 * 24 * 60 * 60 * 1000) {
            // Reset status checkbox
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("checkbox_status", false);
            editor.putLong("checkbox_activation_date", 0L);
            editor.apply();

            // Log perubahan status checkbox
            Log.d("CheckboxStatus", "Checkbox di-reset ke: Tidak Aktif");

            // Paksa logout
            forceLogout();
        }
    }
}
