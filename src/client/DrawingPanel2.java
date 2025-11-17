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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DrawingPanel2 extends JPanel {

    private final PrintWriter out;
    private final ToolsPanel2 tools;
    private final List<Stroke> strokes = new ArrayList<>();
    private boolean isArtist = false;
    private String secretWord = "";

    // ✅ Declarar el JLabel para mostrar la palabra
    private final JLabel secretWordLabel = new JLabel();

    public DrawingPanel2(PrintWriter out, ToolsPanel2 tools) {
        this.out = out;
        this.tools = tools;

        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // ✅ Configurar el JLabel
        secretWordLabel.setText("Palabra: ");
        secretWordLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        secretWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        secretWordLabel.setVisible(false); // solo el artista lo ve

        add(secretWordLabel, BorderLayout.NORTH); // ✅ añadirlo al panel

        JPanel canvasPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                List<Stroke> trazosSeguros;
                synchronized (strokes) {
                    trazosSeguros = new ArrayList<>(strokes);
                }

                for (Stroke s : trazosSeguros) {
                    g2.setColor(Color.decode(s.color));
                    if ("eraser".equals(s.tool)) {
                        g2.setColor(getBackground());
                    }
                    g2.fillOval(s.x, s.y, s.size, s.size);
                }

                if (isArtist && !secretWord.isEmpty()) {
                    g2.setColor(Color.RED);
                    g2.setFont(new Font("Arial", Font.BOLD, 20));
                    FontMetrics fm = g2.getFontMetrics();
                    int textWidth = fm.stringWidth(secretWord);
                    int x = (getWidth() - textWidth) / 2;
                    g2.drawString(secretWord, x, 30);
                }
            }
        };

        canvasPanel.setBackground(Color.WHITE);

        canvasPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                drawStroke(e.getX(), e.getY());
            }
        });

        canvasPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                drawStroke(e.getX(), e.getY());
            }
        });

        add(canvasPanel, BorderLayout.CENTER);
    }

    private void drawStroke(int x, int y) {
        if (!isArtist) return;

        int size = tools.getBrushSize();
        String color = tools.getColorHex();
        String tool = tools.isEraser() ? "eraser" : "pencil";

        synchronized (strokes) {
            strokes.add(new Stroke(x, y, size, color, tool));
        }

        out.println(Protocol.encode(Map.of(
                "type", "DRAW",
                "x", x,
                "y", y,
                "size", size,
                "color", color,
                "tool", tool
        )));
        repaint();
    }

    public void addRemoteStroke(Map<String, Object> msg) {
        int x = ((Double) msg.get("x")).intValue();
        int y = ((Double) msg.get("y")).intValue();
        int size = ((Double) msg.get("size")).intValue();
        String color = (String) msg.get("color");
        String tool = (String) msg.get("tool");

        synchronized (strokes) {
            strokes.add(new Stroke(x, y, size, color, tool));
        }
        repaint();
    }

    public void clearCanvasLocal() {
        synchronized (strokes) {
            strokes.clear();
        }
        out.println(Protocol.encode(Map.of("type", "CLEAR")));
        repaint();
    }

    public void clearCanvasRemote() {
        synchronized (strokes) {
            strokes.clear();
        }
        repaint();
    }

    public void setArtist(boolean isArtist) {
        System.out.println("[Client] setArtist=" + isArtist);
        this.isArtist = isArtist;
        setCursor(isArtist ? Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
                           : Cursor.getDefaultCursor());
        setEnabled(isArtist);
        secretWordLabel.setVisible(isArtist); // ✅ mostrar u ocultar el label
        repaint();
    }

    public void setSecretWord(String word) {
        System.out.println("[Client] setSecretWord=" + word);
        this.secretWord = word;
        SwingUtilities.invokeLater(() -> {
            secretWordLabel.setText("Palabra: " + word);
            secretWordLabel.setVisible(isArtist);
        });
    }

    // Clase interna para representar un trazo
    private static class Stroke {
        final int x, y, size;
        final String color, tool;

        Stroke(int x, int y, int size, String color, String tool) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.color = color;
            this.tool = tool;
        }
    }
}

