/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

/**
 *
 * @author FabiFree
 */

import java.util.List;
import java.util.Random;

public class WordBank {
    private static final List<String> WORDS = List.of("casa","sol","perro","flor","carro","gato","árbol","libro","río","montaña");

    public static String randomWord() {
        return WORDS.get(new Random().nextInt(WORDS.size()));
    }
}
