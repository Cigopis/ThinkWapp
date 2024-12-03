package com.wongcoco.thinkwapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.widget.CheckBox;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore db;
    private TextView bantuanTxt, namaTv, emailTxt;
    private String userId;
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);


        imageView = view.findViewById(R.id.fotoProfile); // Ensure this ID matches your layout

        // Load the image if available in arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            String imageUrl = arguments.getString("image_url");
            if (imageUrl != null) {
                loadImage(imageUrl);
                loadImageFromInternalStorage(userId);
                imageView = view.findViewById(R.id.fotoProfile);
            }
        }

        // Inisialisasi FirebaseAuth dan Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observasi perubahan data
        sharedViewModel.getUserData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                TextView nameTextView = view.findViewById(R.id.namaTv);
                nameTextView.setText((String) data.get("nama"));

                imageView = view.findViewById(R.id.fotoProfile);
                String userId = (String) data.get("userId");
                loadImageFromInternalStorage(userId);

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
            loadImageFromInternalStorage(userId);
        } else {
            emailTxt.setText("Email: Belum login");
            namaTv.setText("Nama: Belum login");
        }

        // SharedPreferences untuk menyimpan status checkbox
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        CheckBox checkbox = view.findViewById(R.id.checkbox_loginback);

        // Set status awal checkbox
        boolean isCheckboxChecked = preferences.getBoolean("checkbox_status", false);
        checkbox.setChecked(isCheckboxChecked);

        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            if (isChecked) {
                long currentTime = System.currentTimeMillis();
                editor.putBoolean("checkbox_status", true);
                editor.putLong("checkbox_activation_date", currentTime);
                editor.apply();
                Toast.makeText(requireContext(), "Fitur aktif! Anda akan login kembali saat 7 hari berlalu.", Toast.LENGTH_SHORT).show();
                Log.d("CheckboxStatus", "Checkbox diaktifkan. Tanggal aktivasi: " + currentTime);
            } else {
                resetCheckbox(preferences);
                Toast.makeText(requireContext(), "Fitur nonaktif. Anda harus login ulang setiap sesi.", Toast.LENGTH_SHORT).show();
                Log.d("CheckboxStatus", "Checkbox dinonaktifkan.");
            }
        });

        LinearLayout cbRestartLogin = view.findViewById(R.id.cbLoginRestart);
        cbRestartLogin.setOnClickListener(v -> {
            // Toggle checkbox status
            checkbox.toggle();

            SharedPreferences.Editor editor = preferences.edit();
            boolean isChecked = checkbox.isChecked(); // Ambil status checkbox setelah toggle

            if (isChecked) {
                long currentTime = System.currentTimeMillis();
                editor.putBoolean("checkbox_status", true);
                editor.putLong("checkbox_activation_date", currentTime);
                editor.apply();
                Toast.makeText(requireContext(), "Fitur aktif! Anda akan login kembali saat 7 hari berlalu.", Toast.LENGTH_SHORT).show();
                Log.d("CheckboxStatus", "Checkbox diaktifkan. Tanggal aktivasi: " + currentTime);
            } else {
                resetCheckbox(preferences);
                Toast.makeText(requireContext(), "Fitur nonaktif. Anda harus login ulang setiap sesi.", Toast.LENGTH_SHORT).show();
                Log.d("CheckboxStatus", "Checkbox dinonaktifkan.");
            }

            // Animasi untuk klik
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in));
        });

// Untuk memeriksa status checkbox dan waktu aktivasi yang disimpan
        long activationDate = preferences.getLong("checkbox_activation_date", 0);
        Log.d("CheckboxStatus", "Waktu aktivasi yang tersimpan: " + activationDate);





        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getJenisBantuanFromFirestore(userId);


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

        // Edit Button
        LinearLayout btnGanti = view.findViewById(R.id.GantiPassword);
        btnGanti.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), GantiPasswordActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in));
        });

        checkLoginExpiration(preferences);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
        loadImageFromInternalStorage(userId);

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        checkLoginExpiration(preferences);
        boolean isCheckboxChecked = preferences.getBoolean("checkbox_status", false);
        long activationDate = preferences.getLong("checkbox_activation_date", 0L);

        // Log status checkbox saat resume
        Log.d("CheckboxStatus", "Status Checkbox: " + (isCheckboxChecked ? "Aktif" : "Tidak Aktif"));

        // Jika fitur aktif, periksa apakah 7 hari telah berlalu
        if (isCheckboxChecked) {
            if (System.currentTimeMillis() - activationDate > 7 * 24 * 60 * 60 * 1000) {
                // Reset status checkbox
                resetCheckbox(preferences);

                // Paksa logout karena waktu 7 hari telah berlalu
                forceLogout();
            }
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            String imageUrl = arguments.getString("image_url");
            if (imageUrl != null) {
                loadImage(imageUrl);
            }
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

    private void getJenisBantuanFromFirestore(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("bantuan")
                .whereEqualTo("userId", userId) // Filter berdasarkan userId
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        // Ambil dokumen pertama dari hasil query
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        String jenis = document.getString("jenisBantuan");
                        bantuanTxt.setText(jenis != null ? " " + jenis : "Jenis bantuan tidak tersedia.");
                    } else {
                        bantuanTxt.setText("Belum disetujui menjadi member, Tunggu sebentar yaa!.");
                        bantuanTxt.setTextSize(12);
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
        Toast.makeText(requireContext(), "Berhasil Logout. Silakan login kembali nanti!.", Toast.LENGTH_SHORT).show();
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

        if (isCheckboxChecked) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - activationDate > 7 * 24 * 60 * 60 * 1000) {
                // Jika waktu telah melebihi 7 hari
                resetCheckbox(preferences);
                Toast.makeText(requireContext(), "Fitur login otomatis telah kedaluwarsa. Silakan login kembali.", Toast.LENGTH_SHORT).show();
                Log.d("CheckboxStatus", "Login otomatis kedaluwarsa.");
                forceLogout();
            } else {
                Log.d("CheckboxStatus", "Login otomatis masih aktif. Tersisa waktu: " +
                        ((7 * 24 * 60 * 60 * 1000 - (currentTime - activationDate)) / (1000 * 60 * 60)) + " jam.");
            }
        } else {
            Log.d("CheckboxStatus", "Login otomatis tidak diaktifkan.");
        }
    }

    private void resetCheckbox(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("checkbox_status", false);  // Disable auto-login
        editor.remove("checkbox_activation_date");  // Remove the activation date
        editor.apply();
        Log.d("CheckboxStatus", "Checkbox status reset.");
    }

    private void loadImage(String imageUrl) {
        // Use Glide to load the image
        Glide.with(this).load(imageUrl).into(imageView);

    }
    private void loadImageFromInternalStorage(String userId) {
        File directory = new File(getContext().getFilesDir(), "profile_images");
        File imageFile = new File(directory, userId + ".jpg");

        if (imageFile.exists()) {
            // Clear Glide's cache to ensure the latest image is loaded
            Glide.get(getContext()).clearMemory();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(getContext()).clearDiskCache();
                }
            }).start();
            // Load the image from internal storage
            Glide.with(this).load(imageFile).into(imageView);
        } else {
            // Handle the case where the image does not exist
            Toast.makeText(getContext(), "Profile image not found", Toast.LENGTH_SHORT).show();
        }
    }





}
