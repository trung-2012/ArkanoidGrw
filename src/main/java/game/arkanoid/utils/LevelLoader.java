package game.arkanoid.utils;

import game.arkanoid.models.*;

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

            List<String> lines = new ArrayList<>();
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
                    Brick brick = createBrickFromChar(c, startX + col * bw, startY + row * bh);
                    if (brick != null) {
                        bricks.add(brick);
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

    /**
     * Factory method - Tạo Brick subclass dựa trên ký tự
     * Áp dụng Polymorphism thay vì BrickType enum
     */
    private static Brick createBrickFromChar(char c, double x, double y) {
        Vector2D position = new Vector2D(x, y);
        
        switch (c) {
            case '1':
                return new NormalBrick(position);
            case '2':
                return new WoodBrick(position);
            case '3':
                return new IronBrick(position);
            case '4':
                return new GoldBrick(position);
            case '5':
                return new ExplodeBrick(position);
            case '9':
                return new InsaneBrick(position);
            case '0':
            default:
                return null; // Khoảng trống
        }
    }
}
