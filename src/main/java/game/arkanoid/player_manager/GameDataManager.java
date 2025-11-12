package game.arkanoid.player_manager;

import java.io.*;

/**
 * Class quản lý việc lưu và load game data.
 * Tương tự PlayerData, sử dụng ObjectOutputStream/ObjectInputStream.
 */
public class GameDataManager {
    
    private static final String FILE_PATH = "gamesave.dat";
    
    /**
     * Load game save data từ file.
     * 
     * @return GameSaveData nếu có file save, null nếu không có
     */
    public static GameSaveData loadGameSave() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            Object obj = ois.readObject();
            if (obj instanceof GameSaveData) {
                System.out.println("Game save loaded successfully.");
                return (GameSaveData) obj;
            } else {
                System.out.println("Invalid save file format.");
                return null;
            }
        } catch (FileNotFoundException e) {
            System.out.println("No save file found. Starting new game.");
            return null;
        } catch (Exception e) {
            System.out.println("Error loading save file: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lưu game save data vào file.
     * 
     * @param saveData GameSaveData cần lưu
     */
    public static void saveGameSave(GameSaveData saveData) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(saveData);
            System.out.println("Game saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Xóa file save game.
     */
    public static void deleteSaveGame() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Save file deleted.");
            } else {
                System.err.println("Failed to delete save file.");
            }
        }
    }
    
    /**
     * Kiểm tra xem có file save game hay không.
     * 
     * @return true nếu có file save, false nếu không
     */
    public static boolean hasSaveGame() {
        File file = new File(FILE_PATH);
        return file.exists();
    }
}
