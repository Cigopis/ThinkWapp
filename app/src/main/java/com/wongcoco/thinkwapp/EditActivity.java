package com.wongcoco.thinkwapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private EditText etUserId, etName, etNik, etPhone, etAddress, etLandArea;
    private Button btnEdit, btnSave, btnAmbilLokasi;
    private FirebaseFirestore firestore;
    private FusedLocationProviderClient locationProvider;
    private boolean isEditable = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Inisialisasi View
        etUserId = findViewById(R.id.et_user_id);
        etName = findViewById(R.id.et_name);
        etNik = findViewById(R.id.et_nik);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        etLandArea = findViewById(R.id.et_land_area);
        btnEdit = findViewById(R.id.btn_edit);
        btnSave = findViewById(R.id.btn_save);
        btnAmbilLokasi = findViewById(R.id.btnAmbilLokasi);

        // Inisialisasi Firebase dan Location Provider
        firestore = FirebaseFirestore.getInstance();
        locationProvider = LocationServices.getFusedLocationProviderClient(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        // Ambil userId dari Intent
        String userId = getIntent().getStringExtra("USER_ID");
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "User ID tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Muat data awal
        loadUserData(userId);
        toggleFields(isEditable);
        btnAmbilLokasi.setVisibility(isEditable ? View.VISIBLE : View.GONE);

        btnEdit.setOnClickListener(v -> {
            isEditable = !isEditable;
            toggleFields(isEditable);
            btnSave.setVisibility(isEditable ? View.VISIBLE : View.GONE);
            btnAmbilLokasi.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        });

        btnSave.setOnClickListener(v -> {
            saveUserData(userId);
        });

        btnAmbilLokasi.setOnClickListener(v -> {
            requestLocationPermission();
        });
    }

    private void loadUserData(String userId) {
        progressDialog.show();
        firestore.collection("registrations").document(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    progressDialog.dismiss();
                    if (snapshot.exists()) {
                        etUserId.setText(snapshot.getId());
                        etName.setText(snapshot.getString("nama"));
                        etNik.setText(snapshot.getString("nik"));
                        etPhone.setText(snapshot.getString("nomorTelepon"));
                        etAddress.setText(snapshot.getString("alamat"));
                        etLandArea.setText(snapshot.getString("luasLahan"));
                    } else {
                        Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserData(String userId) {
        progressDialog.show();
        Map<String, Object> data = new HashMap<>();
        data.put("nama", etName.getText().toString());
        data.put("nik", etNik.getText().toString());
        data.put("nomorTelepon", etPhone.getText().toString());
        data.put("alamat", etAddress.getText().toString());
        data.put("luasLahan", etLandArea.getText().toString());

        firestore.collection("registrations").document(userId)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    toggleFields(false);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show();
                });
    }

    private void toggleFields(boolean enable) {
        etName.setEnabled(enable);
        etNik.setEnabled(enable);
        etPhone.setEnabled(enable);
        etAddress.setEnabled(enable);
        etLandArea.setEnabled(enable);
        btnEdit.setText(enable ? "Batal" : "Edit");
        etName.setFocusableInTouchMode(enable); // Pastikan bisa fokus
        etNik.setFocusableInTouchMode(enable);
        etPhone.setFocusableInTouchMode(enable);
        etAddress.setFocusableInTouchMode(enable);
        etLandArea.setFocusableInTouchMode(enable);
        btnAmbilLokasi.setEnabled(enable);
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchCurrentLocation();
        }
    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationProvider.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        getAddressFromLocation(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(this, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Gagal mendapatkan lokasi", Toast.LENGTH_SHORT).show());
    }

    private void getAddressFromLocation(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses != null && !addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                etAddress.setText(address);
                Toast.makeText(this, "Lokasi berhasil diambil", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Alamat tidak ditemukan", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Gagal mengonversi lokasi ke alamat", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation();
            } else {
                Toast.makeText(this, "Izin lokasi diperlukan", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
