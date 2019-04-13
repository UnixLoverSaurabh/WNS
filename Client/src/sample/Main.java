package sample;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 5700);

        try {
            System.out.println("Client is first sending message");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject("A new message from Client");
            objectOutputStream.flush();


            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            String receivedMessage = (String)objectInputStream.readObject();
            System.out.println("\nReceived Server Message : " + receivedMessage);

        } catch (IOException e) {
            System.out.println("Error in socket.getInputStream() ");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error in objectInputStream.readObject() ");
            e.printStackTrace();
        }
    }
}
