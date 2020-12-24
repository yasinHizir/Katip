package com.crypto.katip.communication;

import androidx.annotation.Nullable;

import com.crypto.katip.cryptography.SignalCipher;
import com.crypto.katip.database.models.User;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.whispersystems.libsignal.SignalProtocolAddress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeoutException;

public class MessageSender {

    public void send(User user, String interlocutor, String text, SendCallBack callBack) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("20.71.252.243");

        SignalCipher cipher = new SignalCipher(user);
        SignalProtocolAddress remoteAddress = new SignalProtocolAddress(interlocutor, 0);
        String queueName = remoteAddress.toString();

        cipher.encrypt(remoteAddress, text, cipherTextMessage -> {
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                Envelope envelope = new Envelope(cipherTextMessage.getType(), user.getUsername(), 0, cipherTextMessage.serialize());
                sendMessage(channel, envelope, queueName);
                callBack.handleSentMessage(envelope);

            } catch (TimeoutException | IOException e) {
                e.printStackTrace();
            }
        });
    }

    public interface SendCallBack {
        void handleSentMessage(Envelope envelope);
    }

    @Nullable
    private byte[] serialize(Envelope envelope) {
        byte[] bytes = null;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            ObjectOutputStream objectOutputStream =  new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(envelope);
            objectOutputStream.close();
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    private void sendMessage(Channel channel, Envelope envelope, String queueName) {
        try {
            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, serialize(envelope));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
