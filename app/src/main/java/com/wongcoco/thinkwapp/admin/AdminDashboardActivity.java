package com.wongcoco.thinkwapp.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.wongcoco.thinkwapp.R;

import java.util.HashMap;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {
    private static final String TAG = "AdminDashboardActivity";
    private TextView tvTotalRegistrations, tvTotalUsers;
    private TableLayout tableLayoutRegistrations;
    private FirebaseFirestore firestore;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize Views
        tvTotalRegistrations = findViewById(R.id.tvTotalRegistrations);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tableLayoutRegistrations = findViewById(R.id.tableLayoutRegistrations);

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        // Initialize Toolbar and set it as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up ActionBarDrawerToggle
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState(); // Sync the toggle state

        // Set up Navigation Drawer item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navRegistrations) {
                Toast.makeText(this, "Navigating to Registrations", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.navEditAdmin) {
                Toast.makeText(this, "Navigating to Edit Admin", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.navUsers) {
                Toast.makeText(this, "Navigating to Users", Toast.LENGTH_SHORT).show();
            }

            // Fetch data from Firestore
            fetchDataFromFirestore();

            // Close the drawer after selecting an item
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        // Fetch data from Firestore
        fetchDataFromFirestore();
    }

    private void fetchDataFromFirestore() {
        CollectionReference registrationsRef = firestore.collection("registrations");

        // Fetch data from the "registrations" collection
        registrationsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int registrationCount = task.getResult().size();
                tvTotalRegistrations.setText("Total Registrations: " + registrationCount);

                tableLayoutRegistrations.removeAllViews();

                // Add header to table
                TableRow headerRow = new TableRow(this);
                headerRow.setBackgroundColor(getResources().getColor(R.color.Primary));
                addTableHeader(headerRow, "Nama");
                addTableHeader(headerRow, "NIK");
                addTableHeader(headerRow, "Alamat");
                addTableHeader(headerRow, "Nomor Telepon");
                addTableHeader(headerRow, "Status");
                addTableHeader(headerRow, "Action");
                tableLayoutRegistrations.addView(headerRow);

                for (QueryDocumentSnapshot document : task.getResult()) {
                    TableRow row = new TableRow(this);

                    String userId = document.getId(); // Ambil ID user
                    addTableData(row, document.getString("nama"));
                    addTableData(row, document.getString("nik"));
                    addTableData(row, document.getString("alamat"));
                    addTableData(row, document.getString("nomorTelepon"));

                    // Fetch status bantuan
                    fetchStatusForUser(userId, row);

                    // Tombol Edit
                    Button btnEdit = new Button(this);
                    btnEdit.setText("Edit");
                    btnEdit.setOnClickListener(v -> editRegistration(userId));
                    row.addView(btnEdit);

                    // Tombol Delete
                    Button btnDelete = new Button(this);
                    btnDelete.setText("Delete");
                    btnDelete.setOnClickListener(v -> deleteRegistration(userId));
                    row.addView(btnDelete);

                    // Tombol Beri Bantuan
                    Button btnBeriBantuan = new Button(this);
                    btnBeriBantuan.setText("Beri Bantuan");
                    btnBeriBantuan.setOnClickListener(v -> beriBantuan(userId, document.getString("nama")));
                    row.addView(btnBeriBantuan);

                    tableLayoutRegistrations.addView(row);
                }
            } else {
                Log.w(TAG, "Error getting registrations.", task.getException());
            }
        });
    }

    private void fetchStatusForUser(String userId, TableRow row) {
        CollectionReference bantuanRef = firestore.collection("bantuan");

        bantuanRef.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        String status = "Tidak Ada Bantuan";
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            status = document.getString("status"); // Ambil kolom status
                            break; // Ambil status pertama saja
                        }
                        addTableData(row, status);
                    } else {
                        addTableData(row, "Belum Ada Bantuan");
                    }
                })
                .addOnFailureListener(e -> {
                    addTableData(row, "Error");
                    Log.w(TAG, "Error fetching bantuan status for userId: " + userId, e);
                });
    }


    private void addTableHeader(TableRow row, String headerText) {
        TextView header = new TextView(this);
        header.setText(headerText);
        header.setTextColor(getResources().getColor(android.R.color.white));
        header.setTextSize(14);
        header.setPadding(40, 8, 40, 8);
        row.addView(header);
    }

    private void addTableData(TableRow row, String data) {
        TextView tvData = new TextView(this);
        tvData.setText(data);
        tvData.setPadding(16, 8, 16, 8);
        row.addView(tvData);
    }

    private void editRegistration(String documentId) {
        Log.d(TAG, "Edit registration with ID: " + documentId);
    }

    private void deleteRegistration(String documentId) {
        DocumentReference docRef = firestore.collection("registrations").document(documentId);
        docRef.delete().addOnSuccessListener(aVoid -> {
            Log.d(TAG, "DocumentSnapshot successfully deleted!");
            fetchDataFromFirestore();
        }).addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void beriBantuan(String userId, String nama) {
        // Pilihan bantuan
        String[] bantuanOptions = {"Uang Tunai", "Bibit pohon", "Pupuk", "Bantuan telah selesai"};

        // Dialog untuk memilih jenis bantuan
        new AlertDialog.Builder(this)
                .setTitle("Pilih Jenis Bantuan")
                .setItems(bantuanOptions, (dialog, which) -> {
                    String jenisBantuan = bantuanOptions[which];
                    simpanBantuan(userId, nama, jenisBantuan);
                })
                .setNegativeButton("Batal", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void simpanBantuan(String userId, String nama, String jenisBantuan) {
        // Cek apakah sudah ada data bantuan untuk pengguna tersebut
        CollectionReference bantuanRef = firestore.collection("bantuan");

        // Cari dokumen bantuan yang sudah ada untuk pengguna
        bantuanRef.whereEqualTo("userId", userId)
                .whereEqualTo("status", "Diberikan") // Menyaring hanya bantuan yang sudah diberikan
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Jika sudah ada bantuan, lakukan update
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String bantuanId = document.getId(); // Ambil ID bantuan yang ada
                            updateBantuan(bantuanId, jenisBantuan); // Perbarui data bantuan
                        }
                    } else {
                        // Jika belum ada bantuan, buat data baru
                        saveNewBantuan(userId, nama, jenisBantuan);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memeriksa data bantuan", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error checking existing bantuan", e);
                });
    }

    private void saveNewBantuan(String userId, String nama, String jenisBantuan) {
        // Data bantuan baru yang akan disimpan
        Map<String, Object> bantuanData = new HashMap<>();
        String bantuanId = java.util.UUID.randomUUID().toString(); // Generate ID unik untuk bantuan
        bantuanData.put("bantuanId", bantuanId);
        bantuanData.put("userId", userId);
        bantuanData.put("nama", nama);
        bantuanData.put("jenisBantuan", jenisBantuan);
        bantuanData.put("adminId", "admin123"); // Ganti dengan ID admin yang sesuai
        bantuanData.put("waktuBantuan", System.currentTimeMillis());
        bantuanData.put("status", "Diberikan");

        // Simpan data bantuan baru
        firestore.collection("bantuan").document(bantuanId)
                .set(bantuanData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Bantuan Berhasil Diberikan: " + jenisBantuan, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Bantuan dicatat dengan ID: " + bantuanId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal Memberikan Bantuan", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error mencatat bantuan", e);
                });
    }

    private void updateBantuan(String bantuanId, String jenisBantuan) {
        // Data bantuan yang akan diperbarui
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("jenisBantuan", jenisBantuan);
        updateData.put("status", "Diberikan");
        updateData.put("waktuBantuan", System.currentTimeMillis());

        // Perbarui data bantuan di Firestore
        firestore.collection("bantuan").document(bantuanId)
                .update(updateData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Bantuan berhasil diperbarui: " + jenisBantuan, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Bantuan diperbarui dengan ID: " + bantuanId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal Memperbarui Bantuan", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error memperbarui bantuan", e);
                });
    }



}
