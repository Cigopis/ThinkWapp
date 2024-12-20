package com.wongcoco.thinkwapp;

public class GalleryItem {
    private String title;
    private String imageUrl;

    // Constructor kosong diperlukan oleh Firebase Firestore
    public GalleryItem() {}

    public GalleryItem(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
