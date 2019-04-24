package sample;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;

import static sample.Main.usernameKeyPair;

public class ServerThread extends Thread {

    private Socket socket;

    public ServerThread(Socket socket) {

        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream());
            String receivedMessage = (String) objectInputStream.readObject();
            System.out.println("Received Message : " + receivedMessage);


            System.out.println("\nKDC is receiving RSA public key");
            PublicKey publicKeyClient = (PublicKey) objectInputStream.readObject();

            usernameKeyPair.put(receivedMessage, publicKeyClient);
            System.out.println(usernameKeyPair);

            if (!receivedMessage.equals("server")) {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                if (usernameKeyPair.containsKey("server")) {
                    objectOutputStream.writeObject(usernameKeyPair.get("server"));
                }
                else {
                    System.out.println("Start server first");
                }
            }
            else {
                System.out.println("Start server first from outer loop");
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
