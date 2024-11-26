package com.wongcoco.thinkwapp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.wongcoco.thinkwapp.R;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private static final String TAG = "GalleryActivity";
    private RecyclerView recyclerViewGallery;
    private GalleryAdapter adapter;
    private List<GalleryItem> galleryList;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerViewGallery = findViewById(R.id.recyclerViewGallery);
        recyclerViewGallery.setLayoutManager(new LinearLayoutManager(this));
        galleryList = new ArrayList<>();
        adapter = new GalleryAdapter(this, galleryList);
        recyclerViewGallery.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        fetchGalleryData();
    }

    private void fetchGalleryData() {
        firestore.collection("gallery").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        galleryList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            GalleryItem item = document.toObject(GalleryItem.class);
                            galleryList.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}
