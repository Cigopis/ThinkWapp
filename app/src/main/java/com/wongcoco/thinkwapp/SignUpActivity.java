package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailField, usernameField, passwordField;
    private Button signUpButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        FirebaseApp.initializeApp(this);

        // Inisialisasi Firebase Auth dan Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inisialisasi komponen UI
        emailField = findViewById(R.id.emailField);
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        signUpButton = findViewById(R.id.signUpButton);

        // Aksi tombol daftar
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String username = usernameField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(SignUpActivity.this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                registerUser(email, username, password);
            }
        });
    }

    private void registerUser(String email, String username, String password) {
        // Validasi email
        if (email.isEmpty() || !email.contains("@")) {
            Toast.makeText(this, "Email harus valid dan mengandung '@'", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi username
        if (username.isEmpty()) {
            Toast.makeText(this, "Username tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validasi password
        if (password.length() <= 6) {
            Toast.makeText(this, "Password harus lebih dari 6 karakter", Toast.LENGTH_SHORT).show();
            return;
        }


        // Buat akun dengan email dan password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Jika pendaftaran berhasil, simpan data pengguna ke Firestore
                        String userId = mAuth.getCurrentUser().getUid();
                        saveUserToFirestore(userId, email, username, password);
                        Toast.makeText(SignUpActivity.this, "Pendaftaran berhasil", Toast.LENGTH_SHORT).show();

                        // Arahkan setelah pendaftaran
                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Pendaftaran gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToFirestore(String userId, String email, String username, String password) {
        // Buat data pengguna
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("username", username);
        userData.put("password", password); // Menyimpan password yang sudah di-hash

        // Menyimpan data pengguna ke Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(SignUpActivity.this, "Data pengguna disimpan di Firestore", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(SignUpActivity.this, "Gagal menyimpan data di Firestore", Toast.LENGTH_SHORT).show()
                );
    }


}
