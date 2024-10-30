package com.wongcoco.thinkwapp;

import android.os.Bundle;
import android.widget.ExpandableListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PanduanActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private PanduanExpandableListAdapter adapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panduan);

        expandableListView = findViewById(R.id.expandableListView);
        prepareListData();

        adapter = new PanduanExpandableListAdapter(this, listDataHeader, listDataChild);
        expandableListView.setAdapter(adapter);

        // Listener untuk animasi ikon saat grup diperluas atau ditutup
        expandableListView.setOnGroupExpandListener(groupPosition -> adapter.notifyDataSetChanged());
        expandableListView.setOnGroupCollapseListener(groupPosition -> adapter.notifyDataSetChanged());
    }

    // Menyiapkan data untuk ExpandableListView
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Header
        listDataHeader.add("Mendaftarkan diri sebagai mitra thinkwood");
        listDataHeader.add("Mengunggah foto terkait foto KTP, Lahan, dsb");
        listDataHeader.add("Edit profile gagal tersimpan");
        listDataHeader.add("Cara menjadi mitra yang baik, bertanggung jawab");

        // Child data
        List<String> panduan1 = new ArrayList<>();
        panduan1.add("Setelah berhasil masuk, pilih menu registrasi di halaman beranda");

        List<String> panduan2 = new ArrayList<>();
        panduan2.add("Jika ingin mengunggah foto, usahakan file berukuran max. 100mb");

        List<String> panduan3 = new ArrayList<>();
        panduan3.add("Cek Koneksi Internet, dikarenakan proses mengedit membutuhkan koneksi stabil");

        List<String> panduan4 = new ArrayList<>();
        panduan4.add("Komitmen adalah sebuah langkah awal untuk menjalin hubungan baik dan tanggung jawab.");

        listDataChild.put(listDataHeader.get(0), panduan1);
        listDataChild.put(listDataHeader.get(1), panduan2);
        listDataChild.put(listDataHeader.get(2), panduan3);
        listDataChild.put(listDataHeader.get(3), panduan4);
    }
}
