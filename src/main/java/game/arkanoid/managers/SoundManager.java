package game.arkanoid.managers; // Đảm bảo đúng package

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

/**
 * Quản lý âm thanh
 * Hỗ trợ điều chỉnh âm lượng (Slider 0.0 - 1.0) và áp dụng Singleton.
 */
public class SoundManager {

    // === 1. THÊM INSTANCE CHO SINGLETON ===
    private static SoundManager instance;
    // ======================================

    private HashMap<String, AudioClip> soundEffects;
    private MediaPlayer backgroundMusicPlayer;

    private double musicVolume = 1.0; // Mặc định 100%
    private double sfxVolume = 1.0;   // Mặc định 100%


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

//    // === HÀM HELPER ĐỂ LẤY ĐƯỜNG DẪN CLASSPATH ===
//    private String getResourceUrl(String classpathPath) {
//        try {
//            // Lấy URL tài nguyên từ classpath
//            URL resourceUrl = getClass().getResource(classpathPath);
//            if (resourceUrl == null) {
//                throw new IOException("Không tìm thấy tài nguyên: " + classpathPath);
//            }
//            return resourceUrl.toExternalForm();
//        } catch (Exception e) {
//            System.err.println("Lỗi nghiêm trọng khi tải tài nguyên: " + classpathPath);
//            e.printStackTrace();
//            return null;
//        }
//    }

    /**
     * Tải trước một hiệu ứng âm thanh (SFX)
     */
    public void loadSoundEffect(String id, String filePath) {
        try {
            String uriString = new File(filePath).toURI().toString();
            AudioClip clip = new AudioClip(uriString);
            clip.setVolume(this.sfxVolume);
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
        if (this.sfxVolume == 0.0) {
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

        if (this.musicVolume == 0.0) {
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

    public void pauseBackgroundMusic() {
        if (backgroundMusicPlayer != null &&
                backgroundMusicPlayer.getStatus() == MediaPlayer.Status.PLAYING)
        {
            backgroundMusicPlayer.pause();
        }
    }

    public void resumeBackgroundMusic() {
        // Chỉ tiếp tục nếu nhạc đang được bật (âm lượng > 0)
        // và trình phát nhạc đang ở trạng thái TẠM DỪNG
        if (musicVolume > 0 &&
                backgroundMusicPlayer != null &&
                backgroundMusicPlayer.getStatus() == MediaPlayer.Status.PAUSED)
        {
            backgroundMusicPlayer.play();
        }
    }

    /**
     * Điều chỉnh âm lượng nhạc nền (Music).
     * @param volume Giá trị từ 0.0 (tắt) đến 1.0 (tối đa).
     */
    public void setMusicVolume(double volume) {
        // Giới hạn giá trị trong khoảng 0.0 - 1.0
        this.musicVolume = Math.max(0.0, Math.min(1.0, volume));

        if (backgroundMusicPlayer != null) {
            // Cập nhật âm lượng ngay lập tức
            backgroundMusicPlayer.setVolume(this.musicVolume);
        }

        // Nếu người dùng kéo từ 0 lên và có nhạc đang chờ
        if (this.musicVolume > 0 && backgroundMusicPlayer == null && currentMusicPath != null) {
            playBackgroundMusic(currentMusicPath, currentMusicLoop);
        }
        // Nếu người dùng kéo về 0
        else if (this.musicVolume == 0.0) {
            stopBackgroundMusic();
        }
    }

    /**
     * Điều chỉnh âm lượng hiệu ứng (SFX).
     * @param volume Giá trị từ 0.0 (tắt) đến 1.0 (tối đa).
     */
    public void setSfxVolume(double volume) {
        this.sfxVolume = Math.max(0.0, Math.min(1.0, volume));

        // Cập nhật âm lượng cho TẤT CẢ các clip đã tải
        for (AudioClip clip : soundEffects.values()) {
            clip.setVolume(this.sfxVolume);
        }
    }

    /**
     * Lấy mức âm lượng nhạc hiện tại.
     * @return Âm lượng (0.0 - 1.0)
     */
    public double getMusicVolume() {
        return this.musicVolume;
    }

    /**
     * Lấy mức âm lượng SFX hiện tại.
     * @return Âm lượng (0.0 - 1.0)
     */
    public double getSfxVolume() {
        return this.sfxVolume;
    }
}