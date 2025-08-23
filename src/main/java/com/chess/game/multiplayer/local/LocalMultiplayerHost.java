package com.chess.game.multiplayer.local;

import com.chess.ui.MenuListener;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class LocalMultiplayerHost extends Thread {
    private int port;
    private ServerSocket serverSocket;
    private String hostName;
    private String color;
    private String clientName;
    private MenuListener menuListener;
    private LocalMultiplayerHandler localMultiplayerHandler;
    private Socket clientSocket;
    private JDialog waitingDialog;
    private boolean isHosting;

    public LocalMultiplayerHost(int port, String hostName, String color, MenuListener menuListener){
        this.port = port;
        this.hostName = hostName;
        this.color = color;
        this.menuListener = menuListener;
    }

    @Override
    public void run(){
        isHosting = true;

        try{    // hosting...

            serverSocket = new ServerSocket(port);
            System.out.println("Server started. Waiting for client...");

            // Show waiting popup
            showWaitingDialog();

            clientSocket = serverSocket.accept();    // wait for client

            // If hosting was canceled, close the socket and exit
            if (!isHosting) {
                serverSocket.close();
                return;
            }

            System.out.println("Client connected!");
            closeWaitingDialog(); // Close popup when client joins

            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

            // Send host details
            output.writeUTF(hostName);
            output.writeUTF(color);
            output.flush();

            //  Receive client name
            clientName = input.readUTF();
            setClientName(clientName);
            System.out.println("Client joined: " + clientName);

            // Start the game
            SwingUtilities.invokeLater(() -> menuListener.onGameStart("LocalGame"));

            // Pass socket to handler
            localMultiplayerHandler.startGame(clientSocket, hostName, clientName, color, true);

        }catch (IOException e){
            if (isHosting) {
                e.printStackTrace(); // Only print error if not manually stopped
            } else {
                System.out.println("Hosting canceled.");
            }
        }
    }

    //  set handler via setter injection coz constructor injection is complicating other classes
    public void setLocalMultiplayerHandler(LocalMultiplayerHandler localMultiplayerHandler) {
        this.localMultiplayerHandler = localMultiplayerHandler;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    //  private methods
    // Show a non-blocking waiting popup
    private void showWaitingDialog(){
        waitingDialog = new JDialog();
        waitingDialog.setTitle("Waiting for client...");
        waitingDialog.setSize(300,120);
        waitingDialog.setLocationRelativeTo(null);
        waitingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);    // close button will do nothing
//        waitingDialog.setUndecorated(true);   // This will remove the dialog box title bar entirely

        JPanel panel = new JPanel();
        panel.add(new JLabel("Waiting for Client to join..."));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> stopHosting());

        panel.add(cancelButton);
        waitingDialog.add(panel);
        waitingDialog.setModal(false);  // Non-blocking
        waitingDialog.setVisible(true);
    }

    // Close the popup when the client joins
    private void closeWaitingDialog(){
        if(waitingDialog != null){
            waitingDialog.dispose();
        }
    }

    // Stop hosting when cancel is clicked
    private void stopHosting(){
        isHosting = false; // Change flag
        closeWaitingDialog(); // Close popup
        System.out.println("Hosting canceled!");

        //  by the following code the accept() wont get interrupted and socket gets closed if not closed
        try {
            if(serverSocket != null && !serverSocket.isClosed()){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
