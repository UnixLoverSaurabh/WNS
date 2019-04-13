package sample;

import sample.Encryption.EncryptRSAwithSignature;
import sample.Message.AESkeyAndSignature;
import sample.Message.Packet;

import javax.crypto.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;

public class ServerThread extends Thread{

    private Socket socket;

    private String sessionUsername;

    private PublicKey publicKeyClient;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream());
            Packet receivedMessage = (Packet)objectInputStream.readObject();
            this.sessionUsername = receivedMessage.getFrom();
            System.out.println("Received from " + this.sessionUsername +  " Message : " + receivedMessage.getMessage());


            System.out.println("\nServer is giving response message");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            Packet packet = new Packet("Response from server","Server", sessionUsername);
            objectOutputStream.writeObject(packet);
            objectOutputStream.flush();


            // generate an RSA key
            System.out.println("\nStart generating RSA key");
            KeyPairGenerator keyGenRSA = KeyPairGenerator.getInstance("RSA");
            keyGenRSA.initialize(1024);
            KeyPair keyRSA = keyGenRSA.generateKeyPair();
            PrivateKey keyRSAPrivate = keyRSA.getPrivate();
            PublicKey keyRSAPublic = keyRSA.getPublic();
            System.out.println("Private Key : " + keyRSAPrivate);
            System.out.println("Public key : " + keyRSAPublic);
            System.out.println("Finish generating RSA key");

            // First Server send the own public key to the client
            objectOutputStream.writeObject(keyRSAPublic);
            objectOutputStream.flush();
            System.out.println("Public key (RSA)of server has been sent to client");

            // Receive Client public key(RSA)
            try {
                publicKeyClient = (PublicKey) objectInputStream.readObject();
                System.out.println("Received public key of " + sessionUsername + " is : " + publicKeyClient);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            // get a AES private key  (Generates the key)
            System.out.println("\nStart generating AES key");
            KeyGenerator keyGenAES = KeyGenerator.getInstance("AES");
            keyGenAES.init(128);    //In AES cipher block size is 128 bits
            SecretKey key = keyGenAES.generateKey();
            System.out.println("Finish generating AES key" + key);

            EncryptRSAwithSignature encryptRSAwithSignature = new EncryptRSAwithSignature(key.getEncoded(), publicKeyClient, keyRSAPrivate);
            AESkeyAndSignature aeSkeyAndSignature = new AESkeyAndSignature(encryptRSAwithSignature.getCipherKeyAES(), encryptRSAwithSignature.getSignature());
            // Now send the AES common key
            objectOutputStream.writeObject(aeSkeyAndSignature);
            objectOutputStream.flush();
            System.out.println("Common key (AES) has been sent to client");

        } catch (IOException e) {
            System.out.println("Error in socket.getInputStream() ");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error in objectInputStream.readObject() ");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error in KeyPairGenerator.getInstance(\"RSA\") ");
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            System.out.println("Error in AESkeyAndSignature()");
            e.printStackTrace();
        } catch (SignatureException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }
}
