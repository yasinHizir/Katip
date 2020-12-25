package com.crypto.katip.communication;

import androidx.annotation.Nullable;

import com.crypto.katip.cryptography.PublicKeyBundle;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class KeyServer {

    public static void send(UUID userUUID, PublicKeyBundle keyBundle, SendCallBack callBack) {
        ConnectionFactory factory = new ConnectionFactory();
        String queueName = userUUID.toString() + "-Key";
        factory.setHost("20.71.252.243");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, serialize(keyBundle));
            callBack.handleTransmittedKeys(keyBundle);
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void receive(UUID remoteUUID, ReceiveKeyCallBack callBack) {
        ConnectionFactory factory = new ConnectionFactory();
        String queueName = remoteUUID.toString() + "-Key";
        factory.setHost("20.71.252.243");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);

            new Thread() {
                @Override
                public void run() {
                    GetResponse response;
                    try {
                        response = channel.basicGet(queueName, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    PublicKeyBundle keyBundle = deserialize(response.getBody());
                    if (keyBundle != null) {
                        callBack.handleReceivedKeys(keyBundle);
                    }
                }
            }.start();
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    public interface ReceiveKeyCallBack {
        void handleReceivedKeys(PublicKeyBundle receiveBundle);
    }

    public interface SendCallBack {
        void handleTransmittedKeys(PublicKeyBundle sentBundle);
    }

    @Nullable
    private static byte[] serialize(PublicKeyBundle keyBundle) {
        byte[] bytes = null;

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            ObjectOutputStream objectOutputStream =  new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(keyBundle);
            objectOutputStream.close();
            bytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    @Nullable
    private static PublicKeyBundle deserialize(byte[] bytes) {
        PublicKeyBundle bundle = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)){
            ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
            bundle = (PublicKeyBundle) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return bundle;
    }
}