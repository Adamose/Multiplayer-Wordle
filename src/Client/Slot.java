package Client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

//CLass for the individual slots that make up the 6 x 5 grid the games are played on
public class Slot {
    private String letter;
    private boolean showLetter;
    private int state;
    private int size;
    private int x;
    private int y;
    private final Color YELLOW = new Color(246, 190, 0);
    private final Color GREEN = new Color(50, 205, 50);
    private Font font = new Font("aharoni", Font.BOLD, 30);

    //Constructor take location of slot
    public Slot(int xLoc, int yLoc) {
        x = xLoc;
        y = yLoc;
        size = 50;
        state = 0;
        showLetter = false;
    }

    //Painting slot to screen
    public void paint(Graphics2D g) {

        //Checking if slot is colored
        if (state != 0) {

            //Checking which color is slot
            switch (state) {
                case 1:
                    g.setColor(Color.DARK_GRAY);
                    break;

                case 2:
                    g.setColor(YELLOW);
                    break;

                case 3:
                    g.setColor(GREEN);
                    break;
            }

            //Drawing colored slot
            g.fillRect(x, y, size, size);

        } else {

            //Drawing default colored slot
            g.setColor(Color.DARK_GRAY);
            g.drawRect(x, y, size, size);

        }

        //Checking if slot contains a letter to show
        if (showLetter) {

            //Drawing letter
            g.setColor(Color.WHITE);
            g.setFont(font);
            g.drawString(letter, (x + 25) - g.getFontMetrics().stringWidth(letter) / 2, y + 37);
        }

    }

    //Method to set color of slot
    public void setState(int state) {
        this.state = state;
    }

    //Method to set letter of slot
    public void setGuess(String letter) {
        this.letter = letter;
    }

    //Method to set if letter should be drawn
    public void showLetter(boolean showLetter) {
        this.showLetter = showLetter;
    }

    //Getter for slot's letter
    public String getLetter() {
        return letter;
    }

    //Getter for slot's color
    public int getState() { return state; }
}