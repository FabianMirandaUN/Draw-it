package server;

import common.Config;
import common.WordBank;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JTextArea;

public class GameServer {

    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final GameState state = new GameState();
    private volatile boolean running = false;
    public JTextArea jugadores;

    //  referencia al hilo del timer
    private Thread roundTimerThread;

    public GameServer(JTextArea jugadores) {
        this.jugadores = jugadores;
    }

    public void start(int port) throws IOException {
        // Inicia el servidor, abre el puerto y lanza un hilo dedicado a aceptar nuevas conexiones.
        serverSocket = new ServerSocket(port);
        running = true;
        System.out.println("Servidor iniciado en puerto " + port);
        new Thread(this::acceptLoop, "AcceptLoop").start();
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                // Cada vez que un cliente se conecta, se crea un ClientHandler y se ejecuta en un hilo independiente.
                ClientHandler handler = new ClientHandler(clientSocket, this, jugadores);
                clients.add(handler);
                new Thread(handler).start();
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException ignored) {
        }
        for (ClientHandler ch : clients) {
            ch.close();
        }
        clients.clear();

        // detener timer si existe
        if (roundTimerThread != null && roundTimerThread.isAlive()) {
            roundTimerThread.interrupt();
        }
    }

    public List<ClientHandler> getClients() {
        return clients;
    }

    public void remove(ClientHandler ch) {
        clients.remove(ch);
        broadcastPlayersAndScores();
        if (clients.size() < 2) {
            state.setRoundActive(false);
        }
    }

    public void broadcast(Map<String, Object> msg) {
        for (ClientHandler ch : clients) {
            ch.send(msg);
        }
    }

    public void broadcastPlayersAndScores() {
        broadcast(Map.of("type", "PLAYERS",
                "list", clients.stream()
                        .map(ClientHandler::getName)
                        .filter(name -> name != null && !name.isBlank())
                        .toList()));
        broadcast(Map.of("type", "SCORES",
                "players", clients.stream()
                        .map(ClientHandler::snapshot)
                        .toList()));
    }

    public void iniciarPartidaManual() {
        List<ClientHandler> jugadoresValidos = clients.stream()
                .filter(ch -> ch.getName() != null && !ch.getName().isBlank())
                .toList();

        if (jugadoresValidos.isEmpty()) {
            System.out.println("No hay jugadores con nombre para iniciar.");
            return;
        }

        // detener timer anterior si existe
        if (roundTimerThread != null && roundTimerThread.isAlive()) {
            roundTimerThread.interrupt();
        }

        state.setRoundActive(true);
        state.incrementRound();
// Selecciona aleatoriamente al jugador que será el artista y genera la palabra secreta de la ronda.

        int idx = new Random().nextInt(jugadoresValidos.size());
        ClientHandler artist = jugadoresValidos.get(idx);
        state.setArtist(artist.getName());
        String word = WordBank.randomWord();
        state.setSecretWord(word);
// Hilo del servidor encargado de enviar cada segundo el tiempo restante a todos los clientes.

        broadcast(Map.of("type", "START",
                "artist", state.getArtist(),
                "round", state.getRound(),
                "duration", Config.ROUND_SECONDS));

        artist.send(Map.of("type", "WORD", "value", word));
        for (ClientHandler ch : clients) {
            ch.setArtist(ch == artist);
        }

        // lanzar nuevo timer y guardar referencia
        roundTimerThread = new Thread(this::runTimer, "RoundTimer");
        roundTimerThread.start();

        System.out.println("[Server] Iniciando ronda: " + state.getRound());
        System.out.println("[Server] Artista elegido: " + state.getArtist());
        System.out.println("[Server] Palabra: " + word);

    }

    public void finalizarPartida() {
        if (!state.isRoundActive()) {
            System.out.println("No hay partida activa para finalizar.");
            return;
        }

        state.setRoundActive(false);
        state.setSecretWord("");
        state.setArtist(null);

        // detener timer
        if (roundTimerThread != null && roundTimerThread.isAlive()) {
            roundTimerThread.interrupt();
        }

        broadcast(Map.of("type", "ROUND_END", "result", "ended"));
        broadcast(Map.of("type", "CLEAR"));

        System.out.println("Partida finalizada manualmente.");
    }

    private void runTimer() {
        int remaining = Config.ROUND_SECONDS;
        while (state.isRoundActive() && remaining >= 0) {
            broadcast(Map.of("type", "TIME", "remaining", remaining));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // salir si el hilo fue interrumpido
                return;
            }
            remaining--;
        }
        if (state.isRoundActive()) {
            state.setRoundActive(false);
            broadcast(Map.of("type", "ROUND_END",
                    "result", "timeout",
                    "word", state.getSecretWord()));
            scheduleNextRound();
        }
    }

    private void scheduleNextRound() {
        new Thread(() -> {
            try {
                // Espera 3 segundos antes de iniciar automáticamente la siguiente ronda, siempre que haya suficientes jugadores.
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
            if (clients.size() >= 2) {
                iniciarPartidaManual();
            }
        }, "NextRoundScheduler").start();
    }

    public synchronized void verifyGuess(String guess, ClientHandler sender) {
        if (!state.isRoundActive() || guess == null) {
            return;
        }

        if (guess.trim().equalsIgnoreCase(state.getSecretWord())) {
            state.setRoundActive(false);
            // Si el jugador adivina correctamente, se detiene la ronda, se asigna el punto y se notifica a todos los clientes.
            sender.incrementScore();

            // detener timer
            if (roundTimerThread != null && roundTimerThread.isAlive()) {
                roundTimerThread.interrupt();
            }

            broadcast(Map.of("type", "WINNER",
                    "player", sender.getName(),
                    "word", state.getSecretWord(),
                    "scores", clients.stream().map(ClientHandler::snapshot).toList()));

            broadcast(Map.of("type", "ROUND_END",
                    "result", "win",
                    "winner", sender.getName()));

            scheduleNextRound();
        } else {
            sender.incrementAttempts();
            broadcast(Map.of("type", "ATTEMPT",
                    "player", sender.getName(),
                    "count", sender.getAttempts()));
        }
    }

    public void players() {
        for (ClientHandler client : this.clients) {
            System.out.println(client.getName());
        }
    }
}
