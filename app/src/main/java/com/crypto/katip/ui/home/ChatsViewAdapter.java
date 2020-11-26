package com.crypto.katip.ui.home;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.crypto.katip.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsViewAdapter extends RecyclerView.Adapter<ChatsViewAdapter.ChatViewHolder> {
    private ArrayList<String> images;
    private ArrayList<String> interlocutor;

    public ChatsViewAdapter(ArrayList<String> images, ArrayList<String> interlocutor) {
        this.images = images;
        this.interlocutor = interlocutor;
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, final int position) {
        holder.getInterlocutor().setText(this.interlocutor.get(position));
    }

    @Override
    public int getItemCount() {
        return this.interlocutor.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView image;
        private TextView interlocutor;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            interlocutor = itemView.findViewById(R.id.image_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        public CircleImageView getImage() {
            return this.image;
        }

        public TextView getInterlocutor() {
            return this.interlocutor;
        }
    }
}
