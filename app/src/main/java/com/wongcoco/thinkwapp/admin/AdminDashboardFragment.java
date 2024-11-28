package com.wongcoco.thinkwapp.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.wongcoco.thinkwapp.R;

import java.util.HashMap;
import java.util.Map;

public class AdminDashboardFragment extends Fragment {
    private static final String TAG = "AdminDashboardFragment";
    private static final String STATUS_DIBERIKAN = "Diberikan";
    private static final String STATUS_BELUM_ADA = "Belum Ada Bantuan";

    private TextView tvTotalRegistrations;
    private TableLayout tableLayoutRegistrations;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);

        firestore = FirebaseFirestore.getInstance();
        tvTotalRegistrations = view.findViewById(R.id.tvTotalRegistrations);
        tableLayoutRegistrations = view.findViewById(R.id.tableLayoutRegistrations);

        fetchDataFromFirestore();

        return view;
    }

    private void fetchDataFromFirestore() {
        CollectionReference registrationsRef = firestore.collection("registrations");

        registrationsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int registrationCount = task.getResult().size();
                tvTotalRegistrations.setText("Total Registrations: " + registrationCount);

                tableLayoutRegistrations.removeAllViews();
                addTableHeader();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    addRegistrationRow(document);
                }
            } else {
                Log.w(TAG, "Error getting registrations.", task.getException());
            }
        });
    }

    private void addTableHeader() {
        TableRow headerRow = new TableRow(getContext());
        headerRow.setBackgroundColor(getResources().getColor(R.color.Primary));
        addTableCell(headerRow, "Nama", true);
        addTableCell(headerRow, "NIK", true);
        addTableCell(headerRow, "Alamat", true);
        addTableCell(headerRow, "Nomor Telepon", true);
        addTableCell(headerRow, "Status", true);
        addTableCell(headerRow, "Action", true);
        tableLayoutRegistrations.addView(headerRow);
    }

    private void addRegistrationRow(QueryDocumentSnapshot document) {
        TableRow row = new TableRow(getContext());

        String userId = document.getId();
        addTableCell(row, document.getString("nama"), false);
        addTableCell(row, document.getString("nik"), false);
        addTableCell(row, document.getString("alamat"), false);
        addTableCell(row, document.getString("nomorTelepon"), false);

        fetchStatusForUser(userId, row);

        addActionButton(row, "Edit", v -> editRegistration(userId));
        addActionButton(row, "Delete", v -> deleteRegistration(userId));
        addActionButton(row, "Beri Bantuan", v -> showBantuanDialog(userId, document.getString("nama")));

        tableLayoutRegistrations.addView(row);
    }

    private void fetchStatusForUser(String userId, TableRow row) {
        CollectionReference bantuanRef = firestore.collection("bantuan");

        bantuanRef.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            addTableCell(row, document.getString("status"), false);
                            return;
                        }
                    } else {
                        addTableCell(row, STATUS_BELUM_ADA, false);
                    }
                })
                .addOnFailureListener(e -> {
                    addTableCell(row, "Error", false);
                    Log.w(TAG, "Error fetching bantuan status for userId: " + userId, e);
                });
    }

    private void addTableCell(TableRow row, String text, boolean isHeader) {
        TextView cell = new TextView(getContext());
        cell.setText(text);
        cell.setPadding(16, 8, 16, 8);
        cell.setTextSize(isHeader ? 14 : 12);
        if (isHeader) {
            cell.setTextColor(getResources().getColor(android.R.color.white));
        }
        row.addView(cell);
    }

    private void addActionButton(TableRow row, String text, View.OnClickListener listener) {
        Button button = new Button(getContext());
        button.setText(text);
        button.setOnClickListener(listener);
        row.addView(button);
    }

    private void editRegistration(String userId) {
        Log.d(TAG, "Edit registration with ID: " + userId);
    }

    private void deleteRegistration(String userId) {
        firestore.collection("registrations").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Registrasi berhasil dihapus.", Toast.LENGTH_SHORT).show();
                    fetchDataFromFirestore();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Gagal menghapus registrasi.", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error deleting registration.", e);
                });
    }

    private void showBantuanDialog(String userId, String nama) {
        String[] bantuanOptions = {"Uang Tunai", "Bibit pohon", "Pupuk", "Bantuan telah selesai"};

        new AlertDialog.Builder(getContext())
                .setTitle("Pilih Jenis Bantuan")
                .setItems(bantuanOptions, (dialog, which) -> saveOrUpdateBantuan(userId, nama, bantuanOptions[which]))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void saveOrUpdateBantuan(String userId, String nama, String jenisBantuan) {
        CollectionReference bantuanRef = firestore.collection("bantuan");

        bantuanRef.whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            updateBantuan(document.getId(), jenisBantuan);
                            return;
                        }
                    } else {
                        saveNewBantuan(userId, nama, jenisBantuan);
                    }
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error checking bantuan.", e));
    }

    private void saveNewBantuan(String userId, String nama, String jenisBantuan) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("nama", nama);
        data.put("jenisBantuan", jenisBantuan);
        data.put("status", STATUS_DIBERIKAN);

        firestore.collection("bantuan").add(data)
                .addOnSuccessListener(docRef -> Toast.makeText(getContext(), "Bantuan berhasil disimpan.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w(TAG, "Error saving bantuan.", e));
    }

    private void updateBantuan(String bantuanId, String jenisBantuan) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("jenisBantuan", jenisBantuan);
        updates.put("status", STATUS_DIBERIKAN);
        updates.put("waktuBantuan", System.currentTimeMillis());

        firestore.collection("bantuan").document(bantuanId).update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Bantuan diperbarui.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Log.w(TAG, "Error updating bantuan.", e));
    }
}
