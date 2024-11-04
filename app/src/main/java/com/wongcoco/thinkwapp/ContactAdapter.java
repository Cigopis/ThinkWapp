package com.wongcoco.thinkwapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> contacts;

    public ContactAdapter(Context context, List<String> contacts) {
        super(context, R.layout.list_item_contact, contacts);
        this.context = context;
        this.contacts = contacts;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            itemView = inflater.inflate(R.layout.list_item_contact, parent, false);
        }

        // Ambil data kontak
        String contact = contacts.get(position);

        // Set nama kontak pada TextView
        TextView contactText = itemView.findViewById(R.id.contact_text);
        contactText.setText(contact);

        // Set gambar pada ImageView
        ImageView contactImage = itemView.findViewById(R.id.contact_image);
        contactImage.setImageResource(R.drawable.pesan); // Sesuaikan dengan gambar yang diinginkan

        return itemView;
    }
}

