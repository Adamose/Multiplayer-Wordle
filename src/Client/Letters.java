package Client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.HashMap;

//Class used to draw alphabet at bottom of screen during games
public class Letters {
    private HashMap<Character, Integer> map;
    private final char[] firstRow = {'Q','W','E','R','T','Y','U','I','O','P'};
    private final char[] secondRow = {'A','S','D','F','G','H','J','K','L'};
    private final char[] thirdRow = {'Z','X','C','V','B','N','M'};
    private final Color YELLOW = new Color(246, 190, 0);
    private final Color GREEN = new Color(50, 205, 50);
    private final Font FONT = new Font("aharoni", Font.BOLD, 20);
    private int x;
    private int y;
    private int size;

    //Constructor takes location to place letters at
    public Letters(int xLoc, int yLoc) {
        x = xLoc;
        y = yLoc;
        size = 35;
        map = new HashMap<>();

        for (char c = 'A'; c <= 'Z'; c++) {
            map.put(c, 0);
        }
    }

    //Method to paint letters
    public void paint(Graphics2D g) {
        g.setFont(FONT);

        //Painting first row
        for (int i = 0; i < 10; i++) {

            //Checking which color to paint letter
            switch (map.get(firstRow[i])) {

                case 0:
                    g.setColor(Color.GRAY);
                    break;

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

            //Painting letter
            g.fillRect(x + (size * i) + (5 * i), y, size, size);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(firstRow[i]), (x + (size * i) + (5 * i) + (size / 2)) - g.getFontMetrics().stringWidth(String.valueOf(firstRow[i])) / 2, y + 5 * (size / 6) );

        }

        //Painting second row
        for (int i = 0; i < 9; i++) {

            //Checking which color to paint letter
            switch (map.get(secondRow[i])) {

                case 0:
                    g.setColor(Color.GRAY);
                    break;

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

            //Painting letter
            g.fillRect(x + (size * i) + (5 * i) + (size / 2) + 3, y + size + 5, size, size);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(secondRow[i]), (x + (size * i) + 3 + (5 * i) + (size / 2) + (size / 2)) - g.getFontMetrics().stringWidth(String.valueOf(secondRow[i])) / 2, y + size + 5+  5 * (size / 6) );
        }

        //Painting third row
        for (int i = 0; i < 7; i++) {

            //Checking which color to paint letter
            switch (map.get(thirdRow[i])) {

                case 0:
                    g.setColor(Color.GRAY);
                    break;

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

            //Painting letter
            g.fillRect(x + (size * i) + (5 * i) + 9 + 3 * (size / 2), y + 2 * size + 10, size, size);
            g.setColor(Color.WHITE);
            g.drawString(String.valueOf(thirdRow[i]), (x + (size * i) + (5 * i) + 9 + (size / 2) + 3 * (size / 2)) - g.getFontMetrics().stringWidth(String.valueOf(thirdRow[i])) / 2, y + 2 * size + 10 +  5 * (size / 6) );
        }
    }

    //Method used to change state (color) of a letter
    public void setLetterState(char c, int state) {
        switch (state) {
            case 1:
                if (map.get(c) == 0) map.put(c, 1);
                break;

            case 2:
                if (map.get(c) != 3) map.put(c, 2);
                break;

            case 3:
                map.put(c, 3);
                break;
        }
    }

    //Method to initialize all letters into hashmap with default color
    public void set() {
        for (char c = 'A'; c <= 'Z'; c++) {
            map.put(c, 0);
        }
    }
}