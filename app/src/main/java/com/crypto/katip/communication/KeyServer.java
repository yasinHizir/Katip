package com.crypto.katip.communication;

import androidx.annotation.Nullable;

import com.crypto.katip.cryptography.PublicKeyBundle;

import java.util.Objects;
import java.util.UUID;

public class KeyServer extends Server {

    public boolean send(UUID userUUID, PublicKeyBundle keyBundle) {
        String queueName = userUUID.toString() + "-Key";
        byte[] bytes = PublicKeyBundle.serialize(keyBundle);

        return send(queueName, bytes);
    }

    @Nullable
    public PublicKeyBundle receive(UUID remoteUUID) {
        String queueName = remoteUUID.toString() + "-Key";
        byte[] message = receive(queueName);

        return PublicKeyBundle.deserialize(Objects.requireNonNull(message));
    }
}