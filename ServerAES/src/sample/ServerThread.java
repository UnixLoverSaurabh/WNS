package sample;

import sample.Decryption.MessageDecryption;

import javax.crypto.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
                byte[] mess = (byte[])objectInputStream.readObject();
                MessageDecryption messageDecryption = new MessageDecryption(mess, secretKey);
                System.out.println(messageDecryption.getPlainText());
            }

        } catch (IOException e) {
            System.out.println("Error in socket.getInputStream() ");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error in objectInputStream.readObject() ");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }
}
