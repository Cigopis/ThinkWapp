package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private EditText searchContactInput;
    private ListView contactListView;
    private FloatingActionButton fabAddContact;
    private ArrayAdapter<String> contactAdapter;
    private List<String> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Inisialisasi tampilan
        searchContactInput = findViewById(R.id.searchContact_input);
        contactListView = findViewById(R.id.contact_list_view);
        fabAddContact = findViewById(R.id.fab_add_contact);

        // Contoh daftar kontak (ini bisa diambil dari database pada implementasi lebih lanjut)
        contactList = new ArrayList<>();
        contactList.add("John Doe - 081234567890");
        contactList.add("Jane Smith - 082345678901");
        contactList.add("Alex Brown - 083456789012");

        // Atur adapter untuk ListView
        contactAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactList);
        contactListView.setAdapter(contactAdapter);

        // Listener untuk tombol FAB menambah kontak
        fabAddContact.setOnClickListener(v -> {
            // Tambahkan logika untuk menambah kontak baru
            Toast.makeText(ContactActivity.this, "Tambah Kontak Baru", Toast.LENGTH_SHORT).show();
        });

        // Filter daftar kontak saat pengguna mengetik di search bar
        searchContactInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactAdapter.getFilter().filter(s); // Filter daftar kontak sesuai input
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }
}
