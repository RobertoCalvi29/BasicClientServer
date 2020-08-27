package server;

import common.ConnectionHost;
import common.validation.GetInformation;
import common.validation.Side;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server
{
    private static ArrayList<ConnectionHandler> clientsList = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(8); // For debug

    public static void main(String[] args)
    {
        GetInformation infoGetter = new GetInformation(Side.SERVER);
        ConnectionHost connectionHost = infoGetter.getConnectionHostInfo();

        // Create the server socket
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(connectionHost.port);
            System.out.println("Server listening on port: " + connectionHost.port);
            System.out.println("Type quit to quit if you want to close the server ");
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + connectionHost.port);
            System.exit(1);
        }

        pool.execute(new Admin(serverSocket)); // Thread for the Admin client

        Socket clientSocket = null;
        boolean listening = true;
        while (listening){
            try {
                System.out.println("[SERVER] waiting for client connection...");
                clientSocket = serverSocket.accept();
                System.out.println("client Connected");

                // Multi threading of clients
                ConnectionHandler clientThread = new ConnectionHandler(clientSocket);
                clientsList.add(clientThread);
                pool.execute(clientThread);
            } catch (SocketException e) {
                System.out.println("Server is closed");
                listening = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}
