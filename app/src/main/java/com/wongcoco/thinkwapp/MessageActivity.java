package com.wongcoco.thinkwapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private EditText inputMessage;
    private FloatingActionButton sendButton;
    private ChatAdapter messageAdapter;
    private List<Message> messageList;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String receiverPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_message);

        recyclerViewMessages = findViewById(R.id.pesan_view);
        inputMessage = findViewById(R.id.input_message);
        sendButton = findViewById(R.id.send_button);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Mendapatkan nomor telepon penerima dari Intent
        receiverPhoneNumber = getIntent().getStringExtra("receiverPhoneNumber");

        messageList = new ArrayList<>();
        messageAdapter = new ChatAdapter(this, messageList, auth.getCurrentUser().getUid());

        // Mengatur RecyclerView dengan LinearLayoutManager
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        // Mengambil pesan dari Firestore
        fetchMessages();

        // Mengirim pesan saat tombol diklik
        sendButton.setOnClickListener(v -> {
            String messageText = inputMessage.getText().toString().trim();
            if (!messageText.isEmpty()) {
                sendMessage(messageText);
                inputMessage.setText("");  // Clear input field after sending
            } else {
                Toast.makeText(this, "Pesan tidak boleh kosong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMessages() {
        firestore.collection("messages")
                .whereEqualTo("receiverPhoneNumber", receiverPhoneNumber)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e("MessageActivity", "Gagal memuat pesan: " + e.getMessage(), e);
                        Toast.makeText(this, "Gagal memuat pesan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Clear previous messages
                    List<Message> newMessages = new ArrayList<>();

                    if (snapshots != null && !snapshots.isEmpty()) {
                        // Parse messages from Firestore and add to list
                        for (QueryDocumentSnapshot document : snapshots) {
                            Message message = document.toObject(Message.class);
                            newMessages.add(message);
                        }

                        // Update adapter with the new message list
                        messageAdapter.updateMessageList(newMessages);

                        // Scroll to the latest message
                        if (!newMessages.isEmpty()) {
                            recyclerViewMessages.scrollToPosition(newMessages.size() - 1);
                        }
                    } else {
                        Toast.makeText(this, "Tidak ada pesan", Toast.LENGTH_SHORT).show();
                    }
                });
    }




    private void sendMessage(String messageText) {
        // Create a new message object
        Message message = new Message(
                auth.getCurrentUser().getUid(),
                auth.getCurrentUser().getPhoneNumber(),
                receiverPhoneNumber,
                messageText,
                System.currentTimeMillis()
        );

        // Add message to Firestore
        firestore.collection("messages").add(message)
                .addOnSuccessListener(documentReference -> {
                    // Optionally handle success (e.g., update UI or database)
                    Toast.makeText(this, "Pesan terkirim", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal mengirim pesan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
