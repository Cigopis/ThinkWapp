package com.wongcoco.thinkwapp;

import static android.text.InputType.*;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StartActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private String generatedOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_start);

        // Inisialisasi Google SignIn
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Inisialisasi tombol "Masuk"
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(view -> startActivity(new Intent(StartActivity.this, SignInActivity.class)));

        // Inisialisasi tombol "Daftar"
        Button btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(view -> startActivity(new Intent(StartActivity.this, SignUpActivity.class)));

        mAuth = FirebaseAuth.getInstance();

        // Cek apakah pengguna sudah login
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
        }

        LinearLayout googleSignInButton = findViewById(R.id.google_icon);
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account);
            } else {
                Log.w("GoogleLogin", "Google sign in failed: account is null");
            }
        } catch (ApiException e) {
            Log.w("GoogleLogin", "Google sign in failed: " + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        sendOtp(user);
                    } else {
                        Toast.makeText(StartActivity.this, "Login dengan Google gagal.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendOtp(FirebaseUser user) {
        generatedOtp = generateOtp();
        String email = user.getEmail();

        Log.d("SignInActivity", "Generated OTP: " + generatedOtp);
        sendEmailOtp(email, generatedOtp);
        showOtpDialog();
    }

    private void sendEmailOtp(String email, String otp) {
        OkHttpClient client = new OkHttpClient();

        String json = "{"
                + "\"Messages\":[{"
                + "\"From\":{\"Email\":\"nafisarrosyid002@gmail.com\",\"Name\":\"ThinkWapp\"},"
                + "\"To\":[{\"Email\":\"" + email + "\"}],"
                + "\"Subject\":\"Kode OTP Anda\","
                + "\"TextPart\":\"Kode OTP Anda adalah: " + otp + "\""
                + "}]"
                + "}";

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));
        String mailjetCredentials = "4d4a318cd572561ff33ad47208a4d47e:81c8d40f9e376e554fed35fc18cf7920";

        Request request = new Request.Builder()
                .url("https://api.mailjet.com/v3.1/send")
                .post(body)
                .addHeader("Authorization", "Basic " + Base64.encodeToString(mailjetCredentials.getBytes(), Base64.NO_WRAP))
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Mailjet", "Error sending email", e);
                runOnUiThread(() -> Toast.makeText(StartActivity.this, "Gagal mengirim email.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("Mailjet", "Email sent successfully to " + email);
                } else {
                    Log.e("Mailjet", "Failed to send email: " + response.message());
                    runOnUiThread(() -> Toast.makeText(StartActivity.this, "Gagal mengirim email: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void showOtpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Masukkan OTP");

        // Buat layout utama untuk dialog
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(32, 32, 32, 32); // Tambahkan padding
        mainLayout.setGravity(Gravity.CENTER);


        // Tambahkan ImageView untuk GIF
        ImageView gifImageView = new ImageView(this);
        int gifSize = 300; // Ukuran GIF (width & height)
        LinearLayout.LayoutParams gifParams = new LinearLayout.LayoutParams(gifSize, gifSize);
        gifParams.gravity = Gravity.CENTER;
        gifImageView.setLayoutParams(gifParams);

        // Gunakan Glide untuk memuat GIF
        Glide.with(this)
                .asGif()
                .load(R.drawable.email4) // Ganti dengan file GIF Anda di res/drawable
                .into(gifImageView);

        mainLayout.addView(gifImageView); // Tambahkan GIF ke layout utama

        // Buat layout horizontal untuk input OTP
        LinearLayout inputLayout = new LinearLayout(this);
        inputLayout.setOrientation(LinearLayout.HORIZONTAL);
        inputLayout.setGravity(Gravity.CENTER);

        // Buat array untuk menyimpan EditText
        EditText[] otpInputs = new EditText[6];
        int boxSize = 80; // Ukuran kotak OTP
        int boxMargin = 16; // Jarak antar kotak

        for (int i = 0; i < 6; i++) {
            otpInputs[i] = new EditText(this);
            otpInputs[i].setInputType(InputType.TYPE_CLASS_NUMBER);
            otpInputs[i].setWidth(boxSize);
            otpInputs[i].setHeight(boxSize);
            otpInputs[i].setGravity(Gravity.CENTER);
            otpInputs[i].setTextSize(24);
            otpInputs[i].setPadding(5, 5, 5, 5);
            otpInputs[i].setBackgroundResource(R.drawable.otp_edittext_background);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(boxSize, boxSize);
            params.setMargins(i == 0 ? 0 : boxMargin, 0, 0, 0);
            otpInputs[i].setLayoutParams(params);

            // TextWatcher untuk memindahkan fokus
            final int index = i;
            otpInputs[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpInputs.length - 1) {
                        otpInputs[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        otpInputs[index - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            inputLayout.addView(otpInputs[i]);
        }

        mainLayout.addView(inputLayout); // Tambahkan layout input OTP ke layout utama
        builder.setView(mainLayout);

        // Tambahkan tombol konfirmasi
        builder.setPositiveButton("Verifikasi", (dialog, which) -> {
            StringBuilder enteredOtp = new StringBuilder();
            for (EditText otpInput : otpInputs) {
                enteredOtp.append(otpInput.getText().toString());
            }
            if (enteredOtp.toString().equals(generatedOtp)) {
                // Berhasil, lanjutkan ke MainActivity
                startActivity(new Intent(StartActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "OTP salah, coba lagi.", Toast.LENGTH_SHORT).show();
            }
        });

        // Tambahkan tombol batal
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());

        builder.show();
    }



    private void verifyOtp(String enteredOtp) {
        if (enteredOtp.equals(generatedOtp)) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                updateUI(user);
            }
        } else {
            Toast.makeText(this, "OTP salah, coba lagi.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Log.d("GoogleLogin", "User logged in: " + user.getEmail());
            Toast.makeText(StartActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();

            // Redirect ke MainActivity jika login sukses
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Agar tidak kembali ke halaman login
        } else {
            Log.d("GoogleLogin", "Login failed.");
            Toast.makeText(StartActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
