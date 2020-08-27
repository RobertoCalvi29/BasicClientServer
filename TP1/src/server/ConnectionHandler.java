package server;

import common.Credentials;
import common.ImagePacket;
import common.UserStatus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

import static common.ImagePacket.defaultFormat;

// This is to allow the handling of multiple threads
public class ConnectionHandler implements Runnable {
    private Socket client;

    public ConnectionHandler(Socket clientSocket)
    {
        client = clientSocket;
    }

    // Coding of the operations the client has to go through and a basic command line
    public void run() {
        try (
                ObjectOutputStream out =  new ObjectOutputStream(client.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(client.getInputStream());
                )
        {
            // Verify credentials
            Credentials credentials = null;
            UserStatus userStatus = UserStatus.BAD;
            while (userStatus == UserStatus.BAD) {
                credentials = (Credentials) in.readObject();
                userStatus = credentials.verify();
                out.writeObject(userStatus);
                out.flush();
            }

            // Receive image
            ImagePacket receivedImagePacket = (ImagePacket)in.readObject();

            // Apply Sobel
            System.out.println("[" + credentials.username + " - " + client.getInetAddress() + ":" + client.getPort() + " - " + LocalDateTime.now().withNano(0) + "] : Image " + receivedImagePacket.name + " received for rendering");
            ImagePacket sobelizedImagePacket = new ImagePacket(receivedImagePacket.name, Sobel.process(receivedImagePacket.image), defaultFormat);

            // Send processed image back to client
            out.writeObject(sobelizedImagePacket);
            out.flush();
        } catch (IOException e) {
            System.out.println("Client terminated the connection to go eat macaroni");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
