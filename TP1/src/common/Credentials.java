package common;

import java.io.*;
import java.util.Scanner;

public class Credentials implements Serializable
{
    public static final String userDbPath = "users.txt";
    public String username;
    public String password;

    public Credentials(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public UserStatus verify() throws IOException // Server side only
    {
        String username = "";
        String password = "";

        try{
            // Search user database file
            Scanner scanner = new Scanner (new File(userDbPath));
            scanner.useDelimiter("[,\n]");
            while (scanner.hasNext()) {
                username = scanner.next();
                password = scanner.next();
                if (username.trim().equals(this.username)) {
                    if (password.trim().equals(this.password)) {
                        scanner.close();
                        return UserStatus.LOGGED_IN;
                    } else {
                        scanner.close();
                        return UserStatus.BAD;
                    }
                }
            }

            // If user not found, create it
            File file = new File(userDbPath);
            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(this.username + "," + this.password);
            printWriter.close();
            scanner.close();
            return UserStatus.CREATED;

        } catch (FileNotFoundException e) {
            // Create user database from scratch if it doesn't exist
            File file = new File(userDbPath);
            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(this.username + "," + this.password);
            printWriter.close();
            return UserStatus.CREATED;
        } catch (IOException e) {
            // Problemo
            e.printStackTrace();
            return UserStatus.BAD;
        }
    }
}
