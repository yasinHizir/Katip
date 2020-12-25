package com.crypto.katip.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.crypto.katip.R;
import com.crypto.katip.database.ChatDatabase;
import com.crypto.katip.ui.home.HomeViewModel;

import java.util.UUID;

public class ChatAdderFragment extends DialogFragment {
    private final HomeViewModel viewModel;
    private final ChatDatabase chatDatabase;
    private EditText uuidTextEdit;
    private EditText interlocutorTextEdit;

    private ChatAdderFragment(HomeViewModel viewModel, ChatDatabase chatDatabase) {
        this.viewModel = viewModel;
        this.chatDatabase = chatDatabase;
    }

    public static ChatAdderFragment newInstance(HomeViewModel homeViewModel, ChatDatabase chatDatabase) {
        return new ChatAdderFragment(homeViewModel, chatDatabase);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_adder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        interlocutorTextEdit = view.findViewById(R.id.edit_text_interlocutor);
        uuidTextEdit = view.findViewById(R.id.edit_text_UUID);
        Button button = view.findViewById(R.id.create);
        button.setOnClickListener(currentView -> {
            UUID remoteUUID = UUID.fromString(uuidTextEdit.getText().toString());
            String username = interlocutorTextEdit.getText().toString();
            viewModel.addChat(username);
            chatDatabase.save(remoteUUID, username);
            dismiss();
        });
    }
}