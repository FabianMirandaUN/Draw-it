
import java.net.*;
import java.io.*;
import java.util.*;

public class Servidor {
    private static List<ClienteHandler> clientes = new ArrayList<>();
    private static int liderId = -1;
    private static String palabraSecreta = "";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(Config.SERVER_PORT);
        System.out.println("Servidor iniciado en puerto " + Config.SERVER_PORT);

        while (true) {
            Socket socket = serverSocket.accept();
            ClienteHandler handler = new ClienteHandler(socket, clientes.size());
            clientes.add(handler);
            new Thread(handler).start();

            if (clientes.size() >= 2 && liderId == -1) {
                liderId = new Random().nextInt(clientes.size());
                palabraSecreta = Palabras.obtenerPalabraAleatoria();
                clientes.get(liderId).enviar("LIDER:" + palabraSecreta);
                for (int i = 0; i < clientes.size(); i++) {
                    if (i != liderId) clientes.get(i).enviar("ESPECTADOR");
                }
            }
        }
    }

    public static void transmitirDibujo(String datos) {
        for (int i = 0; i < clientes.size(); i++) {
            if (i != liderId) clientes.get(i).enviar("DIBUJO:" + datos);
        }
    }

    public static void verificarPalabra(String intento, int clienteId) {
        if (intento.equalsIgnoreCase(palabraSecreta)) {
            for (ClienteHandler ch : clientes) {
                ch.enviar("GANADOR:Jugador " + clienteId);
            }
        }
    }
}
