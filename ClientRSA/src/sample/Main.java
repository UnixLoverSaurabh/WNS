package sample;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.security.*;
import java.util.Scanner;

public class Main {

    private static String sessionUsername;

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, SignatureException {
        Socket socket = new Socket("localhost", 5555);

        System.out.println("Enter username");
        Scanner scanner = new Scanner(System.in);
        sessionUsername = scanner.nextLine();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(sessionUsername);
        objectOutputStream.flush();


        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        String message = (String) objectInputStream.readObject();
        System.out.println("This is from Server and the message is " + message);

        // Generate RSA keys
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKeyClient = keyPair.getPrivate();
        PublicKey publicKeyClient = keyPair.getPublic();

        objectOutputStream.writeObject(publicKeyClient);
        objectOutputStream.flush();

        PublicKey publicKeyServer = (PublicKey) objectInputStream.readObject();
        System.out.println("Public key of server : " + publicKeyServer);

        String mess = "Hii";
        while (!mess.equals("over")) {
            mess = scanner.nextLine();

            if (mess.equals("file")) {
                objectOutputStream.writeObject("File");
                objectOutputStream.flush();

                File file = new File("q1.sql");
                byte[] fileArray = Files.readAllBytes(file.toPath());


                MessageEncryption messageEncryption = new MessageEncryption(fileArray, publicKeyServer, privateKeyClient);
                objectOutputStream.writeObject(messageEncryption.getSignature());
                objectOutputStream.flush();
                objectOutputStream.writeObject(messageEncryption.getCipherText());
                objectOutputStream.flush();
            }
            else {
                objectOutputStream.writeObject("String");
                objectOutputStream.flush();

                MessageEncryption messageEncryption = new MessageEncryption(mess, publicKeyServer, privateKeyClient);
                objectOutputStream.writeObject(messageEncryption.getSignature());
                objectOutputStream.flush();
                objectOutputStream.writeObject(messageEncryption.getCipherText());
                objectOutputStream.flush();
            }
        }
    }
}
