package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

// This class is only to allow an admin client that allows to run a command quit to shutdown the server if needed
public class Admin implements Runnable {
    public Admin(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // Command line
    public void run() {
        Scanner in = new Scanner(System.in);
        boolean listening = true;
        while (listening) {
            System.out.print("$ ");
            String command = in.nextLine();
            if ("quit".equals(command)) {
                try {
                    listening = false;
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Not a command");
            }
        }
    }

    ServerSocket serverSocket;
}
