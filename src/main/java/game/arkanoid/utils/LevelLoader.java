package game.arkanoid.utils;

import game.arkanoid.models.Brick;
import game.arkanoid.models.BrickType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LevelLoader {

    // Load level từ file txt.
    public static List<Brick> loadLevel(String levelFile) {
        List<Brick> bricks = new ArrayList<>();

        try (InputStream is = LevelLoader.class.getResourceAsStream("/game/arkanoid/levels/" + levelFile);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            List<String> lines = new ArrayList<>(); // can chinh phu hop voi man hinh
            String rawLine;
            while ((rawLine = br.readLine()) != null) {
                if (!rawLine.trim().isEmpty()) {
                    lines.add(rawLine.trim());
                }
            }

            // Thông số gạch
            int bw = GameConstants.BRICK_WIDTH;
            int bh = GameConstants.BRICK_HEIGHT;

            int maxCols = lines.stream().mapToInt(String::length).max().orElse(0);

            int totalWidth = maxCols * bw;
            int startX = (GameConstants.WINDOW_WIDTH - totalWidth) / 2;
            int startY = 50;

            int row = 0;
            for (String line : lines) {
                for (int col = 0; col < line.length(); col++) {
                    char c = line.charAt(col);
                    BrickType type = charToBrickType(c);
                    if (type != null) {
                        double x = startX + col * bw;
                        double y = startY + row * bh;
                        bricks.add(new Brick(type, new Vector2D(x, y)));
                    }
                }
                row++;
            }

        } catch (Exception e) {
            System.err.println("Không thể load level hiện tại: " + levelFile);
            e.printStackTrace();
        }

        return bricks;
    }

    // Phân loại gạch dựa trên các ký tự trong file txt
    private static BrickType charToBrickType(char c) {
        switch (c) {
            case '1':
                return BrickType.NORMAL;
            case '2':
                return BrickType.WOOD;
            case '3':
                return BrickType.IRON;
            case '4':
                return BrickType.GOLD;
            case '5':
                return BrickType.EXPLODE;
            case '9':
                return BrickType.INSANE;
            case '0':
            default:
                return null;
        }
    }
}
