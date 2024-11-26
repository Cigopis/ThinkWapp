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

    private final Context context;
    private final List<GalleryItem> galleryList;

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
        holder.tvDescription.setText(item.getDescription());
        Glide.with(context).load(item.getImageUrl()).into(holder.ivImage);
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvDescription;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
