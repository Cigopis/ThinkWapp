package com.wongcoco.thinkwapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class RoomActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private ImageButton buttonSend;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String receiverPhoneNumber;
    private String receiverUid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_room);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Ambil nomor telepon dan ID penerima dari Intent
        receiverPhoneNumber = getIntent().getStringExtra("RECEIVER_PHONE_NUMBER");
        receiverUid = getIntent().getStringExtra("RECEIVER_UID");

        editTextMessage = findViewById(R.id.edit_text_message);
        buttonSend = findViewById(R.id.button_send);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(RoomActivity.this, "Anda harus login terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;  // Keluar dari metode jika pengguna belum login
        }


        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextMessage.getText().toString().trim();
                if (!messageText.isEmpty() && receiverUid != null) {
                    sendMessage(messageText);
                    editTextMessage.setText("");
                } else {
                    Toast.makeText(RoomActivity.this, "Pesan tidak boleh kosong atau penerima tidak ditemukan", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendMessage(String messageText) {
        String senderId = mAuth.getCurrentUser().getUid();
        String senderPhoneNumber = mAuth.getCurrentUser().getPhoneNumber();

        // Pastikan nomor telepon pengirim tersedia
        if (senderPhoneNumber == null) {
            Toast.makeText(RoomActivity.this, "Nomor telepon pengirim tidak tersedia", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pastikan receiverUid dan receiverPhoneNumber valid
        if (receiverUid != null && receiverPhoneNumber != null) {
            Message message = new Message(senderId, senderPhoneNumber, receiverPhoneNumber, messageText, System.currentTimeMillis());
            firestore.collection("messages")
                    .add(message)
                    .addOnSuccessListener(documentReference -> {
                        // Setelah berhasil mengirim, navigasi ke MessageActivity untuk melihat percakapan
                        Intent intent = new Intent(RoomActivity.this, MessageActivity.class);
                        intent.putExtra("receiverPhoneNumber", receiverPhoneNumber);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        // Log untuk membantu debugging
                        Log.e("RoomActivity", "Gagal mengirim pesan: ", e);
                        Toast.makeText(RoomActivity.this, "Gagal mengirim pesan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(RoomActivity.this, "Receiver tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }


}


