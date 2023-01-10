package Client;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

//Class for the menu panel
public class Menu extends JPanel {
    private JButton playButton;
    private JButton connectButton;
    private JTextField codeField;
    private JTextField ipField;
    private JTextField nameField;
    private String message;
    private boolean isError;
    private Socket socket;
    private final Font TITLEFONT = new Font("aharoni", Font.BOLD, 50);
    private final Font NAMEFONT = new Font("aharoni", Font.BOLD, 25);
    private final Font TEXTFONT = new Font("aharoni", Font.BOLD, 15);

    //Constructor for menu, adds buttons and input fields
    public Menu(Main m) {
        message = "";

        //Play button
        playButton = new JButton("PLAY");
        playButton.setBounds(250, 175, 100, 50);
        playButton.setBackground(Color.DARK_GRAY);
        playButton.setForeground(Color.CYAN);
        playButton.setFocusPainted(false);
        //Changes panel to singleplayer
        playButton.addActionListener(a -> m.setPanel(new Singleplayer(m)));

        //Connect button
        connectButton = new JButton("CONNECT");
        connectButton.setBounds(250, 540, 100, 50);
        connectButton.setBackground(Color.DARK_GRAY);
        connectButton.setForeground(Color.CYAN);
        connectButton.setFocusPainted(false);
        //Tries to join lobby
        connectButton.addActionListener(a -> {

            //Checking if user entered a name
            if (nameField.getText().length() > 0) {

                message = "Attempting Connection";
                isError = false;
                paintImmediately(0, 0, 600, 650);

                try {

                    //Trying to connect to server (crashes if server doesn't respond within 1 second)
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(ipField.getText(), 6666), 1000);

                    //Connection to server made, sending lobby code
                    PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    writer.println(codeField.getText());
                    System.out.println("Code sent");
                    String response = reader.readLine();
                    System.out.println("Response: " + response);

                    if (response.equals("2")) {
                        //Changing panel to 2 player game
                        m.setPanel(new Multiplayer(m, socket, nameField.getText()));
                    } else if (response.equals("3")) {
                        //Changing panel to 3 player game
                        m.setPanel(new Multiplayers(m, socket, nameField.getText()));
                    } else {

                        //User entered incorrect lobby code
                        isError = true;
                        message = "Lobby Not Found";
                        socket.close();
                        System.out.println("Lobby not found");
                        repaint();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    isError = true;
                    message = "Connection To Server Failed";
                    repaint();
                }

            } else {
                isError = true;
                message = "Enter An IGN";
                repaint();
            }
        });

        //Lobby code field
        codeField = new JTextField();
        codeField.setBounds(225, 420, 150, 25);
        codeField.setBackground(Color.LIGHT_GRAY);
        codeField.setFont(TEXTFONT);
        codeField.setHorizontalAlignment(JTextField.CENTER);

        //Server IP field
        ipField = new JTextField();
        ipField.setBounds(200, 345, 200, 25);
        ipField.setBackground(Color.LIGHT_GRAY);
        ipField.setFont(TEXTFONT);
        ipField.setHorizontalAlignment(JTextField.CENTER);

        //User name field
        nameField = new JTextField();
        nameField.setBounds(250, 488, 100, 25);
        nameField.setBackground(Color.LIGHT_GRAY);
        nameField.setFont(TEXTFONT);
        nameField.setHorizontalAlignment(JTextField.CENTER);

        //Setting panel's size and adding buttons/fields to it
        this.setPreferredSize(new Dimension(600, 650));
        this.setLayout(null);
        this.add(playButton);
        this.add(connectButton);
        this.add(codeField);
        this.add(ipField);
        this.add(nameField);
    }

    //Paint method to draw background and text
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, 600, 650);
        g2.setFont(TITLEFONT);
        g2.setColor(Color.WHITE);
        g2.drawString("Wordle", 300 - g2.getFontMetrics().stringWidth("Wordle") / 2, 75);
        g2.setFont(NAMEFONT);
        g2.drawString("Singleplayer", 300 - g2.getFontMetrics().stringWidth("Singleplayer") / 2, 150);
        g2.drawString("Multiplayer", 300 - g2.getFontMetrics().stringWidth("Multiplayer") / 2, 300);
        g2.setFont(TEXTFONT);
        g2.drawString("Server IP", 300 - g2.getFontMetrics().stringWidth("Server IP") / 2, 335);
        g2.drawString("Lobby Code", 300 - g2.getFontMetrics().stringWidth("Lobby Code") / 2, 410);
        g2.drawString("IGN", 300 - g2.getFontMetrics().stringWidth("IGN") / 2, 480);

        //Drawing message
        //Checking if message is an error
        if (isError) {
            g2.setColor(Color.RED);
        } else {
            g2.setColor(Color.GREEN);
        }
        g2.drawString(message, 300 - g2.getFontMetrics().stringWidth(message) / 2, 625);
    }
}