package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.PublicKey;
import java.util.HashMap;

public class Main {

    static HashMap<String, PublicKey> usernameKeyPair = new HashMap<>();

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(5700)){

            while(true){
                System.out.println("Waiting for client");
                new ServerThread(serverSocket.accept()).start();
            }
        }catch (IOException e){
            System.out.println("Server exception " + e.getMessage());
        }
    }

}