package sample;

import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 5700);

        try {
            System.out.println("Enter your name");
            Scanner scanner = new Scanner(System.in);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            String username = scanner.nextLine();
            objectOutputStream.writeObject("Username : " + username);

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            SecretKey secretKey = (SecretKey)objectInputStream.readObject();
            System.out.println("Common AES key : " + secretKey);


            // get a message digest object using the MD5 algorithm
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            System.out.println("Enter the message");
            String mess = "Hii";

            while(!mess.equals("over")) {
                mess = scanner.nextLine();

                if(mess.equals("file")) {
                    objectOutputStream.writeObject("File");
                    objectOutputStream.flush();

                    File file = new File("trigger.sql");
                    byte [] fileArray  = Files.readAllBytes(file.toPath());
                    objectOutputStream.writeObject(fileArray);
                    objectOutputStream.flush();
                }
                else {
                    objectOutputStream.writeObject("String");
                    objectOutputStream.flush();

                    // calculate the digest and print it out
                    messageDigest.update(mess.getBytes(StandardCharsets.UTF_8));
                    String stringDigest= new String( messageDigest.digest(), StandardCharsets.UTF_8);

                    objectOutputStream.writeObject(mess);
                    objectOutputStream.flush();
                    objectOutputStream.writeObject(stringDigest);
                    objectOutputStream.flush();
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