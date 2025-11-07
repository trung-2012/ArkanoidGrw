package game.arkanoid.models;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;

    private int highScore;

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.highScore = 0;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    public int getHighScore() {
        return highScore;
    }

    public void updateHighScore(int score) {
        if (score > this.highScore) this.highScore = score;
    }
}
