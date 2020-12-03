package com.crypto.katip.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crypto.katip.R;

import java.util.ArrayList;

public class MessagesViewAdapter extends RecyclerView.Adapter<MessagesViewAdapter.MessageViewHolder> {
    private ArrayList<String> messages;

    public MessagesViewAdapter(ArrayList<String> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_list_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.getMessageTextView().setText(this.messages.get(position));
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.text_view_message);
        }

        public TextView getMessageTextView() {
            return this.messageTextView;
        }
    }
}