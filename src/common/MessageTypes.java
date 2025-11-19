/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package common;

/**
 *
 * @author FabiFree
 */

/**
 * Esta clase contiene todas las constantes que representan los tipos de mensajes
 * que pueden intercambiar el cliente y el servidor durante la partida.
 */

public final class MessageTypes {
    public static final String HELLO = "HELLO";
    public static final String START = "START";
    public static final String WORD = "WORD";
    public static final String DRAW = "DRAW";
    public static final String CLEAR = "CLEAR";
    public static final String GUESS = "GUESS";
    public static final String TIME = "TIME";
    public static final String WINNER = "WINNER";
    public static final String ROUND_END = "ROUND_END";
    public static final String SCORES = "SCORES";
    public static final String PLAYERS = "PLAYERS";
    public static final String JOIN = "JOIN";
    public static final String ATTEMPT = "ATTEMPT";
    private MessageTypes() {}
}
