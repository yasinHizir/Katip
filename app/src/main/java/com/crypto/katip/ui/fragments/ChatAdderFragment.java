package com.crypto.katip.ui.fragments;

import android.content.Context;
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
import com.crypto.katip.communication.ChatBuilder;
import com.crypto.katip.database.ChatDatabase;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.models.User;
import com.crypto.katip.login.LoginRepository;
import com.crypto.katip.ui.home.HomeViewModel;

import java.util.UUID;

public class ChatAdderFragment extends DialogFragment {
    private final User user;
    private final HomeViewModel viewModel;
    private final Context context;
    private EditText uuidTextEdit;

    private ChatAdderFragment(User user, HomeViewModel viewModel, Context context) {
        this.user = user;
        this.viewModel = viewModel;
        this.context = context;
    }

    public static ChatAdderFragment newInstance(HomeViewModel homeViewModel, Context context) {
        User user = LoginRepository.getInstance().getUser();
        return new ChatAdderFragment(user, homeViewModel, context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_adder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uuidTextEdit = view.findViewById(R.id.edit_text_UUID);
        Button button = view.findViewById(R.id.create);

        button.setOnClickListener(currentView -> {
            UUID remoteUUID = UUID.fromString(uuidTextEdit.getText().toString());
            if (new ChatBuilder(user.getId(), remoteUUID).build(context)) {
                viewModel.getLiveData().setValue(new ChatDatabase(new DbHelper(context), user.getId()).getChats());
            }
            dismiss();
        });
    }
}