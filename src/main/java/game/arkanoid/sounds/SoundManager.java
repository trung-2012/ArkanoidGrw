package game.arkanoid.sound;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.application.Platform;
import java.net.URL;

public class SoundManager {
    private static MediaPlayer menuMusic;
    private static MediaPlayer gameMusic;
    private static AudioClip hitPaddleSound;
    private static AudioClip brickBreakSound;

    public static void init() {
        menuMusic = createMediaPlayer("/game/arkanoid/sounds/menu.mp3", true);
        gameMusic = createMediaPlayer("/game/arkanoid/sounds/gameplay_music.mp3", true);
        hitPaddleSound = createAudioClip("/game/arkanoid/sounds/hit_paddle.wav");
        brickBreakSound = createAudioClip("/game/arkanoid/sounds/brick_break.wav");
    }

    private static MediaPlayer createMediaPlayer(String path, boolean loop) {
        URL resource = SoundManager.class.getResource(path);
        if (resource == null) {
            System.out.println("File not found: " + path);
            return null;
        }
        Media media = new Media(resource.toString());
        MediaPlayer player = new MediaPlayer(media);
        player.setVolume(0.5);
        if (loop) player.setCycleCount(MediaPlayer.INDEFINITE);
        player.setOnReady(() -> System.out.println("Ready: " + path));
        player.setOnError(() -> System.out.println("Media error: " + player.getError()));
        return player;
    }

    private static AudioClip createAudioClip(String path) {
        URL resource = SoundManager.class.getResource(path);
        if (resource == null) {
            System.out.println("AudioClip not found: " + path);
            return null;
        }
        return new AudioClip(resource.toString());
    }

    public static void playMenuMusic() {
        stopAll();
        if (menuMusic != null) {
            Platform.runLater(() -> {
                System.out.println("Playing menu music...");
                menuMusic.play();
            });
        }
    }

    public static void playGameMusic() {
        stopAll();
        if (gameMusic != null) {
            Platform.runLater(() -> {
                System.out.println("Playing game music...");
                gameMusic.play();
            });
        }
    }

    public static void playHitPaddle() {
        if (hitPaddleSound != null) hitPaddleSound.play();
    }

    public static void playBrickBreak() {
        if (brickBreakSound != null) brickBreakSound.play();
    }

    public static void stopAll() {
        if (menuMusic != null) menuMusic.stop();
        if (gameMusic != null) gameMusic.stop();
    }
}
