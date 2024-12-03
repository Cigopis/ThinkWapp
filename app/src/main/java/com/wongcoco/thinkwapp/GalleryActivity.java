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
        galleryList.clear();
        galleryList.add(new GalleryItem("Kayu Sengon: Pemotongan Kayu",
                "https://images.unsplash.com/photo-1502082553048-f009c37129b9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=400"));
        galleryList.add(new GalleryItem("Pengelolaan Kayu Sengon",
                "https://images.unsplash.com/photo-1611692558904-467b17215f98?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=400"));
        galleryList.add(new GalleryItem("Pengangkutan Kayu Sengon",
                "https://images.unsplash.com/photo-1576664018968-007c1a93b5fa?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=400"));
        adapter.notifyDataSetChanged();
    }


}
