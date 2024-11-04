package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends AppCompatActivity {

    private EditText searchContactInput;
    private ListView contactListView;
    private FloatingActionButton fabAddContact;
    private ContactAdapter contactAdapter;
    private List<String> contactList;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String userPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Inisialisasi Firebase Auth dan Firestore
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Inisialisasi tampilan
        searchContactInput = findViewById(R.id.searchContact_input);
        contactListView = findViewById(R.id.contact_list_view);
        fabAddContact = findViewById(R.id.fab_add_contact);

        // Inisialisasi list kontak dan adapter
        contactList = new ArrayList<>();
        contactAdapter = new ContactAdapter(this, contactList);
        contactListView.setAdapter(contactAdapter);

        // Ambil nomor telepon pengguna yang login dari Firestore
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            fetchUserPhoneNumber(userId);
        } else {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show();
        }

        // Listener untuk tombol FAB menambah kontak
        fabAddContact.setOnClickListener(v ->
                Toast.makeText(ContactActivity.this, "Tambah Kontak Baru", Toast.LENGTH_SHORT).show()
        );

        // Filter daftar kontak saat pengguna mengetik di search bar
        searchContactInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchUserPhoneNumber(String uid) {
        firestore.collection("registrations").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userPhoneNumber = documentSnapshot.getString("nomorTelepon");
                        if (userPhoneNumber != null) {
                            contactList.add("Nomor Saya: " + userPhoneNumber);
                            contactAdapter.notifyDataSetChanged(); // Perbarui adapter
                        }
                    } else {
                        Toast.makeText(this, "Data user tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal mengambil data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void searchContacts(String queryText) {
        CollectionReference contactsRef = firestore.collection("registrations");

        Query query = contactsRef.whereEqualTo("nomorTelepon", queryText);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                contactList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("nama");
                    String phone = document.getString("nomorTelepon");
                    contactList.add(name + " - " + phone);
                }
                if (contactList.isEmpty()) {
                    contactList.add("Kontak tidak ditemukan");
                }
                contactAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ContactActivity.this, "Gagal mencari kontak", Toast.LENGTH_SHORT).show();
            }
        });

        contactListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedContact = contactList.get(position); // Mendapatkan kontak yang dipilih
            Intent intent = new Intent(ContactActivity.this, RoomActivity.class);
            intent.putExtra("name", selectedContact); // Kirim nama kontak yang dipilih
            startActivity(intent);
        });

    }
}
