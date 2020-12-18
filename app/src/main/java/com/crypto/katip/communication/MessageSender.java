package com.crypto.katip.communication;

import androidx.annotation.Nullable;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.whispersystems.libsignal.SignalProtocolAddress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.TimeoutException;

public class MessageSender {

    public void send(SignalProtocolAddress remoteAddress, Envelope envelope) {
        send(remoteAddress, envelope, new NotCallBack());
    }

    public void send(SignalProtocolAddress remoteAddress, Envelope envelope, SendCallBack callBack) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("20.71.252.243");


        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(remoteAddress.toString(), false, false, false, null);
            channel.basicPublish("", remoteAddress.toString(), null, serialize(envelope));
            callBack.handleSentMessage(envelope);

        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    public interface SendCallBack {
        void handleSentMessage(Envelope envelope);
    }

    private static class NotCallBack implements SendCallBack{
        @Override
        public void handleSentMessage(Envelope envelope) {}
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
}
