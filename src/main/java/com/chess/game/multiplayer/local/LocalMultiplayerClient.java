package com.chess.game.multiplayer.local;

import com.chess.ui.MenuListener;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LocalMultiplayerClient extends Thread{
    private String serverAddress;
    private int port;
    private String hostName;
    private String clientName;
    private String hostColor;
    private MenuListener menuListener;
    private LocalMultiplayerHandler localMultiplayerHandler;
    private Socket socket;

    public LocalMultiplayerClient(String serverAddress, int port, String clientName, MenuListener menuListener){
        this.serverAddress = serverAddress;
        this.port = port;
        this.clientName = clientName;
        this.menuListener = menuListener;
    }

    @Override
    public void run(){

        try{

            socket = new Socket(serverAddress, port);  // Connect to host at the specified IP address and port
            System.out.println("Connected to server!");

            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            // Send client name
            output.writeUTF(clientName);

            // Receive host details
            hostName = input.readUTF();
            hostColor = input.readUTF();

            setHostName(hostName);
            setHostColor(hostColor);

            System.out.println("Connected to Host: " + hostName + " with color: " + hostColor);

            // Start the game
            SwingUtilities.invokeLater(() -> menuListener.onGameStart("LocalGame"));

            // Pass socket to handler
            localMultiplayerHandler.startGame(socket, hostName, clientName, hostColor, false);

        }catch (IOException e){

            //  error handling for connection to server related errors
            SwingUtilities.invokeLater(() -> {
                showErrorMessage();
            });

            e.printStackTrace();
        }
    }

    //  set handler via setter injection coz constructor injection is complicating other classes
    public void setLocalMultiplayerHandler(LocalMultiplayerHandler localMultiplayerHandler) {
        this.localMultiplayerHandler = localMultiplayerHandler;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public void setHostColor(String hostColor) {
        this.hostColor = hostColor;
    }

    //  private methods
    //  error client socket notification
    private void showErrorMessage(){
        JOptionPane.showMessageDialog(null,
                "OOPS! \nConnection Failed: Could not reach the host. \nPlease check the IP and try again.",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
