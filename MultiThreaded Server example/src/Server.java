import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private List<ClientThread> clientList = new ArrayList<>();
    private final int serverPort;
    private PrintWriter output;
    private OutputStream outputStream;

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    public List<ClientThread> getClientList() {
        return clientList;
    }

    @Override
    public void run() {

        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);

            while (true) {
                System.out.println("Accepting new client connection...");
                Socket client = serverSocket.accept();
                System.out.println("\nNew client accepted. \n" + client);
                ClientThread ct = new ClientThread(client, this);
                clientList.add(ct);
                ct.start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Bruges til at broadcaste en besked til alle
    //Klienter forbundet til serveren
    void broadcastMsg(String msg) throws IOException {

        for (ClientThread ct : clientList) {
                ct.getClient().getOutputStream();
                ct.writeMsg(msg);
            }

    }

    //Bruges til at printe alle brugere forbundet til serveren
    void printUserList() throws IOException {

        for (ClientThread ct : clientList) {

                ct.getClient().getOutputStream();
                broadcastMsg(ct.getUsername());
                System.out.println(ct.getUsername());
        }
    }

    //Metode virker ikke, forsøg på at tjekke om username er i brug.
    void checkUsernameAvailability(String username) {
        String nameInUse;


        for (ClientThread ct : clientList) {
            nameInUse = ct.getUsername();
            if (username.equals(nameInUse)) {
                ct.writeMsg("Username already in use, try again");
            }

        }
    }

    //Bruges når en klient kalder kommandoen "QUIT"
    //Fjerner klienten fra listen, så vi ikke forsøger
    //at bruge klientens plads i printUserList og broadcastMsg.
    void removeClient(String username) {

        for (ClientThread ct : clientList) {
            if (username.equals(ct.getUsername())) {
            clientList.remove(ct);
            break;
            }
        }


    }


}
