
import java.net.*;
import java.io.*;

public class ClienteHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int id;

    public ClienteHandler(Socket socket, int id) {
        this.socket = socket;
        this.id = id;
    }

    public void enviar(String mensaje) {
        out.println(mensaje);
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("ID:" + id);

            String input;
            while ((input = in.readLine()) != null) {
                if (input.startsWith("DIBUJO:")) {
                    Servidor.transmitirDibujo(input.substring(7));
                } else if (input.startsWith("INTENTO:")) {
                    Servidor.verificarPalabra(input.substring(8), id);
                }
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado: " + id);
        }
    }
}
