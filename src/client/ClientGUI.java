/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

/**
 *
 * @author FabiFree
 */
import common.Protocol;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ClientGUI extends JFrame {

    private final String playerName;
    private final ToolsPanel tools;
    private final DrawingPanel drawing;
    private final GuessPanel guess;
    private final StatusPanel status;

    public ClientGUI(String playerName, java.io.PrintWriter out) {

        super("Draw It - " + playerName);
        this.playerName = playerName;

        setLayout(new BorderLayout());

        tools = new ToolsPanel(out, null, null);
        drawing = new DrawingPanel(out, tools);
        guess = new GuessPanel(out, playerName);
        status = new StatusPanel(tools);

        add(tools, BorderLayout.WEST);
        add(drawing, BorderLayout.CENTER);
        add(guess, BorderLayout.SOUTH);
        add(status, BorderLayout.NORTH);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Right after creating 'out' and before you ever press "Iniciar partida":
        out.println(Protocol.encode(Map.of("type", "JOIN", "player", playerName)));
        out.flush(); // extra safety if autoFlush wasn't used

        // Estado inicial
        status.setMessage("Esperando inicio de partida...");
        tools.setEnabled(false);
        drawing.setArtist(false);
        guess.setEnabled(false);
        
        setContentPane(new LoadingPanel());

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

                // Cambiar pantalla de carga a la interfaz de juego
                getContentPane().removeAll();
                setLayout(new BorderLayout());
                add(tools, BorderLayout.EAST);
                add(drawing, BorderLayout.CENTER);
                add(guess, BorderLayout.SOUTH);
                add(status, BorderLayout.NORTH);
                revalidate();
                repaint();

                // Configurar roles
                tools.setEnabled(iAmArtist);
                drawing.setArtist(iAmArtist);
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
                status.setMessage("Partida finalizada. Esperando nuevo inicio...");
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

    public class LoadingPanel extends JPanel {

        public LoadingPanel() {
            setLayout(new BorderLayout());
            JLabel label = new JLabel("Esperando que el servidor inicie la partida...", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            add(label, BorderLayout.CENTER);
        }
    }

}
