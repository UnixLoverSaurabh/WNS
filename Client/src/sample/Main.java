package sample;

import sample.Message.Packet;

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
            Packet packet = new Packet("A new message from Client","Client", "Server");
            objectOutputStream.writeObject(packet);

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Packet receivedMessage = (Packet)objectInputStream.readObject();
            System.out.println("\nReceived Server sample.Message : " + receivedMessage.getMessage());

        } catch (IOException e) {
            System.out.println("Error in socket.getInputStream() ");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error in objectInputStream.readObject() ");
            e.printStackTrace();
        }
    }
}
