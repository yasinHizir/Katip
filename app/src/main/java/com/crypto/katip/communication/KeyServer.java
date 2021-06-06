package com.crypto.katip.communication;

import androidx.annotation.Nullable;

import com.crypto.katip.cryptography.PublicKeyBundle;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class KeyServer {

    public static boolean send(UUID userUUID, PublicKeyBundle keyBundle) {
        ConnectionFactory factory = new ConnectionFactory();
        String queueName = userUUID.toString() + "-Key";
        factory.setHost("138.68.78.206");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, PublicKeyBundle.serialize(keyBundle));
            return true;
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Nullable
    public static PublicKeyBundle receive(UUID remoteUUID) {
        ConnectionFactory factory = new ConnectionFactory();
        String queueName = remoteUUID.toString() + "-Key";
        factory.setHost("138.68.78.206");
        PublicKeyBundle keyBundle = null;

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);

            GetResponse response;
            try {
                response = channel.basicGet(queueName, true);
                keyBundle = PublicKeyBundle.deserialize(response.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }

        return keyBundle;
    }
}