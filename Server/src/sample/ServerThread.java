package sample;

import sample.Message.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread extends Thread{

    private Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(this.socket.getInputStream());
            Packet receivedMessage = (Packet)objectInputStream.readObject();
            System.out.println("Received Client Message : " + receivedMessage.getMessage());


            System.out.println("\nServer is giving response message");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            Packet packet = new Packet("Response from server","Server", "Client");
            objectOutputStream.writeObject(packet);
            objectOutputStream.flush();

        } catch (IOException e) {
            System.out.println("Error in socket.getInputStream() ");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Error in objectInputStream.readObject() ");
            e.printStackTrace();
        }
    }
}
