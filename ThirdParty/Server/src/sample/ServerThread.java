package sample;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.security.PublicKey;

public class ServerThread extends Thread{

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream());
            String receivedMessage = (String)objectInputStream.readObject();
            System.out.println("Received Message : " + receivedMessage);


            System.out.println("\nServer is Receiving RSA public key of client");
            PublicKey publicKeyClient = (PublicKey)objectInputStream.readObject();
            System.out.println("Public key client: " + publicKeyClient);


            while (true) {
                String switchTo = (String)objectInputStream.readObject();
                switch (switchTo) {
                    case "String":
                        String message = (String)objectInputStream.readObject();
                        System.out.println("Message : " + message);
                        break;

                    case "File":
                        System.out.println("Hey files");
                        File imgFile = new File("testFile.sql");
                        byte[] mess = (byte[])objectInputStream.readObject();
                        Files.write(imgFile.toPath(), mess);

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
        }
    }
}
