/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import Gráfico.AudioLoop;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;
import visual.Inicio;

/**
 *
 * @author fvarelo
 */
public class Main {

    public static void main(String args[]) {
        System.setProperty("flatlaf.useNativeLibrary", "false");
// Iniciar música en bucle
        AudioLoop.iniciarMusica("/Gráfico/Música.wav");
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Inicio().setVisible(true);
            }
        });
    }
}
