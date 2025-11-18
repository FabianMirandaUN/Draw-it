/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

/**
 *
 * @author FabiFree
 */
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class StatusPanel extends JPanel {

    private final JLabel artistLabel;
    private final JLabel roundLabel;
    private final JLabel timeLabel;
    private final JLabel messageLabel;
    private final JTextArea playersArea;
    private final JTextArea scoresArea;
    private final ToolsPanel h;

    public StatusPanel(ToolsPanel h) {
        this.h = h;
        setLayout(new BorderLayout());

        // Panel superior con información básica
        JPanel infoPanel = new JPanel(new GridLayout(1, 3));
        artistLabel = new JLabel("Artista: -");
        roundLabel = new JLabel("Ronda: -");
        timeLabel = new JLabel("Tiempo: -");
        infoPanel.add(artistLabel);
        infoPanel.add(roundLabel);
        infoPanel.add(timeLabel);

        // Mensaje dinámico
        messageLabel = new JLabel("Esperando inicio...");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Áreas de jugadores y puntajes
        playersArea = new JTextArea(5, 15);
        playersArea.setEditable(false);
        scoresArea = new JTextArea(5, 15);
        scoresArea.setEditable(false);

        JPanel listsPanel = new JPanel(new GridLayout(1, 2));
        listsPanel.add(new JScrollPane(playersArea));
        listsPanel.add(new JScrollPane(scoresArea));

        add(infoPanel, BorderLayout.NORTH);
        add(messageLabel, BorderLayout.CENTER);
        add(listsPanel, BorderLayout.SOUTH);
    }
    

    // ✅ Nuevo método para mostrar mensajes libres
    public void setMessage(String msg) {
        messageLabel.setText(msg);
    }

    public void setArtist(String artist) {
        artistLabel.setText("Artista: " + artist);
    }

    public void setRound(int round) {
        roundLabel.setText("Ronda: " + round);
    }

    public void setDuration(int duration) {
        h.setTime(duration);
    }

    public void setRemaining(int remaining) {
        h.setTime(remaining);
    }

    public void updatePlayers(List<String> players) {
        playersArea.setText("Jugadores:\n");
        for (String p : players) {
            playersArea.append(p + "\n");
        }
    }
    public void updatePlayers(List<String> players, JTextArea playersArea) {
        playersArea.setText("Jugadores:\n");
        for (String p : players) {
            playersArea.append(p + "\n");
        }
    }

    public void updateScores(List<Map<String, Object>> scores) {
        scoresArea.setText("Puntajes:\n");
        for (Map<String, Object> s : scores) {
            scoresArea.append(s.get("player") + ": " + s.get("score") + "\n");
        }
    }
}
