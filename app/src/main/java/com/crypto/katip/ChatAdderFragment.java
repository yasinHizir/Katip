package com.crypto.katip;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.crypto.katip.viewmodels.home.HomeViewModel;

public class ChatAdderFragment extends DialogFragment {
    private final HomeViewModel viewModel;
    private EditText interlocutorTextEdit;

    private ChatAdderFragment(HomeViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public static ChatAdderFragment newInstance(HomeViewModel homeViewModel) {
        return new ChatAdderFragment(homeViewModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_adder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        interlocutorTextEdit = view.findViewById(R.id.interlocutor);
        Button button = view.findViewById(R.id.create);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.addChat(interlocutorTextEdit.getText().toString());
                dismiss();
            }
        });
    }
}