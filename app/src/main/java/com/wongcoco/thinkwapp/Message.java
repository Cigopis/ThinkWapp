package com.wongcoco.thinkwapp;

public class Message {

    private String senderId;
    private String senderPhoneNumber;
    private String receiverPhoneNumber;
    private String messageText;
    private long timestamp;

    // Konstruktor kosong untuk Firebase
    public Message() {}

    // Konstruktor dengan parameter
    public Message(String senderId, String senderPhoneNumber, String receiverPhoneNumber, String messageText, long timestamp) {
        this.senderId = senderId;
        this.senderPhoneNumber = senderPhoneNumber;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    // Getter dan Setter
    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }

    public void setSenderPhoneNumber(String senderPhoneNumber) {
        this.senderPhoneNumber = senderPhoneNumber;
    }

    public String getReceiverPhoneNumber() {
        return receiverPhoneNumber;
    }

    public void setReceiverPhoneNumber(String receiverPhoneNumber) {
        this.receiverPhoneNumber = receiverPhoneNumber;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
