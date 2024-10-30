package com.wongcoco.thinkwapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;

    private EditText emailField, passwordField;
    private Button signInButton, verifyOtpButton;
    private LinearLayout googleSignInButton;
    private TextView registerText;

    private String generatedOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        signInButton = findViewById(R.id.signInButton);
        googleSignInButton = findViewById(R.id.google_layout);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);
        registerText = findViewById(R.id.register_text);

        signInButton.setOnClickListener(view -> signInWithEmail());
        googleSignInButton.setOnClickListener(view -> signInWithGoogle());
        verifyOtpButton.setOnClickListener(view -> showOtpDialog());

        registerText.setOnClickListener(view -> startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithEmail() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Simpan email ke SharedPreferences
                            saveEmailToPreferences(email);
                            sendOtp(user);
                        }
                    } else {
                        Toast.makeText(SignInActivity.this, "Autentikasi Gagal.", Toast.LENGTH_SHORT).show();
                    }
                });
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
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("SignInActivity", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Simpan email ke SharedPreferences
                            saveEmailToPreferences(user.getEmail());
                            sendOtp(user);
                        }
                    } else {
                        Toast.makeText(SignInActivity.this, "Login dengan Google gagal.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveEmailToPreferences(String email) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_email", email);
        editor.apply();
    }

    private void sendOtp(FirebaseUser user) {
        generatedOtp = generateOtp();
        String email = user.getEmail();

        Log.d("SignInActivity", "Generated OTP: " + generatedOtp);
        sendEmailOtp(email, generatedOtp);
        showOtpDialog();
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
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
        String mailjetCredentials = "e17b3831947f53a97bc8cf4043e07a9f:13a322d327fb702d2e9099d34136ea35";

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
                runOnUiThread(() -> Toast.makeText(SignInActivity.this, "Gagal mengirim OTP. Coba lagi.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("Mailjet", "Email sent successfully to " + email);
                } else {
                    Log.e("Mailjet", "Failed to send email: " + response.message());
                }
            }
        });
    }

    private void showOtpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Masukkan OTP");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        int boxSize = 80, boxMargin = 16;

        EditText[] otpInputs = new EditText[6];
        for (int i = 0; i < 6; i++) {
            otpInputs[i] = createOtpEditText(i, otpInputs);
            layout.addView(otpInputs[i]);
        }

        builder.setView(layout);
        builder.setPositiveButton("Verifikasi", (dialog, which) -> verifyEnteredOtp(otpInputs));
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private EditText createOtpEditText(int index, EditText[] otpInputs) {
        EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setGravity(Gravity.CENTER);
        editText.setWidth(80);
        editText.setHeight(80);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1 && index < otpInputs.length - 1) {
                    otpInputs[index + 1].requestFocus();
                } else if (s.length() == 0 && index > 0) {
                    otpInputs[index - 1].requestFocus();
                }
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
        return editText;
    }

    private void verifyEnteredOtp(EditText[] otpInputs) {
        StringBuilder enteredOtp = new StringBuilder();
        for (EditText otpInput : otpInputs) {
            enteredOtp.append(otpInput.getText().toString());
        }

        if (enteredOtp.toString().equals(generatedOtp)) {
            Toast.makeText(this, "OTP berhasil diverifikasi", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "OTP salah. Coba lagi.", Toast.LENGTH_SHORT).show();
        }
    }
}
