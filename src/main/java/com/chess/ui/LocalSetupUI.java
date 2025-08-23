package com.chess.ui;

import com.chess.game.multiplayer.local.LocalMultiplayerClient;
import com.chess.game.multiplayer.local.LocalMultiplayerHandler;
import com.chess.game.multiplayer.local.LocalMultiplayerHost;

import javax.swing.*;
import java.awt.*;

public class LocalSetupUI extends JPanel {

    private Color bgColor = new Color(175, 122, 95);
    private MenuListener menuListener;
    private CardLayout cardLayout;
    private JPanel mainPanel, hostSetupPanel, joinSetupPanel;
    private LocalMultiplayerHandler localMultiplayerHandler;

    public LocalSetupUI(MenuListener menuListener, LocalMultiplayerHandler localMultiplayerHandler) {
        this.menuListener = menuListener;
        this.localMultiplayerHandler = localMultiplayerHandler;
        InitializeUI();
    }

    public void InitializeUI(){
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        // Create the main panel with Host/Join/back buttons
        mainPanel = createMainPanel();
        //  creating the host setup panel with choose color and start the game
        hostSetupPanel = createHostSetupPanel();
        //  creating the join setup panel with ip address and join the game
        joinSetupPanel = createJoinSetupPanel();

        add(mainPanel, "MainPanel");
        add(hostSetupPanel, "HostSetupPanel");
        add(joinSetupPanel, "JoinSetupPanel");

        // Show main panel by default
        cardLayout.show(this, "MainPanel");
    }

    // Creates the main menu panel with Host, Join, and Back buttons
    private JPanel createMainPanel() {

        JPanel panel = new JPanel();
        panel.setBackground(bgColor);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JButton hostButton = new JButton("Host Game");
        JButton joinButton = new JButton("Join Game");
        JButton backButton = new JButton("Back to Main Menu");

        styleAndAlignButton(hostButton);
        styleAndAlignButton(joinButton);
        styleAndAlignButton(backButton);

        hostButton.addActionListener(e -> cardLayout.show(this, "HostSetupPanel"));
        joinButton.addActionListener(e -> cardLayout.show(this, "JoinSetupPanel"));
        backButton.addActionListener(e -> menuListener.onGameStart("Home"));

        panel.add(Box.createVerticalGlue());
        panel.add(hostButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(joinButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(backButton);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // Creates the Host Setup panel where the player chooses a color and starts game
    private JPanel createHostSetupPanel() {

        JPanel panel = new JPanel();
        panel.setBackground(bgColor);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Color Selection Panel (Encapsulating Label + Radio Buttons)
        JPanel colorSelectionPanel = new JPanel();
        colorSelectionPanel.setBackground(bgColor);
        colorSelectionPanel.setLayout(new BoxLayout(colorSelectionPanel, BoxLayout.Y_AXIS));
        colorSelectionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel chooseColorLabel = new JLabel("Choose Your Color:");
        chooseColorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        //  radiobuttons
        JRadioButton whiteButton = new JRadioButton("White", true); // default selection is active
        JRadioButton blackButton = new JRadioButton("Black");

        //  button group for radiobuttons
        ButtonGroup colorGroup = new ButtonGroup();
        colorGroup.add(whiteButton);
        colorGroup.add(blackButton);

        //  set bg color for radiobuttons
        whiteButton.setBackground(bgColor);
        blackButton.setBackground(bgColor);
        //  align radiobuttons
        whiteButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        blackButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add components to colorSelectionPanel
        colorSelectionPanel.add(chooseColorLabel);
        colorSelectionPanel.add(Box.createVerticalStrut(5)); // Add spacing
        colorSelectionPanel.add(whiteButton);
        colorSelectionPanel.add(blackButton);

        //  start game and back button
        JButton startGameButton = new JButton("Start Game");
        JButton backButton = new JButton("Back");

        styleAndAlignButton(startGameButton);
        styleAndAlignButton(backButton);

        panel.add(Box.createVerticalGlue());
        panel.add(colorSelectionPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(startGameButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(backButton);
        panel.add(Box.createVerticalGlue());

        startGameButton.addActionListener(e -> {
            String color = whiteButton.isSelected() ? "WHITE" : "BLACK";

            LocalMultiplayerHost host = new LocalMultiplayerHost(5000, "testhost", color, menuListener);
            host.setLocalMultiplayerHandler(localMultiplayerHandler);
            host.start();

        });

        backButton.addActionListener(e -> cardLayout.show(this, "MainPanel"));

        return panel;
    }

    // Creates the Join Setup panel where the player type ip address and joins game
    private JPanel createJoinSetupPanel() {

        JPanel panel = new JPanel();
        panel.setBackground(bgColor);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel joinInputPanel = new JPanel();
        joinInputPanel.setBackground(bgColor);
        joinInputPanel.setLayout(new BoxLayout(joinInputPanel, BoxLayout.Y_AXIS));

        JLabel enterIPLabel = new JLabel("Enter IP Address:");
        enterIPLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField ipAddressField = new JTextField();
        ipAddressField.setMaximumSize(new Dimension(200, 30));
        ipAddressField.setAlignmentX(Component.CENTER_ALIGNMENT);

        joinInputPanel.add(enterIPLabel);
        joinInputPanel.add(Box.createVerticalStrut(10));
        joinInputPanel.add(ipAddressField);

        JButton joinGameButton = new JButton("Join Game");
        JButton backButton = new JButton("Back");

        styleAndAlignButton(joinGameButton);
        styleAndAlignButton(backButton);



        panel.add(Box.createVerticalGlue());
        panel.add(joinInputPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(joinGameButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(backButton);
        panel.add(Box.createVerticalGlue());

        joinGameButton.addActionListener(e -> {

            String ip = ipAddressField.getText();

            if (ip.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a valid IP address.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalMultiplayerClient client = new LocalMultiplayerClient(ip, 5000, "testclient", menuListener);
            client.setLocalMultiplayerHandler(localMultiplayerHandler);
            client.start();

        });

        backButton.addActionListener(e -> {
            ipAddressField.setText("");
            cardLayout.show(this, "MainPanel");
        });

        return panel;
    }

    // Helper method to style and align buttons
    //  since buttons commonly have similar allignments so avoiding redundancy
    private void styleAndAlignButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}
