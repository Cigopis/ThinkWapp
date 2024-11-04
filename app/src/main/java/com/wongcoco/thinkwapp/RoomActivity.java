package com.wongcoco.thinkwapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private EditText editTextMessage;
    private ImageButton buttonSend;

    // Referensi database
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth; // Menambahkan FirebaseAuth untuk mendapatkan userId
    private String receiverId; // Tambahkan receiverId di sini

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room); // Pastikan layout ini ada

        // Inisialisasi Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Ambil receiverId dari Intent (jika diperlukan)
        receiverId = getIntent().getStringExtra("RECEIVER_ID"); // Pastikan Anda mengirimkan ID penerima dari activity sebelumnya

        recyclerView = findViewById(R.id.recycler_view_chat);
        editTextMessage = findViewById(R.id.edit_text_message);
        buttonSend = findViewById(R.id.button_send);

        // Inisialisasi data dan adapter
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Inisialisasi Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        // Set click listener untuk tombol kirim
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(receiverId); // Panggil sendMessage dengan receiverId
            }
        });
    }

    private void sendMessage(String receiverId) {
        // Ambil teks dari EditText
        String messageText = editTextMessage.getText().toString().trim();

        // Pastikan pesan tidak kosong
        if (!messageText.isEmpty()) {
            // Ambil userId dari FirebaseAuth
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(RoomActivity.this, "Anda perlu login untuk mengirim pesan", Toast.LENGTH_SHORT).show();
                return;
            }
            String senderId = currentUser.getUid();

            // Tambahkan pesan ke list
            Message message = new Message(messageText, true, receiverId); // Tambahkan receiverId
            messageList.add(message);

            // Simpan pesan ke Realtime Database
            String messageId = databaseReference.push().getKey(); // Dapatkan ID unik untuk pesan
            if (messageId != null) {
                HashMap<String, Object> messageMap = new HashMap<>();
                messageMap.put("messageId", messageId);
                messageMap.put("messageText", messageText);
                messageMap.put("senderId", senderId); // Menggunakan userId dari FirebaseAuth
                messageMap.put("receiverId", receiverId); // Menambahkan receiverId

                // Simpan ke database
                databaseReference.child(messageId).setValue(messageMap)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(RoomActivity.this, "Pesan berhasil dikirim", Toast.LENGTH_SHORT).show();
                                Log.d("RoomActivity", "Pesan berhasil disimpan dengan ID: " + messageId);
                            } else {
                                Toast.makeText(RoomActivity.this, "Gagal mengirim pesan", Toast.LENGTH_SHORT).show();
                                Log.e("RoomActivity", "Gagal menyimpan pesan: " + task.getException().getMessage());
                            }
                        });
            }

            // Beritahu adapter ada data baru
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);

            // Kosongkan EditText setelah mengirim pesan
            editTextMessage.setText("");
        }
    }
}
