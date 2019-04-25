import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {

    private static InetAddress host;
    private static final int PORT = 1234;


    public static void main(String[] args) {
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException uhEx) {
            System.out.println("\nHost ID not found!\n");
            System.exit(1);
        }
        ClientThread.start();
    }

}
