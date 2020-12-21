package com.crypto.katip.communication;

import android.content.Context;

import androidx.annotation.Nullable;

import com.crypto.katip.cryptography.SignalPublicKeyBundle;
import com.crypto.katip.database.DbHelper;
import com.crypto.katip.database.PreKeyDatabase;
import com.crypto.katip.database.SignedPreKeyDatabase;
import com.crypto.katip.database.UserDatabase;
import com.crypto.katip.database.models.User;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class KeyServer {
    //TODO:Anhtar yöneticisini ayarladıktan sonra burayı düzenle
    public void send(User user, Context context) {
        ConnectionFactory factory = new ConnectionFactory();
        SignalProtocolAddress localAddress = new SignalProtocolAddress(user.getUsername(), 0);
        String queueName = localAddress.toString() + "Key";
        factory.setHost("20.71.252.243");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish("", queueName, null, serialize(getPreKey(user.getId(), context)));
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void receive(SignalProtocolAddress remoteAddress, ReceiveKeyCallBack callBack) {
        ConnectionFactory factory = new ConnectionFactory();
        String queueName = remoteAddress.toString() + "Key";
        factory.setHost("20.71.252.243");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(queueName, false, false, false, null);
            DeliverCallback deliverCallback = (consumerTag, message) -> {
                SignalPublicKeyBundle keyBundle = deserialize(message.getBody());
                if (keyBundle != null) {
                    callBack.handleReceivedKeys(keyBundle);
                }
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    public interface ReceiveKeyCallBack {
        void handleReceivedKeys(SignalPublicKeyBundle keyBundle);
    }

    @Nullable
    private static byte[] serialize(SignalPublicKeyBundle keyBundle) {
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
    private static SignalPublicKeyBundle deserialize(byte[] bytes) {
        SignalPublicKeyBundle bundle = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)){
            ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);
            bundle = (SignalPublicKeyBundle) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    private SignalPublicKeyBundle getPreKey(int userId, Context context) {
        IdentityKeyPair identityKeyPair = new UserDatabase(new DbHelper(context)).getIdentityKeyPair(userId);
        int registrationId = new UserDatabase(new DbHelper(context)).getRegistrationID(userId);
        IdentityKey identityKey = identityKeyPair.getPublicKey();

        List<PreKeyRecord> preKeyRecords = KeyHelper.generatePreKeys(0,1);
        new PreKeyDatabase(new DbHelper(context), userId).store(preKeyRecords.get(0).getId(), preKeyRecords.get(0));

        SignedPreKeyRecord record;
        int signedPreKeyId = 0;
        try {
            record = KeyHelper.generateSignedPreKey(identityKeyPair, signedPreKeyId);
            new SignedPreKeyDatabase(new DbHelper(context), userId).store(signedPreKeyId, record);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }

        return new SignalPublicKeyBundle(registrationId, 0, preKeyRecords.get(0).getId(), preKeyRecords.get(0).getKeyPair().getPublicKey(), signedPreKeyId, record.getKeyPair().getPublicKey(), record.getSignature(), identityKey);
    }
}