package com.crypto.katip.communication;

import androidx.annotation.Nullable;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class Server {
    private static final String HOST = "138.68.78.206";

    protected boolean send(String queueName, byte[] message) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.basicPublish("", queueName, null, message);
            return true;

        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Nullable
    protected byte[] receive(String queueName) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        byte[] message = null;

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queueName, false, false, false, null);
            message = channel.basicGet(queueName, true).getBody();

        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }

        return message;
    }
}
