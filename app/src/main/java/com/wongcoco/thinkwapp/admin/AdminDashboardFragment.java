package com.wongcoco.thinkwapp.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.wongcoco.thinkwapp.R;

import java.util.HashMap;
import java.util.Map;

public class AdminDashboardFragment extends Fragment {
    private static final String TAG = "AdminDashboardFragment";
    private TextView tvTotalRegistrations, tvTotalUsers;
    private TableLayout tableLayoutRegistrations;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize Views
        tvTotalRegistrations = view.findViewById(R.id.tvTotalRegistrations);
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        tableLayoutRegistrations = view.findViewById(R.id.tableLayoutRegistrations);

        // Fetch data from Firestore
        fetchDataFromFirestore();

        return view;
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
                TableRow headerRow = new TableRow(requireContext());
                headerRow.setBackgroundColor(getResources().getColor(R.color.Primary));
                addTableHeader(headerRow, "Nama");
                addTableHeader(headerRow, "NIK");
                addTableHeader(headerRow, "Alamat");
                addTableHeader(headerRow, "Nomor Telepon");
                addTableHeader(headerRow, "Status");
                addTableHeader(headerRow, "Action");
                tableLayoutRegistrations.addView(headerRow);

                for (QueryDocumentSnapshot document : task.getResult()) {
                    TableRow row = new TableRow(requireContext());

                    String userId = document.getId(); // Ambil ID user
                    addTableData(row, document.getString("nama"));
                    addTableData(row, document.getString("nik"));
                    addTableData(row, document.getString("alamat"));
                    addTableData(row, document.getString("nomorTelepon"));

                    // Fetch status bantuan
                    fetchStatusForUser(userId, row);

                    // Tombol Edit
                    Button btnEdit = new Button(requireContext());
                    btnEdit.setText("Edit");
                    btnEdit.setOnClickListener(v -> editRegistration(userId));
                    row.addView(btnEdit);

                    // Tombol Delete
                    Button btnDelete = new Button(requireContext());
                    btnDelete.setText("Delete");
                    btnDelete.setOnClickListener(v -> deleteRegistration(userId));
                    row.addView(btnDelete);

                    // Tombol Beri Bantuan
                    Button btnBeriBantuan = new Button(requireContext());
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
        TextView header = new TextView(requireContext());
        header.setText(headerText);
        header.setTextColor(getResources().getColor(android.R.color.white));
        header.setTextSize(14);
        header.setPadding(40, 8, 40, 8);
        row.addView(header);
    }

    private void addTableData(TableRow row, String data) {
        TextView tvData = new TextView(requireContext());
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

    private void beriBantuan(String userId, String nama) {
        // Pilihan bantuan
        String[] bantuanOptions = {"Uang Tunai", "Bibit pohon", "Pupuk", "Bantuan telah selesai"};

        // Dialog untuk memilih jenis bantuan
        new AlertDialog.Builder(requireContext())
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
                            updateBantuan(bantuanId, jenisBantuan);
                        }
                    } else {
                        // Jika belum ada, buat data bantuan baru
                        addNewBantuan(userId, nama, jenisBantuan);
                    }
                });
    }

    private void addNewBantuan(String userId, String nama, String jenisBantuan) {
        Map<String, Object> bantuan = new HashMap<>();
        bantuan.put("userId", userId);
        bantuan.put("nama", nama);
        bantuan.put("status", jenisBantuan);

        firestore.collection("bantuan")
                .add(bantuan)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Bantuan added successfully");
                    Toast.makeText(getContext(), "Bantuan diberikan: " + jenisBantuan, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding bantuan", e);
                    Toast.makeText(getContext(), "Gagal memberikan bantuan", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateBantuan(String bantuanId, String jenisBantuan) {
        DocumentReference bantuanRef = firestore.collection("bantuan").document(bantuanId);
        bantuanRef.update("status", jenisBantuan)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Bantuan updated successfully");
                    Toast.makeText(getContext(), "Bantuan diperbarui menjadi: " + jenisBantuan, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating bantuan", e);
                    Toast.makeText(getContext(), "Gagal memperbarui bantuan", Toast.LENGTH_SHORT).show();
                });
    }
}
