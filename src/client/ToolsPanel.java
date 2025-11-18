package client;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.util.Map;
import common.Protocol;
import slider_material.JsliderCustom;
import visual.PanelRound;

public class ToolsPanel extends PanelRound {

    private final PrintWriter out;
    private Color currentColor = Color.BLACK;
    private boolean eraser = false;
    private int size = 8;

    private JLabel secretWordLabel;
    private JLabel tiempo;
    private JTextField guessField;
    private JButton guessBtn;
    private Timer countdownTimer;
    private int remainingSeconds;

    public ToolsPanel(PrintWriter out, JsliderCustom grosor, JLabel tiempo) {
        this.out = out;
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5)); // horizontal
        setPreferredSize(new Dimension(1100, 70));
        setBackground(Color.WHITE);
        this.tiempo = tiempo;

        // Colores predefinidos
        Color[] colors = {
            new Color(128, 0, 128), // púrpura
            Color.PINK,
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.YELLOW,
            Color.RED,
            Color.ORANGE,
            new Color(139, 69, 19), // marrón
            Color.BLACK,
            Color.WHITE
        };

        for (Color color : colors) {
            JButton colorBtn = new JButton();
            colorBtn.setBackground(color);
            colorBtn.setPreferredSize(new Dimension(40, 40));
            colorBtn.setOpaque(true);
            colorBtn.setBorderPainted(false);
            colorBtn.addActionListener(e -> {
                currentColor = color;
                eraser = false;
            });
            add(colorBtn);
        }

        // Botón JColorChooser
        JButton paletteBtn = new JButton("🎨");
        paletteBtn.setFont(new Font("Arial", Font.BOLD, 16));
        paletteBtn.setPreferredSize(new Dimension(50, 40));
        paletteBtn.setBackground(Color.LIGHT_GRAY);
        paletteBtn.addActionListener(e -> {
            Color chosen = JColorChooser.showDialog(this, "Elegir color personalizado", currentColor);
            if (chosen != null) {
                currentColor = chosen;
                eraser = false;
            }
        });
        add(paletteBtn);

        // Borrador
        JToggleButton eraserBtn = new JToggleButton("🧽");
        eraserBtn.setFont(new Font("Arial", Font.BOLD, 16));
        eraserBtn.setBackground(Color.LIGHT_GRAY);
        eraserBtn.setPreferredSize(new Dimension(50, 40));
        eraserBtn.addActionListener(e -> eraser = eraserBtn.isSelected());
        add(eraserBtn);

        // Limpiar
        JButton clearBtn = new JButton("✋");
        clearBtn.setFont(new Font("Arial", Font.BOLD, 16));
        clearBtn.setBackground(Color.LIGHT_GRAY);
        clearBtn.setPreferredSize(new Dimension(50, 40));
        clearBtn.addActionListener(e -> out.println(Protocol.encode(Map.of("type", "CLEAR"))));
        add(clearBtn);

        // slider de grosor
        grosor.setPreferredSize(new Dimension(150, 40));
        grosor.addChangeListener(e -> size = grosor.getValue());

        // palabra secreta (solo dibujante)
        secretWordLabel = new JLabel("Palabra: ");
        secretWordLabel.setForeground(new Color(60, 0, 80));
        secretWordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(secretWordLabel);

        tiempo = new JLabel("00:00");
        tiempo.setFont(new Font("Arial", Font.BOLD, 18));
        tiempo.setForeground(new Color(251, 178, 99)); // color solicitado

        // campo de adivinanza (solo adivinador)
        guessField = new JTextField(10);
        guessField.setForeground(new Color(60, 0, 80));
        guessBtn = new JButton("Adivinar");
        guessBtn.addActionListener(e -> {
            String guess = guessField.getText().trim();
            if (!guess.isEmpty()) {
                out.println(Protocol.encode(Map.of("type", "GUESS", "value", guess)));
                guessField.setText("");
            }
        });
        add(guessField);
        add(guessBtn);

        // Ocultar ambos al inicio
        secretWordLabel.setVisible(false);
        guessField.setVisible(false);
        guessBtn.setVisible(false);
    }

    public String getColorHex() {
        return String.format("#%02x%02x%02x", currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue());
    }

    public boolean isEraser() {
        return eraser;
    }

    public int getBrushSize() {
        return size;
    }

    public void setTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        tiempo.setText(String.format("%02d:%02d", minutes, secs));
    }

    public void setArtist(boolean isArtist) {
        secretWordLabel.setVisible(isArtist);
        guessField.setVisible(!isArtist);
        guessBtn.setVisible(!isArtist);

    }

    public void setSecretWord(String word) {
        secretWordLabel.setText("Palabra: " + word);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (Component c : getComponents()) {
            // No deshabilitar el campo de adivinanza ni el botón
            if (c == guessField || c == guessBtn) {
                continue;
            }
            c.setEnabled(enabled);
        }
    }

    public void startTimer(int durationSeconds) {
        // detener cualquier timer previo
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }

        remainingSeconds = durationSeconds;
        setTime(remainingSeconds); // inicializa la etiqueta

        countdownTimer = new Timer(1000, e -> {
            remainingSeconds--;
            setTime(remainingSeconds);

            if (remainingSeconds <= 0) {
                countdownTimer.stop();
                tiempo.setText("00:00");
            }
        });
        countdownTimer.start();
    }

}
