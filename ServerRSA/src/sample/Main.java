package sample;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5555);

        while (true) {
            System.out.println("Waiting for client");
            new ServerThread(serverSocket.accept()).start();
        }
    }
}
