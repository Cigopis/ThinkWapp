package com.wongcoco.thinkwapp;

public class GalleryItem {
    private String imageUrl;
    private String description;

    public GalleryItem() {}

    public GalleryItem(String imageUrl, String description) {
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
