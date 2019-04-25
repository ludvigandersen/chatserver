import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static org.apache.commons.lang3.StringUtils.split;

class ClientThread extends Thread {

    private Socket client;
    private Scanner input;
    private PrintWriter output;
    private String username;
    private final Server server;

    ClientThread(Socket socket, Server server){
        this.client = socket;
        this.server = server;

        System.out.println("Creating input/output stream");
        try {
            input = new Scanner(client.getInputStream());
            output = new PrintWriter(client.getOutputStream(), true);


        } catch (IOException e) {
            e.printStackTrace();
        }

        output.println("J_OK");


    }

    @Override
    public void run() {
        String received;
        String nameReceived = input.nextLine();

        //Forsøg på at tjekke duplicate usernames, virker ikke.
        for (ClientThread ct: server.getClientList()) {
            String checkName = ct.getUsername();
            if (nameReceived.equals(checkName)){
                writeMsg("Username not available");
                username = input.nextLine();
            } else{
                username = nameReceived;
                break;
            }

        }
        try {
            server.broadcastMsg(username + " Has joined the server, say Hello :)\n");
            server.broadcastMsg("Current users on the server:");
            server.printUserList();
        } catch (IOException e) {
            e.printStackTrace();
        }


        while (true) {
            //Accept message from client on
            //the socket's input stream
            received = input.nextLine();
            String[] splitString = splitStringArray(received);


            if (splitString[0].equalsIgnoreCase("QUIT")) {
                System.out.println("Closing down connection...");
                try {
                    client.close();
                    server.removeClient(username);
                    server.broadcastMsg("User has left the server: " + username + "\n");
                    server.broadcastMsg("Remaining users on the server:");
                    server.printUserList();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Unable to disconnect");
                }
            } else if (splitString[0].equalsIgnoreCase("DATA")) {
                String msg = splitString[1] + " " + splitString[2];
                System.out.println(msg);

                try {
                    server.broadcastMsg(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (splitString[0].equalsIgnoreCase("LIST")) {

                try {
                    server.broadcastMsg("Current users on the server:");
                    server.printUserList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (splitString[0].equalsIgnoreCase("JOIN")){

            }

        }


    }

    //Bruges til at dele den modtagede streng fra klienten op
    //i kommando og besked.
    public static String[] splitStringArray(String string) {
        try {
            String[] split = string.split(" ",3);
            return split;

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e);
            String[] array = new String[1];
            array[0] = string;
            return array;
        }

    }

    //Bruges til at sende en besked til enkelt klient.
    public void writeMsg(String msg) {
        try {
            output.println(msg);
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Socket getClient() {
        return client;
    }
}
