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

public class GuessPanel extends JPanel {

    private final java.io.PrintWriter out; // 'out' es el canal de salida que envía mensajes al servidor usando el socket.
    private final String playerName;
    private final JTextField guessField = new JTextField(); // Campo de texto donde el jugador escribe su adivinanza.

    public GuessPanel(java.io.PrintWriter out, String playerName) {
        this.out = out;
        this.playerName = playerName;
        setLayout(new BorderLayout());
        JButton sendBtn = new JButton("Adivinar");
        sendBtn.addActionListener(e -> {
            String guessText = guessField.getText().trim();
            if (!guessText.isEmpty()) {
                out.println(Protocol.encode(Map.of("type", "GUESS", "value", guessText))); // Se arma el mensaje con el protocolo y se envía al servidor.

                guessField.setText("");
            }
        });

        add(guessField, BorderLayout.CENTER);
        add(sendBtn, BorderLayout.EAST);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (Component c : getComponents()) {
            // Deshabilita todos los componentes del panel (ej. cuando no es tu turno).
            c.setEnabled(enabled);
        }
    }
}
