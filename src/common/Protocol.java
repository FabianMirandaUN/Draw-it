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

public class Protocol {
    private static final Gson gson = new Gson();

    public static String encode(Map<String, Object> msg) {
        return gson.toJson(msg);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> decode(String s) {
        return gson.fromJson(s, Map.class);
    }
}

