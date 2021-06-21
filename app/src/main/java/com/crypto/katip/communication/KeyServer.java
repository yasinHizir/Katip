package com.crypto.katip.communication;

import androidx.annotation.Nullable;

import com.crypto.katip.cryptography.PublicKeyBundle;

import java.util.Objects;
import java.util.UUID;

/**
 * This class sends key bundle to the server
 * and receives key bundle from the server.
 *
 * @author Yasin HIZIR
 * @version Beta
 * @since 2021-06-17
 */
public class KeyServer extends Server {

    /**
     * This method sends the key bundle to
     * the server.
     *
     * @param userUUID  The UUID of the user who
     *                  has the key bundle
     * @param keyBundle The bundle of the public
     *                  keys
     * @return          send or not send
     */
    public boolean send(UUID userUUID, PublicKeyBundle keyBundle) {
        String queueName = userUUID.toString() + "-Key";
        byte[] bytes = PublicKeyBundle.serialize(keyBundle);

        return send(queueName, bytes);
    }

    /**
     * This method receive the key bundle from the server.
     *
     * @param remoteUUID    The uuid of the user who you
     *                      want to establish  encrypted
     *                      sessions
     * @return              Received key bundle
     */
    @Nullable
    public PublicKeyBundle receive(UUID remoteUUID) {
        String queueName = remoteUUID.toString() + "-Key";
        byte[] message = receive(queueName);

        return PublicKeyBundle.deserialize(Objects.requireNonNull(message));
    }
}