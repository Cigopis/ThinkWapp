package com.wongcoco.thinkwapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wongcoco.thinkwapp.R;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    private Context context;
    private List<GalleryItem> galleryList;

    public GalleryAdapter(Context context, List<GalleryItem> galleryList) {
        this.context = context;
        this.galleryList = galleryList;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        GalleryItem item = galleryList.get(position);
        holder.titleText.setText(item.getTitle());
        Glide.with(context).load(item.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        ImageView imageView;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.textViewTitle);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
