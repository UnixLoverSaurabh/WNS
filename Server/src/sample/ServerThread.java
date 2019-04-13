package sample;

import sample.Message.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;

public class ServerThread extends Thread{

    private Socket socket;

    private String sessionUsername;

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
            PublicKey publicKeyClient;
            try {
                publicKeyClient = (PublicKey) objectInputStream.readObject();
                System.out.println(publicKeyClient);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            System.out.println("Error in socket.getInputStream() ");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error in objectInputStream.readObject() ");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error in KeyPairGenerator.getInstance(\"RSA\") ");
            e.printStackTrace();
        }
    }
}
