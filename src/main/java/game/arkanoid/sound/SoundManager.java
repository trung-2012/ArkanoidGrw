package game.arkanoid.sound;

import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class SoundManager {

    private static MediaPlayer menuMusic;
    private static MediaPlayer gameMusic;
    private static AudioClip hitPaddleSound;
    private static AudioClip brickBreakSound;

    private static boolean soundEnabled = true;

    // trạng thái nhạc hiện tại
    private static boolean menuMusicPlaying = false;
    private static boolean gameMusicPlaying = false;

    // volume hiện tại (0.0 -> 1.0)
    private static double volume = 0.5;

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

        // apply default volume
        player.setVolume(volume);

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

        AudioClip clip = new AudioClip(resource.toString());
        clip.setVolume(volume);
        return clip;
    }

    // music
    public static void playMenuMusic() {
        if (!soundEnabled) return;
        if (menuMusicPlaying) return; // tránh reset lại nhạc
        stopAll();
        if (menuMusic != null) {
            Platform.runLater(() -> {
                System.out.println("Playing menu music...");
                menuMusic.setVolume(volume);
                menuMusic.play();
                menuMusicPlaying = true;
                gameMusicPlaying = false;
            });
        }
    }

    public static void playGameMusic() {
        if (!soundEnabled) return;
        if (gameMusicPlaying) return; // tránh restart khi đang phát
        stopAll();
        if (gameMusic != null) {
            Platform.runLater(() -> {
                System.out.println("Playing game music...");
                gameMusic.setVolume(volume);
                gameMusic.play();
                gameMusicPlaying = true;
                menuMusicPlaying = false;
            });
        }
    }

    public static void stopAll() {
        if (menuMusic != null) menuMusic.stop();
        if (gameMusic != null) gameMusic.stop();
        if (hitPaddleSound != null) hitPaddleSound.stop();
        if (brickBreakSound != null) brickBreakSound.stop();
        menuMusicPlaying = false;
        gameMusicPlaying = false;
    }

    public static void stopMenuMusic() {
        if (menuMusic != null) {
            menuMusic.stop();
            menuMusicPlaying = false;
        }
    }

    public static void stopGameMusic() {
        if (gameMusic != null) {
            gameMusic.stop();
            gameMusicPlaying = false;
        }
    }

    // effect
    public static void playHitPaddle() {
        if (!soundEnabled) return;
        if (hitPaddleSound != null) hitPaddleSound.play();
    }

    public static void playBrickBreak() {
        if (!soundEnabled) return;
        if (brickBreakSound != null) brickBreakSound.play();
    }

    // state
    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        if (!enabled) stopAll();
    }

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static boolean isMenuMusicPlaying() {
        return menuMusicPlaying;
    }

    public static boolean isGameMusicPlaying() {
        return gameMusicPlaying;
    }

    // Volume control được thêm vào
    public static void setVolume(double v) {
        volume = v;

        // apply ngay cho tất cả nguồn âm thanh
        if (menuMusic != null) menuMusic.setVolume(v);
        if (gameMusic != null) gameMusic.setVolume(v);
        if (hitPaddleSound != null) hitPaddleSound.setVolume(v);
        if (brickBreakSound != null) brickBreakSound.setVolume(v);
    }

    public static double getVolume() {
        return volume;
    }

    // Toggle sound (dùng trong settings)
    public static void toggleGameSound() {
        soundEnabled = !soundEnabled;
        if (soundEnabled) {
            // Nếu đang ở menu thì chỉ bật lại menu music
            if (!menuMusicPlaying && !gameMusicPlaying) {
                playMenuMusic();
            }
            System.out.println("Sound on");
        } else {
            stopAll();
            System.out.println("Sound off");
        }
    }
}
