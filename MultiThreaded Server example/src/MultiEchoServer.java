import java.io.IOException;

public class MultiEchoServer {

    private static int port = 1234;

    public static void main(String[] args) throws IOException {
        port = 1234;
        Server server = new Server(port);
        server.start();

    }

}

