/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

/**
 *
 * @author FabiFree
 */
import com.google.gson.Gson;
import java.util.Map;
// Clase encargada de convertir mensajes entre Map <-> JSON para comunicación cliente-servidor.

public class Protocol {

    private static final Gson gson = new Gson();

    public static String encode(Map<String, Object> msg) {
        return gson.toJson(msg);
        // Convierte el mapa de datos en una cadena JSON para enviarlo por la red.

    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> decode(String s) {
        // Convierte una cadena JSON recibida desde la red nuevamente en un mapa.

        return gson.fromJson(s, Map.class);
    }
}
