/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Gráfico;

/**
 *
 * @author FabiFree
 */
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioLoop {

    private static Clip clip;

    public static void iniciarMusica(String ruta) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(
                    AudioLoop.class.getResource(ruta)
            );
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // 🔁 Repetir en bucle
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
