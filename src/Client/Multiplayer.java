package Client;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

//Class for 2 player game panel
public class Multiplayer extends JPanel implements Client {
    private Slot[] playerSlots;
    private Slot[] oppSlots;
    private int currentSlot;
    private EventLogger eventLogger;
    private String word;
    private String playerGuess;
    private String oppGuess;
    private String playerName;
    public String oppName;
    private int playerPoints;
    private int oppPoints;
    public int messageCode;
    private boolean playerPlayed;
    private boolean oppPlayed;
    private boolean inGame;
    private boolean showError;
    private boolean showWord;
    private int errorRow;
    private Thread eventThread;
    private PrintWriter writer;
    private Letters letters;

    private final Font TITLEFONT = new Font("aharoni", Font.BOLD, 50);
    private final Font NAMEFONT = new Font("aharoni", Font.BOLD, 25);
    private final Font TEXTFONT = new Font("aharoni", Font.BOLD, 20);

    //Constructor takes main (to return to menu), socket to read data from and the user's name
    public Multiplayer(Main main, Socket socket, String name) throws IOException {
        playerSlots = new Slot[30];
        oppSlots = new Slot[30];
        letters = new Letters(303, 540);
        currentSlot = 0;
        playerPoints = 0;
        oppPoints = 0;
        messageCode = 3;
        playerName = name;
        oppName = "Opponent";
        playerGuess = "";
        showError = true;
        inGame = false;
        oppPlayed = false;
        playerPlayed = false;
        writer = new PrintWriter(socket.getOutputStream(), true);

        //Creating thread with eventLogger using socket
        eventLogger = new EventLogger(socket, this);
        eventThread = new Thread(eventLogger);

        //Sending to server user's name
        sendToServer(playerName);
        eventThread.start();

        setPlayerSlots(true);
        setPlayerSlots(false);

        this.setPreferredSize(new Dimension(1000, 680));
        this.setFocusable(true);

        //Setting event listeners from key presses
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {

                //Closing game and going back to menu if user presses escape
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    shutdown();
                    main.setPanel(new Menu(main));
                } else if (inGame) {
                    if (!playerPlayed) {

                        //User tries to delete a letter from guess
                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {

                            //Making sure user's guess has a letter to delete
                            if (playerGuess.length() != 0) {

                                //Deleting letter from user's guess
                                playerGuess = playerGuess.substring(0, playerGuess.length() - 1);
                                playerSlots[currentSlot - 1].setGuess(null);
                                playerSlots[currentSlot - 1].showLetter(false);
                                currentSlot--;
                                repaint();
                            }

                        //User pressed a letter
                        } else if (Character.isAlphabetic(e.getKeyChar())) {

                            //Checking user hasn't already put 5 letters
                            if (playerGuess.length() < 5) {

                                //Adding letter to user's guess
                                playerGuess = (playerGuess + e.getKeyChar()).toUpperCase();
                                playerSlots[currentSlot].setGuess(String.valueOf(e.getKeyChar()).toUpperCase());
                                playerSlots[currentSlot].showLetter(true);
                                currentSlot++;
                                repaint();
                            }

                        //User tries to make a guess by pressing enter
                        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                            //Making sure guess is long enough
                            if (playerGuess.length() == 5) {

                                //Making sure guess is a valid word
                                if (main.isValidWord(playerGuess.toLowerCase())) {
                                    playerPlayed = true;
                                    messageCode = 4;

                                    //Sending guess to server
                                    sendToServer(playerGuess);
                                    processGuesses();
                                    repaint();
                                } else {
                                    showError = true;
                                    errorRow = (int) Math.ceil(currentSlot / 5) - 1;
                                    repaint();
                                }
                            }
                        }
                    }

                //Telling server user pressed enter
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (messageCode != 1) {
                        sendToServer("E");
                        messageCode = 4;
                        repaint();
                    }
                }
            }
        });
    }

    //Shutdown method for when going back to menu
    @Override
    public void shutdown() {
        if (eventLogger.active) {
            try {
                sendToServer("Q");
                eventLogger.quit();
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    //Method to process data from server
    @Override
    public void inputEvent(String input) throws Exception {
        System.out.println(input);

        //Opponent quit
        if (input.equals("Q")) {
            playerPlayed = true;
            messageCode = 1;
            eventLogger.quit();
            writer.close();
            repaint();

        //Server sending word to guess
        } else if (input.charAt(0) == '@') {
            word = input.substring(1);
            play();

        //Server sending opponent's guess
        } else {
            oppGuess = input;
            oppPlayed = true;
            processGuesses();
        }
    }

    //Method to set opponent's name
    @Override
    public void setOppName(String name) {
        oppName = name;
        messageCode = 5;
        repaint();
    }

    //Method to initialize slots
    public void setPlayerSlots(boolean isOpp) {
        int i = 0;

        if (isOpp) {
            for (int row = 0; row < 6; row++) {

                for (int column = 0; column < 5; column++) {

                    oppSlots[i] = new Slot(630 + (column * 50) + (column * 5), 150 + (row * 50) + (row * 5));
                    i++;
                }
            }
        } else {
            for (int row = 0; row < 6; row++) {

                for (int column = 0; column < 5; column++) {

                    playerSlots[i] = new Slot(100 + (column * 50) + (column * 5), 150 + (row * 50) + (row * 5));
                    i++;
                }
            }
        }
    }

    //Method to send data to server
    public void sendToServer(String message) {
        writer.println(message);
        System.out.println("Sent: " + message);
    }

    //Method to set defaults when starting a round
    public void play() {
        setPlayerSlots(true);
        setPlayerSlots(false);
        letters.set();
        inGame = true;
        showWord = false;
        currentSlot = 0;
        messageCode = 2;
        repaint();
    }

    //Method to process guesses, tests both guesses and check if a player won
    public void processGuesses() {
        if (oppPlayed && playerPlayed) {

            //Updating current row of slots with opponent's guess
            for (int i = 1; i < 6; i++ ) {
                oppSlots[currentSlot - i].setGuess(String.valueOf(oppGuess.charAt(5 - i)));
            }

            //testing guesses
            testGuess(oppSlots);
            testGuess(playerSlots);

            playerPlayed = false;
            oppPlayed = false;
            messageCode = 2;

            //Checking if user won
            if (playerGuess.equals(word)) {
                playerPoints += 1;
                inGame = false;
                messageCode = 5;
                revealOppSlots();
            }

            //Checking if opponent won
            if (oppGuess.equals(word)) {
                oppPoints += 1;
                inGame = false;
                messageCode = 5;
                revealOppSlots();
            }

            //Checking if both players lost
            if (currentSlot == 30) {
                inGame = false;
                showWord = true;
                messageCode = 5;
                revealOppSlots();
            }

            //Resetting user's guess and updating screen
            playerGuess = "";
            repaint();
        }
    }

    //Method to set all of used opponent's slots to have their letter be visible
    public void revealOppSlots() {
        for (int i = 0; i < currentSlot; i++) {
            oppSlots[i].showLetter(true);
        }
    }

    //Method to test a guess
    public void testGuess(Slot[] s) {
        char[] wordChars = word.toCharArray();

        //Checking for any letters that are in the correct spot (green)
        for (int i = 1; i < 6; i++) {

            if (s[currentSlot - i].getLetter().charAt(0) == wordChars[5 - i]) {
                s[currentSlot - i].setState(3);
                wordChars[5 - i] = '?';
                if (s == playerSlots) {
                    letters.setLetterState(s[currentSlot - i].getLetter().charAt(0), 3);
                }
            }

        }

        //Checking for letters that are in the word but not at the correct spot
        for (int i = 1; i < 6; i++) {

            for (int y = 0; y < 5; y++) {

                if (s[currentSlot - i].getState() != 3 && s[currentSlot - i].getState() != 2 && s[currentSlot - i].getLetter().charAt(0) == wordChars[y]) {
                    s[currentSlot - i].setState(2);
                    wordChars[y] = '?';
                    if (s == playerSlots) {
                        letters.setLetterState(s[currentSlot - i].getLetter().charAt(0), 2);
                    }
                }

            }

            if (s[currentSlot - i].getState() != 3 && s[currentSlot - i].getState() != 2) {
                s[currentSlot - i].setState(1);
                if (s == playerSlots) {
                    letters.setLetterState(s[currentSlot - i].getLetter().charAt(0), 1);
                }
            }
        }
    }

    //Method to draw screen
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        //Drawing background, title and points
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, 1000, 680);
        g2.setColor(Color.WHITE);
        g2.setFont(TITLEFONT);
        g2.drawString("Wordle", 500 - g2.getFontMetrics().stringWidth("Wordle") / 2, 75);
        g2.drawString(String.valueOf(playerPoints), 440 - g2.getFontMetrics().stringWidth(String.valueOf(playerPoints)) / 2, 330);
        g2.drawString(String.valueOf(oppPoints), 560 - g2.getFontMetrics().stringWidth(String.valueOf(oppPoints)) / 2, 330);
        g2.drawString("-", 498 - g2.getFontMetrics().stringWidth("-") / 2, 327);


        //Drawing all slots
        for (int i = 0; i < 30; i++) {
            playerSlots[i].paint(g2);
            oppSlots[i].paint(g2);
        }

        g2.setFont(TEXTFONT);

        //Checking if there is an error to draw
        if (showError) {
            g2.setColor(Color.RED);
            g2.drawRect(97, 147 + (errorRow * 50) + (errorRow * 5), 277, 56);
            g2.drawString("Not In Word List", 500 - g2.getFontMetrics().stringWidth("Not In Word List") / 2, 125);
            showError = false;
        }

        //Checking if word should be drawn
        if (showWord) {
            g2.setColor(Color.RED);
            g2.drawString("The Word Was: " + word, 500 - g2.getFontMetrics().stringWidth("The Word Was: " + word) / 2, 125);
        }

        g2.setColor(Color.WHITE);

        //Checking if there is a message to draw
        if (messageCode == 1) {
            g2.setColor(Color.RED);
            g2.drawString("Opponent Has Left", 500 - g2.getFontMetrics().stringWidth("Opponent Has Left") / 2, 520);
        } else if (messageCode == 2) {
            g2.drawString("Type Your Guess", 500 - g2.getFontMetrics().stringWidth("Type Your Guess") / 2, 520);
        } else if (messageCode == 3) {
            g2.drawString("Waiting For Opponent To Join Lobby", 500 - g2.getFontMetrics().stringWidth("Waiting For Opponent To Join Lobby") / 2, 520);
        } else if (messageCode == 4) {
            g2.drawString("Waiting For Opponent", 500 - g2.getFontMetrics().stringWidth("Waiting For Opponent") / 2, 520);
        } else if (messageCode == 5) {
            g2.drawString("Press Enter To Start New Game", 500 - g2.getFontMetrics().stringWidth("Press Enter To Start New Game") / 2, 520);
        }

        //Drawing player names
        g2.setFont(NAMEFONT);
        g2.drawString(playerName, 235 - g2.getFontMetrics().stringWidth(playerName) / 2, 130);
        g2.drawString(oppName, 765 - g2.getFontMetrics().stringWidth(oppName) / 2, 130);

        //Drawing letters at bottom of screen
        letters.paint(g2);
    }
}