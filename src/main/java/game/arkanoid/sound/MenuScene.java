import game.arkanoid.sound.SoundManager;

public class MenuScene {
    public void show() {
        SoundManager.playMenuMusic();
    }

    public void startGame() {
        SoundManager.stopMenuMusic();   // dừng nhạc menu
        SoundManager.playGameMusic();   // bật nhạc gameplay
    }
}
