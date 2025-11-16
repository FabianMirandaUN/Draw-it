/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

/**
 *
 * @author FabiFree
 */

public class GameState {
    private String secretWord;
    private String artist;
    private int round = 0;
    private boolean roundActive = false;

    public String getSecretWord() { return secretWord; }
    public void setSecretWord(String secretWord) { this.secretWord = secretWord; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public int getRound() { return round; }
    public void incrementRound() { this.round++; }

    public boolean isRoundActive() { return roundActive; }
    public void setRoundActive(boolean roundActive) { this.roundActive = roundActive; }
}

