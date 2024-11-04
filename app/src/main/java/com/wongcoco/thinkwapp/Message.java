package com.wongcoco.thinkwapp;

public class Message {
    private String text;
    private boolean isSentByUser;
    private String receiverId;

    // Konstruktor tanpa parameter diperlukan untuk Firebase
    public Message() {}

    // Konstruktor untuk membuat objek pesan
    public Message(String text, boolean isSentByUser, String receiverId) {
        this.text = text;
        this.isSentByUser = isSentByUser;
        this.receiverId = receiverId;
    }

    // Getter dan Setter untuk atribut 'text'
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // Getter dan Setter untuk atribut 'isSentByUser'
    public boolean isSentByUser() {
        return isSentByUser;
    }

    public void setSentByUser(boolean sentByUser) {
        isSentByUser = sentByUser;
    }

    // Getter dan Setter untuk atribut 'receiverId'
    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
