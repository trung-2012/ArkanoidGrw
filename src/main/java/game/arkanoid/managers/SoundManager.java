package game.arkanoid.managers; // Đảm bảo đúng package

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.util.HashMap;

/**
 * Quản lý âm thanh cho ứng dụng JavaFX (Nâng cấp)
 * Hỗ trợ bật/tắt (Toggle) và áp dụng mẫu Singleton.
 */
public class SoundManager {

    // === 1. THÊM INSTANCE CHO SINGLETON ===
    private static SoundManager instance;
    // ======================================

    private HashMap<String, AudioClip> soundEffects;
    private MediaPlayer backgroundMusicPlayer;

    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;

    private String currentMusicPath = null;
    private boolean currentMusicLoop = false;

    // === 2. CHUYỂN CONSTRUCTOR THÀNH PRIVATE ===
    private SoundManager() {
        soundEffects = new HashMap<>();
    }
    // ========================================

    // === 3. THÊM PHƯƠNG THỨC LẤY INSTANCE ===
    /**
     * Lấy instance duy nhất của SoundManager.
     * @return Instance của SoundManager
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    // =======================================

    /**
     * Tải trước một hiệu ứng âm thanh (SFX)
     */
    public void loadSoundEffect(String id, String filePath) {
        try {
            String uriString = new File(filePath).toURI().toString();
            AudioClip clip = new AudioClip(uriString);
            soundEffects.put(id, clip);
        } catch (Exception e) {
            System.err.println("Không thể tải hiệu ứng âm thanh: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Phát một hiệu ứng âm thanh (SFX)
     */
    public void playSoundEffect(String id) {
        if (!sfxEnabled) {
            return;
        }
        AudioClip clip = soundEffects.get(id);
        if (clip != null) {
            clip.play();
        } else {
            System.err.println("Không tìm thấy hiệu ứng âm thanh với ID: " + id);
        }
    }

    /**
     * Bắt đầu phát nhạc nền (Music)
     */
    public void playBackgroundMusic(String filePath, boolean loop) {
        this.currentMusicPath = filePath;
        this.currentMusicLoop = loop;

        if (!musicEnabled) {
            return;
        }

        try {
            stopBackgroundMusic(); // Dừng nhạc cũ
            String uriString = new File(filePath).toURI().toString();
            Media media = new Media(uriString);
            backgroundMusicPlayer = new MediaPlayer(media);
            if (loop) {
                backgroundMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
            backgroundMusicPlayer.play();
        } catch (Exception e) {
            System.err.println("Lỗi khi phát nhạc nền: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Dừng phát nhạc nền.
     */
    public void stopBackgroundMusic() {
        if (backgroundMusicPlayer != null) {
            backgroundMusicPlayer.stop();
            backgroundMusicPlayer.dispose();
            backgroundMusicPlayer = null;
        }
    }

    /**
     * Bật hoặc tắt nhạc nền (Music).
     * @param enabled true để bật, false để tắt.
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (enabled) {
            if (backgroundMusicPlayer == null && currentMusicPath != null) {
                playBackgroundMusic(currentMusicPath, currentMusicLoop);
            }
        } else {
            stopBackgroundMusic();
        }
    }

    /**
     * Bật hoặc tắt hiệu ứng âm thanh (SFX).
     * @param enabled true để bật, false để tắt.
     */
    public void setSfxEnabled(boolean enabled) {
        this.sfxEnabled = enabled;
    }

    /**
     * Lấy trạng thái hiện tại của nhạc nền.
     * @return true nếu nhạc đang bật.
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Lấy trạng thái hiện tại của hiệu ứng âm thanh.
     * @return true nếu SFX đang bật.
     */
    public boolean isSfxEnabled() {
        return sfxEnabled;
    }
}