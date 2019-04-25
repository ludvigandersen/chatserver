import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientThread {


    static void start() {

        Socket socket;
        Scanner networkInput;
        PrintWriter networkOutput;
        Scanner sc = new Scanner(System.in);
        System.out.println("Type server IP and Port:JOIN <<user_name>>,<<server_ip>>:<<server_port>>\n");
        String joinString = sc.nextLine();
        String[] joinParts = joinString.split(",| |:");
        String join = joinParts[0];
        String userName = joinParts[1];
        String serverIp = joinParts[2];
        int serverPort = Integer.parseInt(joinParts[3]);
        System.out.println(join + " " + userName + " " + serverIp + " " + serverPort);


        try {
            //Connecting to the server
            socket = new Socket(serverIp, serverPort);


            //Creating both datastreams, input/output
            networkInput = new Scanner(socket.getInputStream());
            networkOutput = new PrintWriter(socket.getOutputStream(), true);
            String msg = "Connection accepted " + serverIp + ":" + serverPort;
            System.out.println(msg);
            String success;
            success = networkInput.nextLine();
            System.out.println(success);


            //Send username to the server
            networkOutput.println(userName);

            //set up stream for keyboard entry...
            Scanner userEntry = new Scanner(System.in);

            System.out.println("Enter command 'QUIT' to exit");
            System.out.println("Enter command 'DATA <<user_name>>: <<free text...>>' to broadcast message");
            System.out.println("Enter command 'LIST' to see active users\n");


            //ListenFromServer tråden kører konstant, indtil QUIT,
            //Og lytter til alle beskeder, der bliver sendt fra serveren.
            ListenFromServer lfs = new ListenFromServer(networkInput);
            lfs.start();

            //While loop, der kører konstant indtil kommandoen (sendParts[0])
            // Er lig med "QUIT"
            while (true) {

                String send = userEntry.nextLine();
                String[] sendParts = splitStringArray(send);

                //De 3 mulige client kommandoer, der er delt op i if statements
                //QUIT (disconnect klienten), DATA(Send besked til server og videre til andre klienter
                //LIST, printer en liste af alle aktive klienter ud
                if (sendParts[0].equalsIgnoreCase("QUIT")) {

                    networkOutput.println("QUIT");
                    System.out.println("\nClosing connection...");
                    socket.close();
                    lfs.close();
                    break;
                } else if (sendParts[0].equalsIgnoreCase("DATA")) {
                    if (sendParts[2].length() < 251) {
                        networkOutput.println(sendParts[0] + " " + sendParts[1] + " " + sendParts[2]);
                    } else {
                        System.out.println("Max 250 user characters in message");
                    }

                } else if (sendParts[0].equalsIgnoreCase("LIST")) {
                    networkOutput.println(sendParts[0]);
                } else if (sendParts[0].equalsIgnoreCase("JOIN")){
                    networkOutput.println(sendParts[0] + " " + sendParts[1]);
                }
            }


        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    //Bruges til at splitte kommandoen, første del af strengen (sendParts[0]), fra resten af strengen.
    public static String[] splitStringArray(String string) {
        try {
            String[] split = string.split(" ", 3);
            return split;

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(e);
            String[] array = new String[1];
            array[0] = string;
            return array;
        }

    }


}
