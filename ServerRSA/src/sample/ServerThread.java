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

public class ServerThread extends Thread {

    private Socket socket;
    private String sessionUsername;

    public ServerThread(Socket serverSocket) {
        this.socket = serverSocket;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            sessionUsername = (String)objectInputStream.readObject();


            ObjectOutputStream objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());
            objectOutputStream.writeObject("Hey : " + sessionUsername);
            objectOutputStream.flush();
            System.out.println("Message sent ");

            // Generate RSA keys
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PrivateKey privateKeyServer = keyPair.getPrivate();
            PublicKey publicKeyServer = keyPair.getPublic();

            PublicKey publicKeyClient = (PublicKey) objectInputStream.readObject();
            System.out.println("Public key of client : " + publicKeyClient);
            objectOutputStream.writeObject(publicKeyServer);

            while (true) {
                String switchTo = (String) objectInputStream.readObject();
                byte[] cipherText;
                MessageDecryption messageDecryption;
                switch(switchTo) {
                    case "String":
                        cipherText = (byte[])objectInputStream.readObject();
                        System.out.println(cipherText);
                        messageDecryption = new MessageDecryption(cipherText, privateKeyServer);

                        System.out.println(messageDecryption.getPlainMessage());
                        break;

                    case "File":
                        cipherText = (byte[])objectInputStream.readObject();
                        System.out.println(cipherText);
                        messageDecryption = new MessageDecryption(cipherText, privateKeyServer);
                        File file = new File("saurabh.sql");
                        Files.write(file.toPath(), messageDecryption.getPlainFile());

                        System.out.println(messageDecryption.getPlainMessage());
                        break;

                        default:
                            System.out.println("Default " + switchTo);
                }
            }

        } catch (IOException | ClassNotFoundException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            System.out.println("KeyPairGenerator.getInstance(\"RSA\")");
            e.printStackTrace();
        }


    }
}
