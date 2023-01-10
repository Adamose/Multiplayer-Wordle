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

//Class for single player game panel
public class Singleplayer extends JPanel {
    private Slot[] slots;
    private int currentSlot;
    private int errorRow;
    private String guess;
    private String word;
    private boolean inGame;
    private boolean showError;
    private boolean showWord;
    private boolean showLetters;
    private Main main;
    private Letters letters;

    private final Font TITLEFONT = new Font("aharoni", Font.BOLD, 50);
    private final Font TEXTFONT = new Font("aharoni", Font.BOLD, 20);

    //Constructor takes main (to return to menu)
    public Singleplayer(Main m) {
        main = m;
        letters = new Letters(103, 520);
        slots = new Slot[30];
        setSlots();
        this.setPreferredSize(new Dimension(600, 680));
        this.setFocusable(true);

        //Setting event listeners from key presses
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {

                //Closing game and going back to menu if user presses escape
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    main.setPanel(new Menu(main));

                //User presses enter to start a new round
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!inGame) {
                        play();

                    //User tries to make a guess by pressing enter
                    //Making sure guess is long enough
                    } else if (guess.length() == 5) {

                        //Making sure guess is a valid word
                        if (main.isValidWord(guess.toLowerCase())) {
                            testGuess();
                            guess = "";
                            repaint();
                        } else {
                            showError = true;
                            errorRow = (int) Math.ceil(currentSlot / 5) - 1;
                            repaint();
                        }
                    }
                } else if (inGame) {

                    //User tries to delete a letter from guess
                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {

                        //Making sure user's guess has a letter to delete
                        if (guess.length() != 0) {

                            //Deleting letter from user's guess
                            guess = guess.substring(0, guess.length() - 1);
                            slots[currentSlot - 1].setGuess(null);
                            slots[currentSlot - 1].showLetter(false);
                            currentSlot--;
                            repaint();
                        }

                    //User pressed a letter
                    } else if (Character.isAlphabetic(e.getKeyChar())) {

                        //Checking user hasn't already put 5 letters
                        if (guess.length() < 5) {

                            //Adding letter to user's guess
                            guess = (guess + e.getKeyChar()).toUpperCase();
                            slots[currentSlot].setGuess(String.valueOf(e.getKeyChar()).toUpperCase());
                            slots[currentSlot].showLetter(true);
                            currentSlot++;
                            repaint();
                        }
                    }
                }
            }
        });
    }

    //Method to set defaults when starting a new round
    public void play() {
        setSlots();
        letters.set();
        inGame = true;
        showWord = false;
        showLetters = false;
        currentSlot = 0;
        guess = "";

        //Getting word user has to guess
        try {
            word = main.getWord().toUpperCase();
            System.out.println(word);
        } catch (Exception e) {

            e.printStackTrace();
        }

        repaint();
    }

    //Method to test a guess
    public void testGuess() {
        char[] wordChars = word.toCharArray();

        //Checking for any letters that are in the correct spot (green)
        for (int i = 1; i < 6; i++) {

           if (slots[currentSlot - i].getLetter().charAt(0) == wordChars[5 - i]) {
               slots[currentSlot - i].setState(3);
               letters.setLetterState(slots[currentSlot - i].getLetter().charAt(0), 3);
               wordChars[5 - i] = '?';
           }

        }

        //Checking for letters that are in the word but not at the correct spot
        for (int i = 1; i < 6; i++) {

            for (int y = 0; y < 5; y++) {

                 if (slots[currentSlot - i].getState() != 3 && slots[currentSlot - i].getState() != 2 && slots[currentSlot - i].getLetter().charAt(0) == wordChars[y]) {
                     slots[currentSlot - i].setState(2);
                     letters.setLetterState(slots[currentSlot - i].getLetter().charAt(0), 2);
                     wordChars[y] = '?';
                 }

            }

            if (slots[currentSlot - i].getState() != 3 && slots[currentSlot - i].getState() != 2) {
                slots[currentSlot - i].setState(1);
                letters.setLetterState(slots[currentSlot - i].getLetter().charAt(0), 1);
            }
        }

        //Checking if user guessed the word correctly
        if (guess.equals(word)) {
            inGame = false;

        //Checking if user used all their guesses
        } else if (currentSlot == 30) {
            inGame = false;
            showWord = true;
        }
    }

    //Method to initialize slots
    public void setSlots() {
        int i = 0;

        for (int row = 0; row < 6; row++) {

            for (int column = 0; column < 5; column++) {

                slots[i] = new Slot(165 + (column * 50) + (column * 5), 150 + (row * 50) + (row * 5));
                i++;
            }
        }
    }

    //Method to draw screen
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        //Drawing background and title
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, 600, 680);
        g2.setColor(Color.WHITE);
        g2.setFont(TITLEFONT);
        g2.drawString("Wordle", 300 - g2.getFontMetrics().stringWidth("Wordle") / 2, 75);

        //Drawing all slots
        for (int i = 0; i < 30; i++) {
            slots[i].paint(g2);
        }

        g2.setFont(TEXTFONT);

        //Checking if there is an error to draw
        if (showError) {
            g2.setColor(Color.RED);
            g2.drawRect(162, 147 + (errorRow * 50) + (errorRow * 5), 277, 56);
            g2.drawString("Not In Word List", 300 - g2.getFontMetrics().stringWidth("Not In Word List") / 2, 125);
            showError = false;
        }

        //Checking if word should be drawn
        if (showWord) {
            g2.setColor(Color.RED);
            g2.drawString("The Word Was: " + word, 300 - g2.getFontMetrics().stringWidth("The Word Was: " + word) / 2, 505);
        }

        g2.setColor(Color.WHITE);

        //Drawing starting game message
        if (!inGame) {
            g2.drawString("Press Enter To Start New Game", 300 - g2.getFontMetrics().stringWidth("Press Enter To Start New Game") / 2, 125);
        }

        //Drawing letters at bottom of screen
        letters.paint(g2);
    }
}