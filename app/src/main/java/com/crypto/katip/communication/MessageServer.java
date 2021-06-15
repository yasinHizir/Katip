package com.crypto.katip.communication;

import java.util.UUID;

public class MessageServer extends Server {

    public boolean send(UUID remoteUUID, Envelope envelope) {
        String queueName = remoteUUID.toString();
        byte[] message = Envelope.serialize(envelope);

        return send(queueName, message);
    }

    public void receive(UUID userUUID, ReceiveCallBack callBack) {
        String queueName = userUUID.toString();

        while (true) {
            byte[] message = receive(queueName);
            if (message != null) {
                callBack.handleReceivedMessage(Envelope.deserialize(message));
            } else if (Thread.interrupted()) {
                break;
            }
        }
    }

    public interface ReceiveCallBack {
        void handleReceivedMessage(Envelope envelope);
    }
}
