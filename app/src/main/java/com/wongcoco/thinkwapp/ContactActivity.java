package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactActivity extends AppCompatActivity {

    private EditText searchContactInput;
    private ListView contactListView;
    private FloatingActionButton fabAddContact;
    private ContactAdapter contactAdapter;
    private List<String> contactList;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String userPhoneNumber;
    private Map<String, String> userIdMap; // Menyimpan nomor telepon dan ID pengguna

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Inisialisasi Firebase Auth dan Firestore
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userIdMap = new HashMap<>(); // Inisialisasi map

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
        fabAddContact.setOnClickListener(v -> {
            // Fitur untuk menambah kontak baru
            Toast.makeText(ContactActivity.this, "Tambah Kontak Baru", Toast.LENGTH_SHORT).show();
        });

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

        // Menangani klik pada item kontak
        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Ambil nomor telepon dari kontak yang dipilih
                String selectedContact = contactList.get(position);
                String[] contactDetails = selectedContact.split(" - ");
                String receiverPhoneNumber = contactDetails[1]; // Nomor telepon

                // Ambil ID pengguna dari map berdasarkan nomor telepon
                String receiverId = userIdMap.get(receiverPhoneNumber);

                if (receiverId != null) {
                    // Kirim nomor telepon dan ID penerima ke RoomActivity
                    Intent intent = new Intent(ContactActivity.this, RoomActivity.class);
                    intent.putExtra("RECEIVER_PHONE_NUMBER", receiverPhoneNumber);
                    intent.putExtra("RECEIVER_UID", receiverId);  // Kirim ID penerima juga
                    startActivity(intent);
                } else {
                    Toast.makeText(ContactActivity.this, "ID pengguna tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }
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
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal mengambil data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void searchContacts(String queryText) {
        CollectionReference contactsRef = firestore.collection("registrations");

        Query query = contactsRef.whereGreaterThanOrEqualTo("nomorTelepon", queryText)
                .whereLessThanOrEqualTo("nomorTelepon", queryText + "\uf8ff"); // Pencarian menggunakan query text

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                contactList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String name = document.getString("nama");
                    String phone = document.getString("nomorTelepon");
                    String userId = document.getId();  // Ambil ID pengguna

                    // Format kontak menjadi nama dan nomor telepon
                    contactList.add(name + " - " + phone);

                    // Simpan ID pengguna untuk digunakan nanti
                    userIdMap.put(phone, userId); // Simpan nomor telepon dan ID pengguna
                }
                if (contactList.isEmpty()) {
                    contactList.add("Kontak tidak ditemukan");
                }
                contactAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ContactActivity.this, "Gagal mencari kontak", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
