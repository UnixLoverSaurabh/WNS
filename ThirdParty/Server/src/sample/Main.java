package sample;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;

public class Main {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        Socket socket = new Socket("localhost", 5700);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject("server");

        // Generate RSA keys
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKeyServer = keyPair.getPrivate();
        PublicKey publicKeyServer = keyPair.getPublic();

        System.out.println("Server is sending its public key");
        objectOutputStream.writeObject(publicKeyServer);

        // Close the socket after sending its public key
        socket.close();

        try(ServerSocket serverSocket = new ServerSocket(5701)){

            while(true){
                System.out.println("Waiting for client");
                new ServerThread(serverSocket.accept()).start();
            }
        }catch (IOException e){
            System.out.println("Server exception " + e.getMessage());
        }
    }
}