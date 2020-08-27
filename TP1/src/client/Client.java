package client;

import common.ConnectionHost;
import common.Credentials;
import common.ImagePacket;
import common.UserStatus;
import common.validation.GetInformation;
import common.validation.Side;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import static common.ImagePacket.defaultFormat;

public class Client
{
    public static void main(String[] args)
    {
        GetInformation infoGetter = new GetInformation(Side.CLIENT);
        ConnectionHost connectionHost = infoGetter.getConnectionHostInfo();

// Communication with server____________________________________________________________________________________________
        // Configure socket with output and input streams
        try (
                Socket socket = new Socket(connectionHost.ip, connectionHost.port);
                ObjectOutputStream out =  new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            // Collect Credentials from user and greats him with appropriate message depending on the case
            boolean logging_in = true;
            while (logging_in) {
                Credentials credentials = infoGetter.getCredentials();
                out.writeObject(credentials);
                out.flush();
                switch ((UserStatus) in.readObject()) {
                    case LOGGED_IN:
                        System.out.println("Welcome back " + credentials.username + " !");
                        logging_in = false;
                        break;
                    case CREATED:
                        System.out.println("Your profile was created. Welcome " + credentials.username + " !");
                        logging_in = false;
                        break;
                    case BAD:
                        System.out.println("Wrong password");
                        break;
                }
            }

            // Shell that reads user input for rendering or quitting
            Scanner scanner = new Scanner(System.in);
            boolean commanding = true;
            System.out.println("Commands");
            System.out.println("'render <src image path> <dst image path>' to render your image (" + defaultFormat + " format only)");
            System.out.println("'quit' to disconnect from server");
            while (commanding) {
                System.out.print("$ ");
                String command = scanner.nextLine();
                if (command.startsWith("render")) {
                    String[] cmdArgs = command.split(" ");
                    if (cmdArgs.length == 3){
                        // Send image to be rendered
                        String imagePath = cmdArgs[1];
                        try {
                            ImagePacket imagePacket = new ImagePacket(
                                    imagePath.split("/")[imagePath.split("/").length - 1],
                                    ImageIO.read(new File(imagePath)),
                                    defaultFormat);
                            out.writeObject(imagePacket);
                            out.flush();
                            System.out.println("Image sent");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // Receive rendered image from server
                        ImagePacket renderedImagePacket = (ImagePacket)in.readObject();
                        String renderedImagePath = cmdArgs[2];
                        try {
                            ImageIO.write(renderedImagePacket.image, defaultFormat, new File(renderedImagePath));
                            System.out.println("Rendered image received and written to " + renderedImagePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        commanding = false;
                    } else {
                        System.out.println("Usage: render src dst");
                    }
                }else if ("quit".equals(command)) {
                    commanding = false;
                } else {
                    System.out.println("Not a command");
                }
            }
            System.out.println("Bye.");
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + connectionHost.ip);
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Couldn't get I/O for the connection to " +
                    connectionHost.ip);
            System.exit(1);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
