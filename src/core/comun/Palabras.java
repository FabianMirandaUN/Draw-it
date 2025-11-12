
import java.util.*;

public class Palabras {
    private static final String[] palabras = {"casa", "sol", "perro", "flor", "carro", "gato"};

    public static String obtenerPalabraAleatoria() {
        Random rand = new Random();
        return palabras[rand.nextInt(palabras.length)];
    }
}
