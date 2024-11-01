package com.wongcoco.thinkwapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.firebase.auth.FirebaseAuth;

public class ConfirmationActivity extends AppCompatActivity {

    private TextView tvConfirmation;
    private Button btnConfirm;
    private FirebaseFirestore db;
    private ImageView imgKTP; // Hanya satu ImageView untuk KTP
    private ImageView imgLahan; // Untuk lahan, sebaiknya gunakan RecyclerView jika banyak
    private DoubleLinkedList<RegistrationData> registrationList;
    private String userId;  // Menyimpan userId dari FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        tvConfirmation = findViewById(R.id.tvConfirmation);
        btnConfirm = findViewById(R.id.btnConfirm);
        imgKTP = findViewById(R.id.imgKTP);
        imgLahan = findViewById(R.id.imgLahan); // Untuk lahan, sebaiknya gunakan RecyclerView jika banyak

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();  // Dapatkan userId dari FirebaseAuth

        // Mengambil data dari LinkedList
        registrationList = ((FormActivity) getApplication()).getRegistrationList();
        RegistrationData data = registrationList.getLast();

        if (data != null) {
            tvConfirmation.setText("NIK: " + data.getNik() +
                    "\nNama: " + data.getNama() +
                    "\nAlamat: " + data.getAlamat() +
                    "\nNomor Telepon: " + data.getNomorTelepon() +
                    "\nLuas Lahan: " + data.getLuasLahan());

            imgKTP.setImageURI(Uri.parse(data.getUriKTP()));

            // Menampilkan gambar lahan
            List<String> uriLahanList = data.getUriLahan();
            // Jika menggunakan ImageView untuk lahan, Anda dapat menampilkan salah satu gambar
            // Contoh untuk menampilkan gambar pertama
            if (!uriLahanList.isEmpty()) {
                imgLahan.setImageURI(Uri.parse(uriLahanList.get(0))); // Menggunakan gambar pertama dari daftar
            }

            // Set userId pada data registrasi
            data.setUserId(userId);
        }

        btnConfirm.setOnClickListener(v -> saveDataToFirestore(data));
    }

    private void saveDataToFirestore(RegistrationData data) {
        if (data != null) {
            Map<String, Object> registrationMap = new HashMap<>();
            registrationMap.put("nik", data.getNik());
            registrationMap.put("nama", data.getNama());
            registrationMap.put("alamat", data.getAlamat());
            registrationMap.put("nomorTelepon", data.getNomorTelepon());
            registrationMap.put("luasLahan", data.getLuasLahan());
            registrationMap.put("uriKTP", data.getUriKTP());
            registrationMap.put("uriLahan", data.getUriLahan());
            registrationMap.put("userId", data.getUserId());

            db.collection("registrations")
                    .document(userId) // Menggunakan userId sebagai ID dokumen
                    .update(registrationMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ConfirmationActivity.this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show();
                        // Lanjutkan ke aktivitas berikutnya
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ConfirmationActivity.this, "Gagal menyimpan data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
