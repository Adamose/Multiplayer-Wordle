package Server;

import java.util.HashMap;

//Class used to manage a two player game
public class TwoPlayerGame implements Game {
    private Player playerOne;
    private Player playerTwo;
    private Server server;
    private HashMap<String, String> guesses;
    private int guessesMade;
    private int enters;
    private String word;
    private boolean active;

    //Constructor takes two players and the server (to get words from)
    public TwoPlayerGame(Player p1, Player p2, Server s) {
        playerOne = p1;
        playerTwo = p2;
        server = s;
        guesses = new HashMap<>();
        active = true;

        //Setting player's game variable
        playerOne.setGame(this);
        playerTwo.setGame(this);

        //Sending players' names to all players
        playerOne.write(playerTwo.getName());
        playerTwo.write(playerOne.getName());

        //Waiting for all players to ready up (send the VK_ENTER event)
        waitForPlayers();

        //Making sure a player didn't quit
        if (!active) {
            playerOne.quit();
            playerTwo.quit();
        } else {
            playRound();
        }
    }

    //Method to play a round (6 guesses per player)
    public synchronized boolean playRound() {
        System.out.println(Server.ANSI_GREEN + "Starting New Round" + Server.ANSI_RESET);

        //Getting word players have to guess
        word = server.getWord();
        System.out.println("The Word Is: " + word);

        //Setting variables to defaults
        enters = 0;
        guessesMade = 0;
        guesses.clear();

        //Sending word to players
        playerOne.write("@" + word);
        playerTwo.write("@" + word);

        //Loop until either 6 guesses are made or a player wins
        while (true) {
            guesses.clear();

            //Waiting for guesses from players
            waitForPlayers();

            //Making sure a player didn't quit
            if (!active) {
                playerOne.quit();
                playerTwo.quit();
                return true;
            }

            //Updating guesses counter once all guesses are received
            guessesMade += 1;

            //Sending players' guesses to all players
            System.out.println("Sending guesses");
            playerOne.write(guesses.get(playerTwo.getName()));
            playerTwo.write(guesses.get(playerOne.getName()));

            //Checking if 6 guesses have been made or if a player won
            if (guessesMade == 6 || guesses.get(playerOne.getName()).equals(word) || guesses.get(playerTwo.getName()).equals(word)) {
                break;
            }
        }

        //Waiting for VK_ENTER events from players
        System.out.println("Waiting for players to press enter");
        waitForPlayers();

        //Making sure a player didn't quit
        if (!active) {
            playerOne.quit();
            playerTwo.quit();
            return true;
        }

        //Starting new round
        playRound();
        return true;
    }

    //Method to pause thread until it gets notified
    public synchronized void waitForPlayers() {
        try {
            wait();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    //Method for closing game
    @Override
    public synchronized void quit() {
        active = false;
        notify();
    }

    //Method for processing received guesses
    @Override
    public synchronized void setGuess(String playerName, String guess) {
        System.out.println("Received guess: " + guess + " from: " + playerName);
        guesses.put(playerName, guess);

        //If two guesses have been received, all players have sent their guess
        if (guesses.size() == 2) {

            //Waking up thread
            notify();
        }
    }

    //Method for processing enter events
    @Override
    public synchronized void setEnter(String playerName) {
        System.out.println("Received enter from " + playerName);
        enters += 1;

        //If enters is 3, all players are ready
        if (enters == 2) {

            //Waking up thread
            notify();
        }
    }
}