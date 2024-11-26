package com.wongcoco.thinkwapp.admin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wongcoco.thinkwapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UploadGalleryFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageViewPreview;
    private Button buttonChooseImage, buttonUploadImage;
    private android.net.Uri imageUri;

    private FirebaseFirestore db;
    private CollectionReference galleryRef;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    public UploadGalleryFragment() {
        // Diperlukan kosong untuk Fragment
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Jika diperlukan, lakukan setup Fragment di sini
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate layout fragment
        View rootView = inflater.inflate(R.layout.fragment_upload_gallery, container, false);

        // Inisialisasi UI
        imageViewPreview = rootView.findViewById(R.id.imageViewPreview);
        buttonChooseImage = rootView.findViewById(R.id.buttonChooseImage);
        buttonUploadImage = rootView.findViewById(R.id.buttonUploadImage);

        // Inisialisasi Firestore
        db = FirebaseFirestore.getInstance();
        galleryRef = db.collection("gallery_images");  // Koleksi Firestore untuk gambar

        // Inisialisasi Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("gallery_images");  // Menyimpan gambar dalam folder gallery_images

        // Pilih gambar dari galeri
        buttonChooseImage.setOnClickListener(v -> openFileChooser());

        // Unggah gambar ke Firebase Storage dan Firestore
        buttonUploadImage.setOnClickListener(v -> {
            if (imageUri != null) {
                uploadImageToFirebaseStorage();
            } else {
                Toast.makeText(getContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void openFileChooser() {
        android.content.Intent intent = new android.content.Intent();
        intent.setType("image/*");
        intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (imageUri != null) {
                imageViewPreview.setImageURI(imageUri);
            } else {
                Toast.makeText(getContext(), "Invalid image URI", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Image selection failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToFirebaseStorage() {
        try {
            // Memastikan imageUri tidak null
            if (imageUri == null) {
                Toast.makeText(getContext(), "Image URI is null", Toast.LENGTH_SHORT).show();
                return;
            }

            // Mendapatkan referensi ke Firebase Storage
            StorageReference fileReference = storageRef.child(System.currentTimeMillis() + ".jpg");

            // Upload gambar ke Firebase Storage
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Gambar berhasil diupload
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Menyimpan URL gambar ke Firestore
                            saveImageUrlToFirestore(uri.toString());
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Gagal mengunggah gambar
                        Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        // Buat data untuk disimpan ke Firestore
        Map<String, Object> imageData = new HashMap<>();
        imageData.put("imageUrl", imageUrl);  // Menyimpan URL gambar
        imageData.put("timestamp", System.currentTimeMillis());  // Menyimpan waktu unggah

        // Menyimpan data ke koleksi Firestore
        galleryRef.add(imageData)
                .addOnSuccessListener(documentReference -> {
                    // Berhasil menyimpan
                    Toast.makeText(getContext(), "Image saved to Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Gagal menyimpan
                    Toast.makeText(getContext(), "Error saving to Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
