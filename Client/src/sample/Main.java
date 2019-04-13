package sample;

import sample.Decryption.DecryptRSAwithSignature;
import sample.Message.AESkeyAndSignature;
import sample.Message.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.util.Scanner;

public class Main {

    private static PublicKey publicKeyServer;
    private static PrivateKey keyRSAPrivate;
    private static Key key;
    private static String sessionUsername;

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 5700);

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the Username : ");
            sessionUsername = scanner.nextLine();
            Packet packet = new Packet("A new message from Client",sessionUsername, "Server");
            objectOutputStream.writeObject(packet);

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Packet receivedMessage = (Packet)objectInputStream.readObject();
            System.out.println("\nReceived Server sample.Message : " + receivedMessage.getMessage());


            // First receive the public key (RSA) of server
            try {
                publicKeyServer = (PublicKey) objectInputStream.readObject();
                System.out.println("Public key of server received");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


            // generate an RSA key
            System.out.println("\nStart generating RSA key");
            KeyPairGenerator keyGenRSA = KeyPairGenerator.getInstance("RSA");
            keyGenRSA.initialize(1024);
            KeyPair keyRSA = keyGenRSA.generateKeyPair();
            keyRSAPrivate = keyRSA.getPrivate();
            PublicKey keyRSAPublic = keyRSA.getPublic();
            System.out.println("Private Key : " + keyRSAPrivate);
            System.out.println("Public key : " + keyRSAPublic);
            System.out.println("Finish generating RSA key");

            objectOutputStream.writeObject(keyRSAPublic);
            objectOutputStream.flush();
            System.out.println("Public key (RSA)of server has been sent to server");

            // Then receive the common key (AES) sent by server
            try {
                AESkeyAndSignature aeSkeyAndSignature = (AESkeyAndSignature) objectInputStream.readObject();
                DecryptRSAwithSignature decryptRSAwithSignature = new DecryptRSAwithSignature(aeSkeyAndSignature.getCipherKeyAES(), keyRSAPrivate, publicKeyServer, aeSkeyAndSignature.getSignature());
                key = decryptRSAwithSignature.getKey();
                System.out.println("Common key(AES) from server received : " + key);
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
            System.out.println("Error in KeyPairGenerator.getInstance(\"RSA\")");
            e.printStackTrace();
        }
    }
}
