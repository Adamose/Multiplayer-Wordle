package Client;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

//Main class of program
public class Main extends JFrame {
    private Random random;
    private Scanner scanner;
    private int i;
    public HashMap<Integer, String> words;

    public Main() {
        random = new Random();
        scanner = new Scanner(getClass().getResourceAsStream("/resources/words.txt"));
        words = new HashMap<>();
        i = 0;

        //Adding all words from words.txt into words' hashmap
        while (scanner.hasNextLine()) {
            words.put(i, scanner.nextLine());
            i++;
        }

        //Adding shutdown method to window closing event handler
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (getContentPane().getComponent(0) instanceof Client) {
                    ((Client) getContentPane().getComponent(0)).shutdown();
                }
                super.windowClosing(e);
            }
        });

        //Setting JFrame's properties
        setTitle("Wordle");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(new ImageIcon(getClass().getResource("/resources/icon.png")).getImage());
        getContentPane().add(new Menu(this));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    //Method to change panel (Example: go from menu to singleplayer)
    public void setPanel(JPanel newPanel) {
        getContentPane().removeAll();
        getContentPane().add(newPanel);
        pack();
        setLocationRelativeTo(null);
        newPanel.requestFocus();
    }

    //Method to get random word
    public String getWord() {
        return words.get(random.nextInt(i));
    }

    //Method to check if a word is in the words list
    public boolean isValidWord(String word) {
        return words.containsValue(word);
    }

    //Main method, creates and starts a Main JFrame class
    public static void main(String[] args) {
            new Main();
    }
}
