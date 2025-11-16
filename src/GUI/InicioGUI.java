/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

/**
 *
 * @author FabiFree
 */
import client.GameClient;
import client.GameServer2;
import server.GameServer;
import common.Config;

import javax.swing.*;
import java.awt.*;

public class InicioGUI extends JFrame {

    private final JTextField nameField = new JTextField("Jugador");
    GameServer server;

    public InicioGUI() {
        setTitle("Draw It - Inicio");
        setSize(420, 260);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel title = new JLabel("BIENVENID@", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(60, 20, 300, 40);
        add(title);

        nameField.setBounds(110, 75, 200, 28);
        add(nameField);

        JButton createBtn = new JButton("Crear partida");
        createBtn.setBounds(110, 115, 200, 35);
        add(createBtn);

        JButton joinBtn = new JButton("Buscar partida");
        joinBtn.setBounds(110, 160, 200, 35);
        add(joinBtn);

        createBtn.addActionListener(e -> crearPartida());
        joinBtn.addActionListener(e -> buscarPartida());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void crearPartida() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            name = "Jugador";
        }

        // Arrancar servidor
        new Thread(() -> {
            try {
                server = new GameServer();
                server.start(Config.SERVER_PORT);
            } catch (Exception ex) {
                SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(this, "Error al iniciar servidor"));
                ex.printStackTrace();
            }
        }, "ServerMain").start();

        // Esperar y conectar cliente
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
        try {
            GameServer2 client = new GameServer2(name, server);
            client.connect(Config.SERVER_IP, Config.SERVER_PORT);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor");
        }
    }

    private void buscarPartida() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            name = "Jugador";
        }

        String ip = JOptionPane.showInputDialog(this, "IP del servidor:", Config.SERVER_IP);
        if (ip == null || ip.isBlank()) {
            return;
        }
        try {
            GameClient client = new GameClient(name);
            client.connect(ip, Config.SERVER_PORT);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor");
        }
    }

    public static void main(String[] args) {
        new InicioGUI();
    }
}
