import java.io.IOException;
import java.util.Scanner;

class ListenFromServer extends Thread {

    private Scanner networkInput;
    private boolean isRunning = true;

    public void setRunning(boolean running) {
        isRunning = running;
    }

    ListenFromServer(Scanner networkInput){
        this.networkInput = networkInput;
    }

    public void run() {
        while(isRunning) {

            try {
                // read the message form the input datastream
                String msg = networkInput.nextLine();
                // print the message
                System.out.println(msg);
            }catch (Exception e){
                break;
            }
        }
        System.out.println("ListenFromServer thread is closing");
    }

    public void close(){

        networkInput.close();
        setRunning(false);
        currentThread().interrupt();

    }


}
