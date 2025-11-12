
import java.net.*;
import java.io.*;
import javax.swing.*;

public class Cliente {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        JFrame frame = new JFrame("Pictionary Cliente");
        InterfazJuego panel = new InterfazJuego(out);
        frame.add(panel);
        frame.setSize(Config.WIDTH, Config.HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        String input;
        while ((input = in.readLine()) != null) {
            panel.procesarMensaje(input);
        }
    }
}
