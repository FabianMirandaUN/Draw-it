/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

/**
 *
 * @author FabiFree
 */
import common.Protocol;
import java.io.*;
import java.net.Socket;
import java.util.Map;
import javax.swing.JTextArea;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final GameServer server;
    private BufferedReader in;
    private PrintWriter out;
    private String playerName;
    private boolean isArtist = false;
    private int score = 0;
    private int attempts = 0;
    private JTextArea jugadores;

    public ClientHandler(Socket socket, GameServer server, JTextArea jugadores) {
        this.socket = socket;
        this.server = server;
        this.jugadores = jugadores;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return playerName; // devuelve null si aún no hay nombre
    }

    public void setArtist(boolean artist) {
        this.isArtist = artist;
    }

    public void incrementScore() {
        score++;
    }

    public void incrementAttempts() {
        attempts++;
    }

    public int getAttempts() {
        return attempts;
    }

    public Map<String, Object> snapshot() {
        return Map.of("player", getName() != null ? getName() : "(sin nombre)", "score", score);
    }

    public void send(Map<String, Object> msg) {
        if (out != null) {
            out.println(Protocol.encode(msg));
        }
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                Map<String, Object> msg = Protocol.decode(line);
                String type = (String) msg.get("type");
                if (type == null) {
                    continue;
                }
                switch (type) {
                    case "JOIN" -> {
                        playerName = (String) msg.get("player");
                        System.out.println("[Server] JOIN recibido de: " + playerName);
                        server.broadcastPlayersAndScores();
                        jugadores.setText("");
                        if (server.getClients().isEmpty()) {
                            jugadores.setText("No hay jugadores conectados.");
                            return;
                        }
                        for (ClientHandler client : server.getClients()) {
                            jugadores.append(client.getName() + "\n");

                        }
                    }
                    case "START_REQUEST" -> {
                        System.out.println("[Server] START_REQUEST recibido");
                        server.iniciarPartidaManual();
                    }
                    case "END_REQUEST" -> {
                        System.out.println("[Server] END_REQUEST recibido");
                        server.finalizarPartida();
                    }
                    case "DRAW" -> {
                        System.out.println("[Server] DRAW de " + playerName);
                        server.broadcast(msg);
                    }
                    case "CLEAR" -> {
                        System.out.println("[Server] CLEAR de " + playerName);
                        server.broadcast(msg);
                    }
                    case "GUESS" -> {
                        System.out.println("[Server] GUESS de " + playerName + ": " + msg.get("value"));
                        server.verifyGuess((String) msg.get("value"), this);
                    }
                    default ->
                        System.out.println("[Server] Mensaje desconocido: " + msg);
                }

            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado: " + playerName);
        } finally {
            server.remove(this);
            close();
        }
    }
}
