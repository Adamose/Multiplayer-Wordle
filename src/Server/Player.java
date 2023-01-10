package Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//Player class used to receive data from a socket
public class Player implements Runnable {
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread thread;
    private String name;
    private Socket socket;
    private Game game;
    private boolean active;

    //Constructor takes a connected socket
    public Player(Socket s) throws Exception {
        active = true;
        socket = s;
        game = null;
        s.setSoTimeout(0);
        reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        writer = new PrintWriter(s.getOutputStream(), true);

        //First piece of data received is always the player's name
        name = reader.readLine();

        //Starting new thread to infinitely wait for data from socket
        thread = new Thread(this);
        thread.start();
    }

    //Method for closing connection to socket
    public void quit() {

        //If player is active, sending them the quit event
        if (active) {
            write("Q");
        }

        writer.close();

        try {
            reader.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method to process data from socket
    @Override
    public void run() {

        try {

            //Infinitely reads data as long as player is active
            while (active) {
                String input = reader.readLine();

                //If receiving quit event from player, closing game
                if (input.equals("Q")) {
                    active = false;
                    game.quit();

                //Telling game that player sent the VK_ENTER event
                } else if (input.equals("E")) {
                    game.setEnter(name);

                //Telling game that player sent a guess
                } else {
                    game.setGuess(name, input);
                }
            }

        } catch (Exception e) {}
    }

    //Method to send data to player
    public void write(String s) { writer.println(s); }

    //Getter to get player's name
    public String getName() { return name; }

    //Setter to set player's game variable
    public void setGame(Game game) { this.game = game; }
}