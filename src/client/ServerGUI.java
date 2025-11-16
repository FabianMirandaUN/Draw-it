/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */package client;

/**
 *
 * @author FabiFree
 */
import common.Protocol;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.util.Map;

public class ServerGUI extends JFrame {

    private final String playerName;
    private final ToolsPanel tools;
    private final DrawingPanel drawing;
    private final GuessPanel guess;
    private final StatusPanel status;

    public ServerGUI(String playerName, PrintWriter out) {
        super("Draw It - " + playerName);
        this.playerName = playerName;

        setLayout(new BorderLayout());

        tools = new ToolsPanel(out);
        drawing = new DrawingPanel(out, tools);
        guess = new GuessPanel(out, playerName);
        status = new StatusPanel();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(status, BorderLayout.CENTER);

        // Right after creating 'out' and before you ever press "Iniciar partida":
        out.println(Protocol.encode(Map.of("type", "JOIN", "player", playerName)));
        out.flush(); // extra safety if autoFlush wasn't used

        JButton iniciarBtn = new JButton("Iniciar partida");
        iniciarBtn.addActionListener(e -> {

            out.println(Protocol.encode(Map.of("type", "START_REQUEST")));
        });
        topPanel.add(iniciarBtn, BorderLayout.WEST);

        add(tools, BorderLayout.EAST);
        add(drawing, BorderLayout.CENTER);
        add(guess, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void handleMessage(String json) {
        Map<String, Object> msg = Protocol.decode(json);
        String type = (String) msg.get("type");
        if (type == null) {
            return;
        }

        switch (type) {
            case "START" -> {
                String artist = (String) msg.get("artist");
                boolean iAmArtist = playerName.equals(artist);

                tools.setEnabled(iAmArtist);
                drawing.setArtist(iAmArtist);   // MUST actually toggle input
                guess.setEnabled(!iAmArtist);

                drawing.clearCanvasLocal();
                status.setArtist(artist);
                status.setRound(((Double) msg.get("round")).intValue());
                status.setDuration(((Double) msg.get("duration")).intValue());
            }
            case "WORD" -> {
                drawing.setSecretWord((String) msg.get("value")); // only the artist should display it
            }

            case "DRAW" ->
                drawing.addRemoteStroke(msg);
            case "CLEAR" ->
                drawing.clearCanvasRemote();
            case "TIME" ->
                status.setRemaining(((Double) msg.get("remaining")).intValue());
            case "WINNER" -> {
                String winner = (String) msg.get("player");
                String word = (String) msg.get("word");
                status.updateScores((java.util.List<Map<String, Object>>) msg.get("scores"));
                JOptionPane.showMessageDialog(this, "Ganador: " + winner + " | Palabra: " + word);
            }
            case "ROUND_END" -> {
                String result = (String) msg.get("result");
                if ("timeout".equals(result)) {
                    String word = (String) msg.get("word");
                    JOptionPane.showMessageDialog(this, "Tiempo agotado. La palabra era: " + word);
                }
                tools.setEnabled(false);
                drawing.setArtist(false);
                guess.setEnabled(false);
            }
            case "SCORES" ->
                status.updateScores((java.util.List<Map<String, Object>>) msg.get("players"));
            case "PLAYERS" ->
                status.updatePlayers((java.util.List<String>) msg.get("list"));
            default -> {
                /* opcional: log */ }
        }
    }

}
