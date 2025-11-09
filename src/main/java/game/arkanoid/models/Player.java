package game.arkanoid.models;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;
    private String nickname;
    private int highScore;

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.highScore = 0;
        this.nickname = null;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

}
