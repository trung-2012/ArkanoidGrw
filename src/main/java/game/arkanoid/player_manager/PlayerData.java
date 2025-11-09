package game.arkanoid.player_manager;

import java.io.*;
import java.util.ArrayList;

public class PlayerData {

    private static final String FILE_PATH = "players.dat";

    public static ArrayList<Player> loadPlayers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (ArrayList<Player>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static void savePlayers(ArrayList<Player> players) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(players);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
