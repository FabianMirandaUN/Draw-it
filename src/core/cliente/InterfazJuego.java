
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.ArrayList;

public class InterfazJuego extends JPanel {
    private PrintWriter out;
    private boolean esLider = false;
    private String palabra = "";
    private JTextField campoTexto;
    private ArrayList<String> trazos = new ArrayList<>();

    public InterfazJuego(PrintWriter out) {
        this.out = out;
        setLayout(null);

        campoTexto = new JTextField();
        campoTexto.setBounds(10, 10, 200, 30);
        add(campoTexto);

        JButton enviar = new JButton("Adivinar");
        enviar.setBounds(220, 10, 100, 30);
        add(enviar);

        enviar.addActionListener(e -> {
            out.println("INTENTO:" + campoTexto.getText());
            campoTexto.setText("");
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (esLider) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        out.println("DIBUJO:" + x + "," + y + ",NEGRO");
                        trazos.add(x + "," + y + ",NEGRO");
                    } else if (SwingUtilities.isRightMouseButton(e)) {
                        out.println("DIBUJO:" + x + "," + y + ",BLANCO");
                        trazos.add(x + "," + y + ",BLANCO");
                    }
                    repaint();
                }
            }
        });
    }

    public void procesarMensaje(String msg) {
        if (msg.startsWith("ID:")) {
            // Ignorar
        } else if (msg.startsWith("LIDER:")) {
            esLider = true;
            palabra = msg.substring(6);
        } else if (msg.startsWith("ESPECTADOR")) {
            esLider = false;
        } else if (msg.startsWith("DIBUJO:")) {
            trazos.add(msg.substring(7));
            repaint();
        } else if (msg.startsWith("GANADOR:")) {
            JOptionPane.showMessageDialog(this, msg);
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 50, getWidth(), getHeight());
        for (String trazo : trazos) {
            String[] partes = trazo.split(",");
            if (partes.length == 3) {
                int x = Integer.parseInt(partes[0]);
                int y = Integer.parseInt(partes[1]);
                String color = partes[2];
                if (color.equals("NEGRO")) {
                    g.setColor(Color.BLACK);
                    g.fillOval(x, y, 5, 5);
                } else if (color.equals("BLANCO")) {
                    g.setColor(Color.WHITE);
                    g.fillRect(x, y, 10, 10);
                }
            }
        }
        if (esLider) {
            g.setColor(Color.RED);
            g.drawString("Palabra: " + palabra, 10, 100);
        }
    }
}
