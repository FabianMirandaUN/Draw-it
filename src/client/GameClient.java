/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

/**
 *
 * @author FabiFree
 */
import common.Config;
import common.Protocol;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import visual.ClienteGame;

public class GameClient {

    private final String playerName;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ClienteGame gui;

    public GameClient(String playerName) {
        this.playerName = playerName;
    }

    public void connect(String host, int port) throws IOException, InterruptedException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Enviar HELLO
        out.println(Protocol.encode(java.util.Map.of("type", "HELLO", "name", playerName)));
//GUI
        SwingUtilities.invokeLater(() -> {
            gui = new ClienteGame(playerName, out);
            gui.setSize(Config.WIDTH, Config.HEIGHT);
            gui.setVisible(true);
        });

        Thread.sleep(300); // espera breve para que la GUI se cree
//Listener
        new Thread(() -> {
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    if (gui != null) {
                        gui.handleMessage(line);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();

    }
}
