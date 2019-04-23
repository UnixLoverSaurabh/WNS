package sample;

import javax.crypto.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ServerThread extends Thread{

    private Socket socket;

    private SecretKey secretKey;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream());
            String receivedMessage = (String)objectInputStream.readObject();
            System.out.println("Received Message : " + receivedMessage);


            System.out.println("\nServer is Sending AES key");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            try {
                KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                keyGenerator.init(128);
                secretKey = keyGenerator.generateKey();
                objectOutputStream.writeObject(secretKey);
                objectOutputStream.flush();

            } catch (NoSuchAlgorithmException e) {
                System.out.println("Error in KeyGenerator.getInstance(\"AES\")");
                e.printStackTrace();
            }

            // get a message digest object using the MD5 algorithm
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            while (true) {
                String switchTo = (String)objectInputStream.readObject();
                byte[] mess;
                switch (switchTo) {
                    case "String":

                        String message = (String)objectInputStream.readObject();
                        String stringDigest = (String)objectInputStream.readObject();

                        // calculate the digest and print it out
                        messageDigest.update(message.getBytes(StandardCharsets.UTF_8));
                        String stringMade= new String( messageDigest.digest(), StandardCharsets.UTF_8);

                        // Its compulsory to compare two digest in string data type
                        if (stringMade.equals(stringDigest)) {
                            System.out.println("Digest matched " + stringDigest);
                        }
                        break;

                    case "File":
                        System.out.println("Hey files");
                        File imgFile = new File("testFile.sql");
                        mess = (byte[])objectInputStream.readObject();
                        Files.write(imgFile.toPath(), mess);

                        break;

                    default:
                        System.out.println("How's is possible " + switchTo);
                }
            }

        } catch (IOException e) {
            System.out.println("Error in socket.getInputStream() ");
            e.printStackTrace();
        } catch (ClassNotFoundException | NoSuchAlgorithmException e) {
            System.out.println("Error in objectInputStream.readObject() ");
            e.printStackTrace();
        }
    }
}
