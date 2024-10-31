package com.wongcoco.thinkwapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; // Tambahkan ini
import androidx.fragment.app.Fragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Inisialisasi FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // Konfigurasi GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Ambil email pengguna yang sedang login
        FirebaseUser user = mAuth.getCurrentUser();
        TextView emailTxt = view.findViewById(R.id.emailTxt);
        if (user != null) {
            String email = user.getEmail();
            emailTxt.setText("   " + email);
        } else {
            emailTxt.setText("Email: Belum login");
        }

        // Inisialisasi tombol Log Out
        ImageView btnLogout = view.findViewById(R.id.logout);
        btnLogout.setOnClickListener(v -> {
            showLogoutConfirmationDialog(); // Tampilkan dialog konfirmasi saat logout
        });

        // Inisialisasi tombol Panduan
        ImageView btnPanduan = view.findViewById(R.id.panduan);
        btnPanduan.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PanduanActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Konfirmasi Logout");
        builder.setMessage("Apakah Anda yakin ingin logout?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logout(); // Jika pengguna mengonfirmasi, panggil metode logout
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Tutup dialog jika tidak
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show(); // Tampilkan dialog konfirmasi
    }

    private void logout() {
        // Lakukan proses logout untuk FirebaseAuth
        mAuth.signOut();

        // Logout akun Google jika pengguna login dengan Google
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Logout berhasil, kembali ke StartActivity
                Intent intent = new Intent(getActivity(), StartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                requireActivity().finish(); // Tutup aktivitas saat ini agar tidak dapat kembali ke sini
            } else {
                // Tangani kegagalan logout
                Toast.makeText(getActivity(), "Logout gagal. Coba lagi.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
