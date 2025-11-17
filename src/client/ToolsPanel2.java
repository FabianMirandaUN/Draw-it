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

public class ToolsPanel2 extends JPanel {
    private final java.io.PrintWriter out;
    private Color currentColor = Color.BLACK;
    private boolean eraser = false;
    private int size = 8;

    public ToolsPanel2(java.io.PrintWriter out) {
        this.out = out;
        setLayout(new GridLayout(0,1));
        setPreferredSize(new Dimension(200, 500));

        JButton colorBtn = new JButton("Color");
        colorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "Elegir color", currentColor);
            if (c != null) currentColor = c;
        });

        JSlider sizeSlider = new JSlider(2, 30, size);
        sizeSlider.setPaintLabels(true);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setMajorTickSpacing(7);
        sizeSlider.addChangeListener(e -> size = sizeSlider.getValue());

        JToggleButton eraserBtn = new JToggleButton("Borrador");
        eraserBtn.addActionListener(e -> eraser = eraserBtn.isSelected());

        JButton clearBtn = new JButton("Limpiar");
        clearBtn.addActionListener(e -> out.println(Protocol.encode(Map.of("type","CLEAR"))));

        add(new JLabel("Herramientas", SwingConstants.CENTER));
        add(colorBtn);
        add(new JLabel("Grosor"));
        add(sizeSlider);
        add(eraserBtn);
        add(clearBtn);
    }

    public String getColorHex() {
        return String.format("#%02x%02x%02x", currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue());
    }
    public boolean isEraser() { return eraser; }
    public int getBrushSize() { return size; }


    @Override public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        for (Component c : getComponents()) c.setEnabled(enabled);
    }
}

  