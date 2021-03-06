package com.crypto.katip.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crypto.katip.ChatActivity;
import com.crypto.katip.R;
import com.crypto.katip.database.models.Chat;

import java.util.List;

public class ChatsViewAdapter extends RecyclerView.Adapter<ChatsViewAdapter.ChatViewHolder> {
    private final List<Chat> chats;

    public ChatsViewAdapter(List<Chat> chats) {
        this.chats = chats;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_list_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, final int position) {
        holder.getInterlocutor().setText(this.chats.get(position).getInterlocutor());
        holder.setChatID(this.chats.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return this.chats.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private int chatID;
        private final TextView interlocutor;

        public ChatViewHolder(@NonNull final View itemView) {
            super(itemView);
            interlocutor = itemView.findViewById(R.id.image_name);
            itemView.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra(ChatActivity.CHAT_ID, chatID);
                view.getContext().startActivity(intent);
            });
        }

        public void setChatID(int chatID) {
            this.chatID = chatID;
        }

        public TextView getInterlocutor() {
            return this.interlocutor;
        }
    }
}