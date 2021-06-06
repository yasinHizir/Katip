package com.crypto.katip.communication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class MessageReceiver {

    public void receive(UUID userUUID, ReceiveCallBack callBack){
        ConnectionFactory factory = new ConnectionFactory();
        String queueName = userUUID.toString();
        factory.setHost("138.68.78.206");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);
            DeliverCallback deliverCallback = (consumerTag, message) -> callBack.handleReceivedMessage( Envelope.deserialize(message.getBody()));
            while (true) {
                channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
                if (Thread.interrupted()) {
                    channel.close();
                    connection.close();
                    break;
                }
            }
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }

    }

    public interface ReceiveCallBack {
        void handleReceivedMessage(Envelope envelope);
    }
}