package com.wongcoco.thinkwapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private Context context;
    private List<Message> messageList;
    private String currentUserId;

    public ChatAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        // Pilih layout berdasarkan apakah pesan dikirim oleh pengguna saat ini
        return message.getSenderId().equals(currentUserId) ? R.layout.right_message_item : R.layout.left_message_item;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(viewType, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.messageTextView.setText(message.getMessageText());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void updateMessageList(List<Message> newMessageList) {
        this.messageList.clear();
        this.messageList.addAll(newMessageList);
        notifyDataSetChanged(); // Memberitahukan adapter bahwa data telah berubah
    }

    public void addMessage(Message newMessage) {
        messageList.add(newMessage);
        notifyItemInserted(messageList.size() - 1); // Menambahkan pesan baru ke dalam list
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message);
        }
    }
}
