package com.crypto.katip.communication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class MessageSender {

    public boolean send(UUID remoteUUID, Envelope envelope) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("138.68.78.206");
        String queueName = remoteUUID.toString();

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, Envelope.serialize(envelope));
            return true;

        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}