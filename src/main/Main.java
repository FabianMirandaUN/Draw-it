/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import Gráfico.AudioLoop;
import visual.Inicio;

/**
 *
 * @author fvarelo
 */
public class Main {

    public static void main(String args[]) {

        // Iniciar música en bucle
        AudioLoop.iniciarMusica("/Gráfico/Música.wav");

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Inicio().setVisible(true);
            }
        });
    }
}
