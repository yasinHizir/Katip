package com.crypto.katip.communication;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.whispersystems.libsignal.SignalProtocolAddress;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageSender {

    public void send(SignalProtocolAddress remoteAddress, String message) {
        send(remoteAddress, message, new NotCallBack());
    }

    public void send(SignalProtocolAddress remoteAddress, String message, SendCallBack callBack) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("20.71.252.243");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(remoteAddress.toString(), false, false, false, null);
            channel.basicPublish("", remoteAddress.toString(), null, message.getBytes());
            callBack.handleSentMessage(message);

        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    public interface SendCallBack {
        void handleSentMessage(String message);
    }

    private static class NotCallBack implements SendCallBack{
        @Override
        public void handleSentMessage(String message) {}
    }
}
