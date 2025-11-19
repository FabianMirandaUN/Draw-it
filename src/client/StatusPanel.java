package client;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import visual.PanelRound;

public class StatusPanel extends PanelRound {

    private final JTextArea statusArea;
    private final ToolsPanel h;
    private String artist = "-";
    private int round = 0;
    private int remainingTime = 0;
    private List<String> players;
    private List<Map<String, Object>> scores;

    public StatusPanel(ToolsPanel h) {
        this.h = h;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        //  Fondo personalizado
        setBackground(new Color(253, 248, 253));

        statusArea = new JTextArea(10, 20);
        statusArea.setEditable(false);
        statusArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusArea.setBackground(new Color(253, 248, 253)); //  Fondo igual al panel

        JScrollPane scrollPane = new JScrollPane(statusArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Estado del Juego"));

        add(scrollPane, BorderLayout.CENTER);
        updateDisplay();
    }

    private void updateDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("Artista: ").append(artist).append("\n");
        sb.append("Ronda: ").append(round).append("\n");
        sb.append("Tiempo: ").append(remainingTime).append("s\n\n");

        sb.append("Jugadores y Puntajes:\n");
        if (scores != null) {
            for (Map<String, Object> s : scores) {
                sb.append(String.format("%-10s : %s\n", s.get("player"), s.get("score")));
            }
        } else if (players != null) {
            for (String p : players) {
                sb.append(p).append("\n");
            }
        }
        statusArea.setText(sb.toString());
    }

    public void setArtist(String artist) {
        this.artist = artist;
        updateDisplay();
    }

    public void setRound(int round) {
        this.round = round;
        updateDisplay();
    }

    public void setDuration(int duration) {
        h.setTime(duration);
        this.remainingTime = duration;
        updateDisplay();
    }

    public void setRemaining(int remaining) {
        h.setTime(remaining);
        this.remainingTime = remaining;
        updateDisplay();
    }

    public void updatePlayers(List<String> players) {
        this.players = players;
        updateDisplay();
    }

    public void updateScores(List<Map<String, Object>> scores) {
        this.scores = scores;
        updateDisplay();
    }
}