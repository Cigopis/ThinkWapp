package com.wongcoco.thinkwapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class GantiPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ganti_password);

        String userId = getIntent().getStringExtra("USER_ID");

        EditText etCurrentPassword = findViewById(R.id.etCurrentPassword);
        EditText etNewPassword = findViewById(R.id.etNewPassword);
        Button btnSubmit = findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            String currentPassword = etCurrentPassword.getText().toString();
            String newPassword = etNewPassword.getText().toString();

            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPassword.length() < 6) {
                Toast.makeText(this, "Password baru harus minimal 6 karakter!", Toast.LENGTH_SHORT).show();
                return;
            }

            reauthenticateAndChangePassword(userId, currentPassword, newPassword);
        });
    }

    private void reauthenticateAndChangePassword(String userId, String currentPassword, String newPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null && user.getUid().equals(userId)) {
            String email = user.getEmail();
            if (email == null) {
                Toast.makeText(this, "Email pengguna tidak ditemukan!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kredensial untuk re-autentikasi
            AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

            // Re-autentikasi pengguna
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Jika re-autentikasi berhasil, ubah password
                    updatePassword(userId, newPassword);
                } else {
                    Toast.makeText(this, "Re-authentication gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "User tidak valid atau tidak ditemukan!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePassword(String userId, String newPassword) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null && user.getUid().equals(userId)) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Perbarui informasi di Firestore
                            updateUserInFirestore(userId, newPassword);
                            Toast.makeText(this, "Password berhasil diubah!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Gagal mengganti password: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } else {
            Toast.makeText(this, "User tidak valid atau tidak ditemukan!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserInFirestore(String userId, String newPassword) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> updates = new HashMap<>();

        // Tambahkan data yang akan diperbarui
        updates.put("lastUpdated", FieldValue.serverTimestamp());
        updates.put("password", newPassword); // Simpan password baru

        db.collection("users").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Password dan informasi pengguna berhasil diperbarui!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal memperbarui Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
