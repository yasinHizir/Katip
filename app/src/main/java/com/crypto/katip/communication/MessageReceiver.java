package com.crypto.katip.communication;

import android.util.Log;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import org.whispersystems.libsignal.SignalProtocolAddress;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessageReceiver {

    public void receive(SignalProtocolAddress localAddress, ReceiveCallBack callBack){
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("20.71.252.243");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(localAddress.toString(), false, false, false, null);
            DeliverCallback deliverCallback = (consumerTag, message) -> callBack.handleReceivedMessage(new String(message.getBody()));
            Log.v("Durum", "Bağlantı gerçekleşti");
            while (true) {
                channel.basicConsume(localAddress.toString(), true, deliverCallback, consumerTag -> {});
                if (Thread.interrupted()) {
                    channel.close();
                    connection.close();
                    Log.v("Durum:", "Bağlantı kesildi.");
                    break;
                }
            }
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }

    }

    public interface ReceiveCallBack {
        void handleReceivedMessage(String message);
    }
}
