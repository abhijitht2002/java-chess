package com.chess.ui;

import javax.swing.*;
import java.awt.*;

import static com.chess.config.Config.BACKGROUND_COLOUR;

public class MainMenu extends JPanel {

    private MenuListener menuListener;

    public MainMenu(MenuListener menuListener) {
        this.menuListener = menuListener;
        InitializeUI();
    }

    public void InitializeUI(){

        setBackground(BACKGROUND_COLOUR);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JButton localMultiplayer = new JButton("Local Multiplayer");
        JButton settingsButton = new JButton("Settings");
        JButton exitButton = new JButton("Exit");

        // Add spacing between buttons
        localMultiplayer.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        localMultiplayer.addActionListener(e -> menuListener.onGameStart("Local"));
        settingsButton.addActionListener(e -> menuListener.onGameStart("Settings"));
        exitButton.addActionListener(e -> System.exit(0));

        // Add buttons with spacing
        add(Box.createVerticalGlue());
        add(localMultiplayer);
        add(Box.createVerticalStrut(10));
        add(settingsButton);
        add(Box.createVerticalStrut(10));
        add(exitButton);
        add(Box.createVerticalGlue());
    }
}
