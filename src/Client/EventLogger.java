package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

//Event logger class used has runnable to pass to thread
//Infinitely waits for data to come from the server
public class EventLogger implements Runnable {
    private BufferedReader reader;
    private Socket socket;
    private Client client;
    public boolean active;

    //Constructor takes socket to receive data from and client to send received data to
    public EventLogger(Socket s, Client c) throws IOException {
        socket = s;
        client = c;
        active = true;
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        try {
            //First message sent by server is always the first opponent's name
            client.setOppName(reader.readLine());

            //Checking if client is in a three man lobby
            if (client instanceof Multiplayers) {
                //If so, second message sent by server is always the second opponent's name
                client.setOppName(reader.readLine());
            }

            //Notifies client when data is sent from server
            while (active) { client.inputEvent(reader.readLine()); }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method to close event logger
    public void quit() throws IOException {
        active = false;
        socket.close();
        reader.close();
    }
}