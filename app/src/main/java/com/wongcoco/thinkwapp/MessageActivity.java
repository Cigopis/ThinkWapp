package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MessageActivity extends AppCompatActivity {

    private EditText searchInput;
    private ListView contactsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Inisialisasi komponen
        searchInput = findViewById(R.id.searchMessage_input);
        contactsListView = findViewById(R.id.pesan_view);

        // Inisialisasi Floating Action Button dan set OnClickListener
        FloatingActionButton fabContact = findViewById(R.id.fab_contact);
        fabContact.setOnClickListener(v -> {
            // Pindah ke ContactActivity
            Intent intent = new Intent(MessageActivity.this, ContactActivity.class);
            startActivity(intent);
        });

        // Implementasikan fungsi pencarian nomor HP dan tampilkan daftar kontak di sini
    }
}
