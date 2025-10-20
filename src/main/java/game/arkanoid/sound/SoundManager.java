package game.arkanoid.sound;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Objects;

public class SoundManager {
    private static MediaPlayer menuMusic;
    private static MediaPlayer gameMusic;

    // =====================
    // 🎵 NHẠC NỀN
    // =====================

    public static void playMenuMusic() {
        if (menuMusic == null) {
            menuMusic = new MediaPlayer(new Media(Objects.requireNonNull(
                    SoundManager.class.getResource("/game/arkanoid/sounds/menu_music.mp3")).toString()));
            menuMusic.setCycleCount(MediaPlayer.INDEFINITE);
        }
        menuMusic.play();
    }

    public static void stopMenuMusic() {
        if (menuMusic != null) menuMusic.stop();
    }

    public static void playGameMusic() {
        if (gameMusic == null) {
            gameMusic = new MediaPlayer(new Media(Objects.requireNonNull(
                    SoundManager.class.getResource("/game/arkanoid/sounds/gameplay_music.mp3")).toString()));
            gameMusic.setCycleCount(MediaPlayer.INDEFINITE);
        }
        gameMusic.play();
    }

    public static void stopGameMusic() {
        if (gameMusic != null) gameMusic.stop();
    }

    // =====================
    // 🎯 HIỆU ỨNG ÂM THANH
    // =====================

    public static void playHitPaddle() {
        playSound("/game/arkanoid/sounds/hit_paddle.wav");
    }

    public static void playBrickBreak() {
        playSound("/game/arkanoid/sounds/brick_break.wav");
    }

    public static void playLoseLife() {
        playSound("/game/arkanoid/sounds/lose_life.wav");
    }

    // =====================
    // ⚙️ HÀM CHUNG
    // =====================
    // Dùng để tránh lặp lại code khởi tạo MediaPlayer
    private static void playSound(String path) {
        try {
            MediaPlayer sfx = new MediaPlayer(new Media(Objects.requireNonNull(
                    SoundManager.class.getResource(path)).toString()));
            sfx.setVolume(0.8); // chỉnh âm lượng hiệu ứng nếu cần
            sfx.play();
        } catch (Exception e) {
            System.err.println("⚠️ Không thể phát âm thanh: " + path);
            e.printStackTrace();
        }
    }
}
