package sample;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.security.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        Socket socketWithKDC = new Socket("localhost", 5700);

        try {
            System.out.println("Enter your name");
            Scanner scanner = new Scanner(System.in);
            ObjectOutputStream objectOutputStreamWithKDC = new ObjectOutputStream(socketWithKDC.getOutputStream());
            String username = scanner.nextLine();
            objectOutputStreamWithKDC.writeObject(username);
            objectOutputStreamWithKDC.flush();

            // Generate RSA keys
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PrivateKey privateKeyClient = keyPair.getPrivate();
            PublicKey publicKeyClient = keyPair.getPublic();

            System.out.println("Client is sending its public key");
            objectOutputStreamWithKDC.writeObject(publicKeyClient);
            objectOutputStreamWithKDC.flush();


            ObjectInputStream objectInputStreamWithKDC = new ObjectInputStream(socketWithKDC.getInputStream());
            PublicKey publicKeyServer = (PublicKey) objectInputStreamWithKDC.readObject();
            System.out.println("Received Server key : " + publicKeyServer);

            // Close the socket with kdc after getting public key of server
            socketWithKDC.close();


            // Start a new socket with the real server with whom real communication takes place
            Socket socket = new Socket("localhost", 5701);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(username);
            objectOutputStream.flush();

            objectOutputStream.writeObject(publicKeyServer);
            objectOutputStream.flush();

            System.out.println("Enter the message");
            String mess = "Hii";
            while(!mess.equals("over")) {
                mess = scanner.nextLine();

                if(mess.equals("file")) {
                    objectOutputStream.writeObject("File");
                    objectOutputStream.flush();

                    File file = new File("trigger.sql");
                    byte [] fileArray  = Files.readAllBytes(file.toPath());
                    objectOutputStream.writeObject(fileArray);
                    objectOutputStream.flush();
                }
                else {
                    objectOutputStream.writeObject("String");
                    objectOutputStream.flush();

                    objectOutputStream.writeObject(mess);
                    objectOutputStream.flush();
                }
            }

        } catch (IOException e) {
            System.out.println("Error in socket.getInputStream() ");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | ClassNotFoundException e) {
            System.out.println("Error in objectInputStream.readObject() ");
            e.printStackTrace();
        }
    }
}