package com.crypto.katip.communication;

import androidx.annotation.Nullable;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import org.whispersystems.libsignal.SignalProtocolAddress;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeoutException;

public class MessageReceiver {

    public void receive(SignalProtocolAddress localAddress, ReceiveCallBack callBack){
        ConnectionFactory factory = new ConnectionFactory();
        String queueName = localAddress.toString();
        factory.setHost("20.71.252.243");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);
            DeliverCallback deliverCallback = (consumerTag, message) -> callBack.handleReceivedMessage( deserialize(message.getBody()));
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

    @Nullable
    private Envelope deserialize(byte[] bytes) {
        Envelope envelope = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)){
            ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
            envelope = (Envelope) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return envelope;
    }
}
