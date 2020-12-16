package com.crypto.katip.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crypto.katip.R;
import com.crypto.katip.database.models.TextMessage;

import java.util.ArrayList;

public class MessagesViewAdapter extends RecyclerView.Adapter<MessagesViewAdapter.MessageViewHolder> {
    private final ArrayList<TextMessage> messages;

    public MessagesViewAdapter(ArrayList<TextMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        TextMessage message = messages.get(position);
        if (!message.isOwn()) {
            return 1;
        }
        return 0;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_list_received_message, parent, false);
        } else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_list_sended_message, parent, false);
        }

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        TextMessage message = this.messages.get(position);
        holder.getMessageTextView().setText(message.getBody());
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
