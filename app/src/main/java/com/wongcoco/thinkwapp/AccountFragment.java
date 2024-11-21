package com.wongcoco.thinkwapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsetsController;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Inisialisasi FirebaseAuth dan Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });


        setNotchColor();

        // Animasi Slide Up untuk backgroundCard
        LinearLayout backgroundCard = view.findViewById(R.id.backgroundCard);
        Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        backgroundCard.startAnimation(slideUp);

        // Inisialisasi View lainnya
        bantuanTxt = view.findViewById(R.id.bantuanTxt);
        emailTxt = view.findViewById(R.id.emailTxt);
        namaTv = view.findViewById(R.id.namaTv);
        ImageView gifImageView = view.findViewById(R.id.gifImageView);

        // Muat GIF menggunakan Glide
        Glide.with(this)
                .asGif()
                .load(R.drawable.pohon) // Ganti dengan nama GIF Anda di folder `res/drawable`
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

    private void setNotchColor() {
        // Ensure that the fragment is attached to an activity
        if (getActivity() != null) {
            Window window = getActivity().getWindow();

            // Mengatur status bar menjadi transparan
            window.setStatusBarColor(Color.TRANSPARENT); // Set transparan

            // Jika perangkat mendukung notch (notch-aware), kita bisa mengatur padding dan menghindari teks terhalang
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                WindowInsetsController controller = window.getInsetsController();
                if (controller != null) {
                    controller.setSystemBarsAppearance(
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    );
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Untuk Android M ke atas, status bar tetap terang
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

            // Mengatur konten agar tidak tertutup notch, memberikan padding atas
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
                    // Setelah data diperbarui, matikan animasi refresh
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    bantuanTxt.setText("Terjadi kesalahan: " + e.getMessage());
                    Log.e("FirestoreError", "Error fetching bantuan: ", e);
                    swipeRefreshLayout.setRefreshing(false);  // Matikan animasi refresh jika terjadi kesalahan
                });
    }


    private void getUserNameFromFirestore(String userId, TextView namaTv) {
        db.collection("registrations")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String name = document.getString("nama");
                        namaTv.setText(name != null ? name : "Daftar sebagai mitra terlebih dahulu!");
                        namaTv.setGravity(Gravity.START);

                    } else {
                        namaTv.setText("Daftar sebagai mitra terlebih dahulu!");

                    }
                    // Setelah data diperbarui, matikan animasi refresh
                    swipeRefreshLayout.setRefreshing(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Gagal mengambil data nama", Toast.LENGTH_SHORT).show();
                    Log.e("FirestoreError", "Error fetching user name: ", e);
                });
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

    private void refreshData() {
        // Menyegarkan data pengguna dan bantuan
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            // Mengambil nama pengguna dari Firestore
            getUserNameFromFirestore(userId, namaTv);
            // Ambil data bantuan dari Firestore
            getJenisBantuanFromFirestore();
        }

        // Matikan animasi refresh setelah data diperbarui
        swipeRefreshLayout.setRefreshing(false);
    }


}
