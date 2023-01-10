package Server;

import java.util.HashMap;

//Class used to manage a three player game
public class ThreePlayerGame implements Game {
    private Player playerOne;
    private Player playerTwo;
    private Player playerThree;
    private Server server;
    private int guessesMade;
    private int enters;
    private boolean active;
    private String word;
    private HashMap<String, String> guesses;

    //Constructor takes three players and the server (to get words from)
    public ThreePlayerGame(Player p1, Player p2, Player p3, Server s) {
       playerOne = p1;
       playerTwo = p2;
       playerThree = p3;
       server = s;
       guesses = new HashMap<>();
       active = true;

       //Setting player's game variable
       playerOne.setGame(this);
       playerTwo.setGame(this);
       playerThree.setGame(this);

       //Sending players' names to all players
       playerOne.write(playerTwo.getName());
       playerOne.write(playerThree.getName());
       playerTwo.write(playerOne.getName());
       playerTwo.write(playerThree.getName());
       playerThree.write(playerOne.getName());
       playerThree.write(playerTwo.getName());

       //Waiting for all players to ready up (send the VK_ENTER event)
       waitForPlayers();

       //Making sure a player didn't quit
       if (!active) {
           playerOne.quit();
           playerTwo.quit();
           playerThree.quit();
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
        playerThree.write("@" + word);

        //Loop until either 6 guesses are made or a player wins
        while (true) {
            guesses.clear();

            //Waiting for guesses from players
            waitForPlayers();

            //Making sure a player didn't quit
            if (!active) {
                playerOne.quit();
                playerTwo.quit();
                playerThree.quit();
                return true;
            }

            //Updating guesses counter once all guesses are received
            guessesMade += 1;

            //Sending players' guesses to all players
            System.out.println("Sending guesses");
            playerOne.write(guesses.get(playerTwo.getName()));
            playerOne.write(guesses.get(playerThree.getName()));
            playerTwo.write(guesses.get(playerOne.getName()));
            playerTwo.write(guesses.get(playerThree.getName()));
            playerThree.write(guesses.get(playerOne.getName()));
            playerThree.write(guesses.get(playerTwo.getName()));

            //Checking if 6 guesses have been made or if a player won
            if (guessesMade == 6 || guesses.get(playerOne.getName()).equals(word) || guesses.get(playerTwo.getName()).equals(word) || guesses.get(playerThree.getName()).equals(word)) {
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
            playerThree.quit();
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

        //If three guesses have been received, all players have sent their guess
        if (guesses.size() == 3) {

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
        if (enters == 3) {

            //Waking up thread
            notify();
        }
    }
}