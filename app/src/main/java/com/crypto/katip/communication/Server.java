package com.crypto.katip.communication;

import androidx.annotation.Nullable;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * This class sends data to the server
 * and receives data from the server.
 *
 * @author Yasin HIZIR
 * @version Beta
 * @since 2021-06-17
 */
public abstract class Server {
    private static final String HOST = "138.68.78.206";

    /**
     *  This method sends data to the server
     *
     * @param queueName Queue name of the user to send
     * @param data data to send to the server
     * @return returns send or not send
     */
    protected boolean send(String queueName, byte[] data) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, data);
            return true;

        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *  This method receives data from the server
     *
     * @param queueName Queue name of the user to receive
     * @return returns data of receives
     */
    @Nullable
    protected byte[] receive(String queueName) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        byte[] data = null;

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queueName, false, false, false, null);
            GetResponse response = channel.basicGet(queueName, true);
            if(response != null) {
                data = response.getBody();
            }
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}