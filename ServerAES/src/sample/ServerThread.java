package sample;

import javax.crypto.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.security.InvalidKeyException;
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

            while (true) {
                String switchTo = (String)objectInputStream.readObject();
                byte[] mess;
                MessageDecryption messageDecryption;
                switch (switchTo) {
                    case "String":
                        mess = (byte[])objectInputStream.readObject();
                        messageDecryption = new MessageDecryption(mess, secretKey);
                        System.out.println(messageDecryption.getPlainText());
                        break;

                    case "File":
                        System.out.println("Hey files");
                        File imgFile = new File("testFile.sql");
                        mess = (byte[])objectInputStream.readObject();
                        messageDecryption = new MessageDecryption(mess, secretKey);
                        Files.write(imgFile.toPath(), messageDecryption.getPlainFile());
                        break;

                        default:
                            System.out.println("How's is possible " + switchTo);
                }
            }

        } catch (IOException e) {
            System.out.println("Error in socket.getInputStream() ");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error in objectInputStream.readObject() ");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }
}
